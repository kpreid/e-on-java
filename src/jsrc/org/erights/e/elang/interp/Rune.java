// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.interp;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.develop.exception.ThrowableSugar;
import org.erights.e.develop.trace.TraceController;
import org.erights.e.elang.scope.Scope;
import org.erights.e.elib.debug.Profiler;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.Selector;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.Memoizer;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.vat.Vat;
import org.erights.e.meta.java.io.FileGetter;
import org.erights.e.meta.java.io.FileSugar;
import org.erights.e.meta.java.util.PropertiesSugar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;


/**
 * The main, non-interactive, E read-eval interpreter loop.
 *
 * @author Mark S. Miller
 */
public class Rune {

    static public final String SYN_PROPS_PATH_PREFIX =
      "org/erights/e/elang/syntax/syntax-props-";

    static public final String MAJOR_VERSION = "0.9";

    static public final String SYN_PROPS_PATH =
      SYN_PROPS_PATH_PREFIX + MAJOR_VERSION + ".txt";

    static public final String SYN_PROPS_EXPLAIN =
      "(see " + SYN_PROPS_PATH_PREFIX + "default.txt)";

    /**
     * prevent instantiation
     */
    private Rune() {
    }

    /**
     * Processes org/erights/e/elang/syntax/syntax-props-<major_version>.txt,
     * eprops.txt, ~/.e/user-eprops.txt, and initial "-Dprop=value" arguments
     * into the system properties.
     * <p/>
     * For use by this and other E-oriented main() methods. If there's no
     * eprops.txt file, then sets those properties that are necessary for
     * running the E installer script.
     * <p/>
     * Normally, all "-Dprop=value" options would occur to the left of the main
     * class name, and therefore be processed by the Java launcher before
     * launching the main class. However, this may be difficult using some IDEs
     * (like Cafe), so we also process such options ourselves.
     *
     * @return The remaining arguments after the initial "-Dprop=value"
     *         arguments have been consumed.
     */
    static public ConstList doProps(ConstList args) throws IOException {
        //noinspection AccessOfSystemProperties
        Properties sysProps = System.getProperties();
        while (1 <= args.size()) {
            String option = (String)args.get(0);
            if (!option.startsWith("-D")) {
                break;
            }
            int equals = option.indexOf('=');
            if (-1 == equals) {
                //Even though this is a probable error, if it is it will be
                //caught downstream, so we don't need to try to diagnose this
                //here.
                break;
            }
            String name = option.substring(2, equals);
            String value = option.substring(equals + 1);
            sysProps.setProperty(name, value);
            args = args.run(1, args.size());
        }

        Properties eprops = new Properties();

        ///////////////////
        //for bootstrapping, set some default properties needed to
        //run setup.e-awt, just in case no properties file is found.
        eprops.setProperty("e.safej.bind-var-to-propName", "true");

        /////////////////////
        //Load the syntax-props-<major_version> file.
        URL synPropsURL = (URL)ResourceUriGetter.THE_ONE.get(SYN_PROPS_PATH);
        PropertiesSugar.loadFromURL(eprops, synPropsURL);

        /////////////////////
        //Get the E installation's eprops.txt file or a reasonable substitiute
        String eHome = sysProps.getProperty("e.home");
        if (null == eHome) {
            eHome = ".";
        }
        File eHomeDir = (File)FileGetter.THE_ONE.get(eHome);
        File epropsFile = FileSugar.get(eHomeDir, "eprops.txt");
        if (!epropsFile.exists()) {
            // load default values.
            // This can happen prior to an initial installation
            epropsFile = FileSugar.get(eHomeDir, "eprops-template.txt");
        }
        if (epropsFile.exists()) {
            //This setting of e.home will probably be overridden by
            //loadFromFile, but if it isn't, then, since we did find an eprops
            //file here, this is a good guess.
            eprops.setProperty("e.home", FileSugar.getPath(eHomeDir));
            PropertiesSugar.loadFromFile(eprops, epropsFile);
        }

        //////////////////
        //Override with this user's user-eprops.txt file, if any
        File userEpropsFile =
          (File)FileGetter.THE_ONE.get("~/.e/user-eprops.txt");
        if (userEpropsFile.exists()) {
            PropertiesSugar.loadFromFile(eprops, userEpropsFile);
        }

        Enumeration iter = eprops.propertyNames();
        while (iter.hasMoreElements()) {
            String key = (String)iter.nextElement();
            if (!sysProps.containsKey(key)) {
                // don't override a "-Dprop=value" argument, which
                // unfortunately means we can't override any pre-existing
                // system property.
                String value = eprops.getProperty(key);
                if (!value.startsWith("${{")) {
                    // don't use an unset property hole
                    sysProps.setProperty(key, value);
                }

//Turns out, eprops.txt is too late to set java.library.path anyway, as
//the ClassLoader will have already cached the prior setting. Therefore,
//java.library.path has to get set by a -D on the java command line.
//          } else if ("java.library.path".equals(key)) {
//              sysProps.setProperty(key, eprops.getProperty(key));
            }
        }

        String optVersion = sysProps.getProperty("e.version");
        if (null == optVersion) {
            sysProps.setProperty("e.version", MAJOR_VERSION + ".??");
        } else {
            T.require(optVersion.startsWith(MAJOR_VERSION + "."),
                      "Inconsistent version: ",
                      E.toQuote(optVersion),
                      " vs. ",
                      E.toQuote(MAJOR_VERSION),
                      ".\n",
                      "See e/src/Makefile for the other version setting.");
        }

        //In order to avoid an upwards dependency, we do the
        //tilde-expansion for the trace package, since it doesn't know
        //how to do it for itself.
        String traceLogDir = sysProps.getProperty("TraceLog_dir", null);
        if (null != traceLogDir) {
            if (!"-".equals(traceLogDir)) {
                traceLogDir = FileGetter.normalize(traceLogDir);
            }
            sysProps.setProperty("TraceLog_dir", traceLogDir);
        }
        TraceController.start(sysProps);

        return args;
    }

