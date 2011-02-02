// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.interp;

import org.erights.e.develop.exception.PrintStreamWriter;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;

import java.io.File;

/**
 * Used to install, and as a "rune" driver for rune-ing a spawned jvm.
 * <p/>
 * This class is also the "Main-Class" in the e.jar manifest, in order to
 * install E by double-clicking on e.jar. Therefore, when it's main() is
 * invoked with no arguments, it runs the install script.
 *
 * @author Mark S. Miller
 */
public class MetaRune {

    static private final String USAGE =
      "Given <prelude> ::= java -jar <j-opt>* <ehome>/e.jar <prop>*\n" +
        "Usage is one of:\n" +
        "    <prelude> --install <arg>*   Installs E\n" +
        "    <prelude>                    Defaults to --install\n" +
        "    <prelude> --rune <m-opts> --? (<fname> <arg>*)?\n" +
        "                                 Runs in this jvm without SWT.\n" +
        "    <prelude> --spawn <s-opt>* --? (<fname> <arg>*)?\n" +
        "                                 Runs in a spawned jvm.\n" +
        "    <prelude> --help             Prints this text\n" +
        "    <prelude> --version          Identical to \n" +
        "                                   \"<prelude> --rune --version\"\n" +
        "Especially for the \"--rune\" or \"--spawn\" cases, it's helpful\n" +
        "if the <j-opt> list includes \"-De.home=<ehome>\" or for the\n" +
        "current directory to be <ehome>. So, for example, for more usage\n" +
        "help, say" +
        "  $ cd <ehome>\n" + "  $ java -jar e.jar --rune --help";

    /**
     * Prevents instantiation.
     */
    private MetaRune() {
    }

//    /**
//     *
//     * @param metain
//     * @param metaout
//     * @param metaerr
//     * @param args
//     * @return
//     */
//    static public Object[] spawn(BufferedReader metain,
//                                 TextWriter metaout,
//                                 TextWriter metaerr,
//                                 String[] args) {
//        final Process process;
//        try {
//            process = Runtime.getRuntime().exec(args);
//        } catch (IOException e) {
//            throw ExceptionMgr.asSafe(e);
//        }
//        Vat exitVat = Vat.make("headless", "process-exit");
//        Object outcomeVow = exitVat.seed(new Thunk() {
//            public Object run() {
//                int exitCode;
//                while (true) {
//                    try {
//                        exitCode = process.waitFor();
//                        break;
//                    } catch (InterruptedException e) {
//                        //try again
//                    }
//                }
//                if (exitCode == 0) {
//                    return Boolean.TRUE;
//                } else {
//                    RuntimeException problem =
//                      new RuntimeException("exitCode: " + exitCode);
//                    return Ref.broken(problem);
//                }
//            }
//
//            public String toString() {
//                return "<MetaRune process-exit>";
//            }
//        });
//        Object[] result = { process, outcomeVow };
//        return result;
//    }

    static private final String INSTALLER_PATH = "scripts/setup.e-awt";

    /**
     * Find the installer E program. Call after the e.home property has been
     * set, if it will be at all.
     */
    static private Object installerFile() {
        String optEHome = System.getProperty("e.home");
        if (optEHome != null) {
            if (!optEHome.endsWith("/")) {
                optEHome += "/";
            }
            return optEHome + INSTALLER_PATH;
        } else {
            // If we don't have an e.home, then we try fetching a resource,
            // in the hope that we're running from our jar, which includes the
            // installer.
            return new InstallerLauncher();
        }
    }
    
    private class InstallerLauncher {
        to run(ConstMap props, Object options, Object optFname, Object args) {
            EParser.run(ResourceUriGetter.THE_ONE.get(INSTALLER_PATH)).eval(ScopeSetup.privileged("__installerMain",
                                           null,
                                           TextWriter altout,
                                           TextWriter alterr,
                                           props,
                                           "dummyInterp",
                                           null));
        }
    }

    /**
     * @param argArray
     */
    static public void main(String[] argArray) {
        final TextWriter errs =
          new TextWriter(PrintStreamWriter.stderr(), true);
        try {
            final TextWriter outs =
              new TextWriter(PrintStreamWriter.stdout(), true);
//            final BufferedReader ins = PrintStreamWriter.stdin();

            ConstList argList = Rune.doProps(ConstList.fromArray(argArray));

            if (0 == argList.size()) {
                Object[] args = {installer()};
                Rune.main(ConstList.fromArray(args));
                return;
            }
            Object option = argList.get(0);
            if ("--install".equals(option)) {
                FlexList args = argList.diverge();
                args.put(0, installer());
                Rune.main(args.snapshot());
                return;
            }
            if ("--rune".equals(option)) {
                Rune.main(argList.run(1, argList.size()));
                return;
            }
            if ("--spawn".equals(option)) {
                errs.println("XXX Rune spawning not yet implemented");
                System.exit(-1);
            }
            if ("--help".equals(option)) {
                outs.println(USAGE);
                return;
            }
            if ("--version".equals(option)) {
                Rune.main(argList);
                return;
            }
            errs.print("Not understood: ", argList, "\n");
            errs.println(USAGE);
            System.exit(-1);

//            final String[] args = argArray;
//
//            Vat metaVat = Vat.make("headless", "meta");
//
//            Throwable optNoStart = metaVat.enqueue(new Runnable() {
//
//                /**
//                 *
//                 */
//                public void run() {
//                    final long start = System.currentTimeMillis();
//
//                    Object outcomeVow;
//                    try {
//                        outcomeVow = spawn(ins, outs, errs, args)[1];
//                    } catch (Throwable problem) {
//                        outcomeVow = Ref.broken(problem);
//                    }
//
//                    Ref.whenResolvedOnly(outcomeVow,
//                                         new Rune.Terminator(start, errs));
//                }
//
//                /**
//                 *
//                 */
//                public String toString() {
//                    return "<MetaRune " + E.toString(argList) + ">";
//                }
//            });
//            if (null != optNoStart) {
//                throw optNoStart;
//            }


        } catch (Throwable problem) {
            Rune.errorExit(errs, problem);
        }
    }
}
