package org.erights.e.elib.slot;

import org.erights.e.elib.base.Ejector;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.util.OneArgFunc;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Most Auditors will also double as Guards in a stereotyped way.
 * <p/>
 * This base class is just a convenience for such combined Auditor/Guards
 * that fit this pattern.
 *
 * @author Mark S. Miller
 */
public abstract class BaseAuditor implements Guard, Auditor {

    /**
     *
     */
    protected BaseAuditor() {
    }

    /**
     * Defaults to reporting a failure to audit.
     */
    public boolean audit(Audition audition) {
        throw new AuditFailedException(this, audition.getSource());
    }

    /**
     * XXX kludge
     */
    protected Auditor getAuditor() {
        return this;
    }

    /**
     * If at first you don't succeed, try again exactly once.
     * <p/>
     * The default implementation here is
     * <a href="http://www.sims.berkeley.edu/~ping/auditors/">Gozarian</a>
     * -- if we don't succeed when we {@link #tryCoerceR try to coerce} the
     * object, it is asked to
     * {@link MirandaMethods#__conformTo
     * __conformTo/1} this guard and given a second chance
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        //shorten first
        specimen = Ref.resolution(specimen);
        Object problem;
        Ejector ej = new Ejector("coercion");
        try {
            return tryCoerceR(specimen, ej);
        } catch (Throwable t) {
            problem = ej.result(t);
        } finally {
            ej.disable();
        }
        if (specimen instanceof Conformable) {
            // Note that this is a guard, though not necessarily an auditor.
            // Don't get misled by the class in which this code appears --
            // this method should only get called if this is actually a guard.
            Object newSpecimen = ((Conformable)specimen).__conformTo(this);
            // If should be ok to use == here rather than Ref.isSame, since
            // it should be unobservable whether we try again.
            if (specimen != newSpecimen) {
                return tryCoerceR(Ref.resolution(newSpecimen), optEjector);
            }
        }
        throw Thrower.toEject(optEjector, problem);
    }

    /**
     * Called before and possibly after asking the object to __conformTo this
     * guard.
     * <p/>
     * In this default implementation, if the specimen was instantiated with
     * the approval of our auditor, the specimen itself is returned. Otherwise,
     * a coercion failure is reported.
     * <p/>
     * The uppercase "R" suffix indicate that this method's callers must
     * ensure that the "short" arguments are already in the form that would
     * be returned by {@link org.erights.e.elib.ref.Ref#resolution
     * Ref.resolution/1}.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (AuditChecker.THE_ONE.run(getAuditor(), shortSpecimen)) {
            return shortSpecimen;
        }
        throw Thrower.toEject(optEjector,
                              "Not audited by " + E.toString(getAuditor()));
    }
}