    /**
     *
     */
    static public void reportProblem(Throwable t,
                                     TextWriter errs,
                                     boolean showsJStack,
                                     boolean showsEStack) {
        try {
            Throwable leaf = ThrowableSugar.leaf(t);

            errs.indent("# ").print("# ", leaf);
            if (showsJStack || showsEStack) {
                errs.lnPrint("#");
            }
            if (showsJStack) {
                errs.indent("#   ").lnPrint(ThrowableSugar.javaStack(leaf));
            }
            if (showsEStack) {
                errs.indent("#   ").print(ThrowableSugar.eStack(t));
            }

            errs.println();
            errs.println();
        } catch (Throwable tt) {
            ExceptionMgr.reportException(tt, "while reporting: " + t);
        }
    }

    /**
     *
     */
    static public void printTime(long start, double percentile) {
        long stop = System.currentTimeMillis();
        //noinspection AccessOfSystemProperties
        ConstMap props = ConstMap.fromProperties(System.getProperties());
        if (ConstMap.testProp(props, "e.interp.print-timing")) {
            Profiler.THE_ONE.printTime(percentile);
            Selector.printCacheStats();
            Memoizer.printCacheStats();
            //noinspection UseOfSystemOutOrSystemErr
            System.err
              .println("Run after initialization: " + (stop - start) + " ms");
        }
    }

    /**
     *
     */
    static private boolean testProperty(String propName, boolean dflt) {
        //noinspection AccessOfSystemProperties
        String optVal = System.getProperty(propName);
        if (null == optVal) {
            return dflt;
        }
        if ("true".equals(optVal)) {
            return true;
        }
        if ("false".equals(optVal)) {
            return false;
        }
        T.fail("\"" + optVal + "\" must be \"true\" or \"false\"");
        return false; //make compiler happy
    }

    /**
     * Called to successfully exit the process.
     * <p/>
     * If e.gui-launch is true and e.onOkGuiExit is "prompt", then this prompts
     * for a character to be typed before exiting.
     */
    static void okExit(TextWriter errs) {
        try {
            if (testProperty("e.gui-launch", false)) {
                //noinspection AccessOfSystemProperties
                String ooge = System.getProperty("e.onOkGuiExit", "ignore");
                if ("ignore".equals(ooge)) {
                    //ignored
                } else if ("prompt".equals(ooge)) {
                    errs.print("\nHit Enter to dismiss.\n");
                    //noinspection ResultOfMethodCallIgnored
                    System.in.read();
                } else {
                    T.fail("unrecognized e.onOkGuiExit value: " + ooge);
                }
            }
        } catch (IOException tt) {
            ExceptionMgr.reportException(tt, "while exiting");
            System.exit(-3);
        }
        System.exit(0);
    }

