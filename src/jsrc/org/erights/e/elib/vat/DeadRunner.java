package org.erights.e.elib.vat;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.trace.Trace;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.ref.EProxyResolver;
import org.erights.e.elib.sealing.Unsealer;

/**
 * To shutdown a Vat is to {@link Vat#mergeInto merge it into} a DeadRunner.
 * <p/>
 * Since a DeadRunner is immutable, it needs no synchronization.
 *
 * @author Mark S. Miller
 * @author From an idea by Dean Tribble
 */
final class DeadRunner extends Runner {

    /**
     * Why is this Vat dead?
     */
    private final Throwable myProblem;

//    private final Ref myBroken;

    /**
     * @param problem Why is this Vat dead?
     */
    DeadRunner(Throwable problem) {
        myProblem = problem;
//        myBroken = Ref.broken(problem);
    }

    /**
     *
     */
    public String toString() {
        return "<DeadRunner " + myProblem + ">";
    }

    /**
     * Does nothing, since a DeadRunner has thread or priority.
     */
    protected void setPriority(int newPriority) {
    }

    /**
     * @return <tt>"dead"</tt>
     */
    protected String getRunnerKind() {
        return "dead";
    }

    /**
     * @return false, since a DeadRunner is never current.
     */
    protected boolean isCurrent() {
        return false;
    }

    /**
     * Does nothing, since we're already doing nothing.
     */
    protected void disturbEvent(Throwable t) {
    }

    /**
     * @return The problem explaining why this Vat is broken.
     */
    protected Throwable enqueue(PendingEvent todo) {
        return new DeadRunnerException(myProblem);
    }

    /**
     *
     */
    protected void addDeadManSwitch(Object deadManSwitchRef) {
        Unsealer unsealer = BootRefHandler.OurUnsealer;
        BootRefHandler optHandler =
          (BootRefHandler)EProxyResolver.getOptProxyHandler(unsealer,
                                                            deadManSwitchRef);
        if (null == optHandler) {
            //not a boot-ref, so just invoke directly
            try {
                E.call(deadManSwitchRef, "__reactToLostClient", myProblem);
            } catch (Throwable t) {
                Trace.causality
                    .errorm("Exception from DeadManSwitch " + deadManSwitchRef, t);
            }

            return;
        }
        //host of the deadManSwitch
        Vat hostVat = optHandler.myTargetsVat;
        Object deadManSwitch = optHandler.myTarget;
        if (hostVat.getRunner() instanceof DeadRunner) {
            //would be notified in a DeadRunner, so forget it
            return;
        }
        Object[] args = {myProblem};
        hostVat.qSendAllOnly(deadManSwitch,
                             false,
                             "__reactToLostClient",
                             args);
        //We don't care about qSendOnly's return result.
    }
}
