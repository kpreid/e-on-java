// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.prim;

import org.erights.e.elib.base.Script;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.meta.java.lang.DoubleSugar;
import org.erights.e.meta.java.math.EInt;

/**
 * Represents an E selector (verb/arity) for implementing call-site caching.
 * <p/>
 * Must be thread safe. Should be (and is) thread safe without locking.
 * <p/>
 * Making this DeepPassByCopy is semantically correct, but misleading. Two
 * copies are semantically identical, but cache separately.
 *
 * @author Mark S. Miller
 */
public class Selector implements DeepPassByCopy {

    static private final long serialVersionUID = -3577341233339877296L;

    static private final StaticMaker SelectorMaker =
      StaticMaker.make(Selector.class);

    static private int MyHitCount = 0;
    static private int MyMISSCount = 0;

    /**
     * XXX Security alert: covert channel?
     */
    static public void printCacheStats() {
        double percent = ((MyHitCount * 100.0) / (MyHitCount + MyMISSCount));
        System.err
          .println("Call site cache hits: " + MyHitCount + " misses: " +
            MyMISSCount + " (" + DoubleSugar.round(percent) + "%)");
    }

    /**
     * @serial interned
     */
    private final String myVerb;

    private final int myArity;

    /**
     * Thread safety depends on reads and writes of this one variable being
     * atomic.
     */
    private transient Script myOptLastScript = null;

    /**
     * @param verb
     * @param arity
     */
    public Selector(String verb, int arity) {
        myVerb = verb.intern();
        myArity = arity;
    }

    /**
     * Uses 'makeSelector(verb, arity)'
     */
    public Object[] getSpreadUncall() {
        Object[] result =
          {SelectorMaker, "run", myVerb, EInt.valueOf(myArity)};
        return result;
    }

    /**
     * @return
     */
    public Object callIt(Object optSelf, Object[] args) {
        optSelf = Ref.resolution(optSelf);
        // read the instance variable only once to ensure thread safety.
        Script optLastScript = myOptLastScript;
        if (null != optLastScript && optLastScript.canHandleR(optSelf)) {
            // cache hit
            MyHitCount++;
            return optLastScript.execute(optSelf, myVerb, args);
        } else {
            // cache miss
            MyMISSCount++;
            Class recClass;
            if (optSelf == null) {
                recClass = Object.class;
            } else {
                recClass = optSelf.getClass();
            }
            Script script = ScriptMaker.THE_ONE.instanceScript(recClass);
            script = script.shorten(optSelf, myVerb, myArity);
            // Because we don't lock, one of these writes can wipe out another,
            // but that's fine.
            myOptLastScript = script;
            return script.execute(optSelf, myVerb, args);
        }
    }

    /**
     * @return
     */
    public String toString() {
        return myVerb + "/" + myArity;
    }
}