    /**
     *
     */
    static void errorExit(TextWriter errs, Throwable problem) {
        reportProblem(problem,
                      errs,
                      testProperty("e.interp.show-j-stack", false),
                      testProperty("e.interp.show-e-stack", true));
        //noinspection AccessOfSystemProperties
        String oee = System.getProperty("e.onErrorExit", "gui");
        try {
            if ("report".equals(oee)) {
                //do nothing, Error should already be reported on stderr

            } else if ("prompt".equals(oee)) {
                errs.print("\nHit Enter to dismiss.\n");
                //noinspection ResultOfMethodCallIgnored
                System.in.read();

            } else if ("gui".equals(oee)) {
                //XXX not yet implemented, uses prompt's behavior instead
                errs.print("\nHit Enter to dismiss.\n");
                //noinspection ResultOfMethodCallIgnored
                System.in.read();
            } else {
                T.fail("unrecognized e.onErrorExit value: " + oee);
            }
        } catch (IOException tt) {
            ExceptionMgr.reportException(tt, "while reporting: " + problem);
            System.exit(-3);
        }
        System.exit(-1);
    }

    /**
     *
     */
    static public void main(String[] argArray) {
        
        // workaround for bug in Mac OS X Leopard Java -XstartOnFirstThread (needed for swt); from http://lists.apple.com/archives/java-dev//2008/Feb/msg00179.html
        {
            final Thread t = Thread.currentThread();
            if (t.getContextClassLoader() == null) {
                t.setContextClassLoader(ClassLoader.getSystemClassLoader());
            }
        }

        final TextWriter errs =
          new TextWriter(PrintStreamWriter.stderr(), true);
        try {
            TextWriter outs = new TextWriter(PrintStreamWriter.stdout(), true);

            final ConstList args = doProps(ConstList.fromArray(argArray));

            //noinspection AccessOfSystemProperties
            Properties sysProps = System.getProperties();
            ConstMap props = ConstMap.fromProperties(sysProps);

            Vat startVat = Vat.make("headless", "start");
            final Scope priv = ScopeSetup.privileged("__main$",
                                                     Ref.broken(E.asRTE(
                                                       "XXX No stdin 4")),
                                                     outs,
                                                     errs,
                                                     props,
                                                     new BogusInterp(props),
                                                     startVat);
            Throwable optNoStart = startVat.enqueue(new Runnable() {

                /**
                 *
                 */
                public void run() {
                    Object rune = priv.get("rune");
                    final long start = System.currentTimeMillis();

                    //Note that this says ".call" rather than ".callAll".
                    //This means that we're invoking run/1 with 'args' as the
                    //one argument, rather than using 'args' as the list of
                    //arguments.
                    Object outcomeVow;
                    try {
                        outcomeVow = E.call(rune, "run", args);
                    } catch (Throwable problem) {
                        outcomeVow = Ref.broken(problem);
                    }

                    Ref.whenResolvedOnly(outcomeVow,
                                         new Terminator(start, errs));
                }

                /**
                 *
                 */
                public String toString() {
                    return "<Rune " + E.toString(args) + ">";
                }
            });
            if (null != optNoStart) {
                throw optNoStart;
            }

            // For some reason, SWT on Mac OS X 10.5 doesn't work if the main thread exits. It looks like SWT should actually be *used* only from the main thread, but this seems sufficient, so we just don't let the main thread exit. (It will be killed when the Terminator does System.exit())
        Object stub = new Object();
        synchronized (stub) {
            while (true) stub.wait();
        }

        } catch (Throwable problem) {
            errorExit(errs, problem);
        }
    }

    /**
     * Only used to make the rune function.
     */
    static public class BogusInterp {

        final ConstMap myProps;

        public BogusInterp(ConstMap props) {
            myProps = props;
        }

        /**
         * This is the only interp method needed for making the rune function.
         */
        public ConstMap getProps() {
            return myProps;
        }
    }

    /**
     *
     */
    static class Terminator implements OneArgFunc {

        private final long myStart;
        private final TextWriter myErrs;

        /**
         *
         */
        Terminator(long start, TextWriter errs) {
            myStart = start;
            myErrs = errs;
        }

        /**
         *
         */
        public Object run(Object arg) {
            Throwable optProblem = Ref.optProblem(arg);
            if (null == optProblem) {
                printTime(myStart, 1.0);
                okExit(myErrs);
            } else {
                printTime(myStart, 1.0);
                errorExit(myErrs, optProblem);
            }
            return null; //make the compiler happy
        }

        /**
         *
         */
        public String toString() {
            return "<Rune terminator>";
        }
    }

}
