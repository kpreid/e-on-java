package org.erights.e.elang.evm;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.MessageDesc;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.prim.SafeJ;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.SugarMethodNode;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.Audition;
import org.erights.e.elib.slot.Auditor;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexSet;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * BNF: "def" oName ("implements" eExpr+)? "{" method* matcher* "}"
 * <p>
 * Yields an object that closes over the current scope, and responds
 * to requests by dispatching to one of its matching methods, or to
 * a matcher if provided and no methods match.
 *
 * @see DefineExpr
 * @author Mark S. Miller
 */
public class ObjectExpr extends EExpr {

    /**
     *
     */
    static private final Guard AuditorGuard =
      ClassDesc.make(Auditor.class);

    static private final Auditor[] NO_AUDITORS = {};

    private final String myDocComment;

    private final GuardedPattern myOName;

    private final EExpr[] myAuditorExprs;

    private transient FlexSet myOptAuditorCache = null;

    private final EScript myEScript;

    private final NounExpr[] myOptFieldInits;

    private final ObjectExpr myOptSource;

    private transient EMethodTable myOptEMTableCache = null;

    /**
     *
     */
    public ObjectExpr(SourceSpan optSpan,
                      String docComment,
                      GuardedPattern oName,
                      EExpr[] auditorExprs,
                      EScript eScript,
                      ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        if (docComment == null) {
            docComment = "Oops, docComment was null";
        }
        myDocComment = docComment;
        myOName = oName;
        myAuditorExprs = auditorExprs;
        myEScript = eScript;
        myOptFieldInits = null;
        myOptSource = null;
        ensureWellFormed();
    }

    /**
     *
     */
    public ObjectExpr(SourceSpan optSpan,
                      String docComment,
                      GuardedPattern oName,
                      EExpr[] auditorExprs,
                      EScript eScript,
                      NounExpr[] fieldNouns,
                      ObjectExpr source,
                      ScopeLayout optScopeLayout) {
        super(optSpan, optScopeLayout);
        myDocComment = docComment;
        myOName = oName;
        myAuditorExprs = auditorExprs;
        myEScript = eScript;
        myOptFieldInits = fieldNouns;
        myOptSource = source;
        T.notNull(source, "Transformed-E constructor requires source");
        ensureWellFormed();
    }

    private void ensureWellFormed() {
        T.notNull(myDocComment, "docComment may be empty, but not null");
        T.require(null == myOName.getOptGuardExpr(),
                  "ObjExpr oName cannot be guarded", myOName);
        StaticScope audScope = staticScopeOfList(myAuditorExprs);
        ConstMap selfNames = myOName.staticScope().outNames();
        T.require(!selfNames.intersects(audScope.namesUsed()),
                  "Auditors can't use self: ", myOName);
//        ConstMap freeNames = myEScript.staticScope().namesUsed();
//        ConstMap conflicts = audScope.outNames().and(freeNames);
//        T.require(conflicts.size() == 0,
//                  "Object can't use names defined by auditors: ", conflicts);
    }

    /**
     * Uses 'makeObjectExpr(...)' for either
     * {@link #ObjectExpr(SourceSpan, String, GuardedPattern, EExpr[],
     * EScript, ScopeLayout) the regular constructor} or the
     * {@link #ObjectExpr(SourceSpan, String, GuardedPattern, EExpr[],
     * EScript, NounExpr[], ObjectExpr, ScopeLayout) the Transformed-E
     * constructor}.
     */
    public Object[] getSpreadUncall() {
        Object[] result;
        if (null == myOptSource) {
            result = new Object[] {
              StaticMaker.make(ObjectExpr.class),
              "run",
              getOptSpan(),
              myDocComment,
              myOName,
              myAuditorExprs,
              myEScript,
              getOptScopeLayout() };
        } else {
            result = new Object[] {
              StaticMaker.make(ObjectExpr.class),
              "run",
              getOptSpan(),
              myDocComment,
              myOName,
              myAuditorExprs,
              myEScript,
              myOptFieldInits,
              myOptSource,
              getOptScopeLayout() };
        }
        return result;
    }

    /**
     *
     */
    public Object welcome(ETreeVisitor visitor) {
        return visitor.visitObjectExpr(this,
                                       myDocComment,
                                       myOName,
                                       myAuditorExprs,
                                       myEScript);
    }

    /**
     * The left-to-right sum of the oName + auditors + the eScript.
     * <p>
     * Note that the instance variable are only the variables used by the
     * eScript, not the variables used by the objectExpr as a whole, since
     * the auditor expressions are evaluated in the instantiating environment.
     */
    protected StaticScope computeStaticScope() {
        StaticScope result = myOName.staticScope();
        result = result.add(staticScopeOfList(myAuditorExprs));
        return result.add(myEScript.staticScope());
    }

    static private StaticScope staticScopeOfList(ENode[] nodes) {
        StaticScope result = StaticScope.EmptyScope;
        for (int i = 0, len = nodes.length; i < len; i++) {
            result = result.add(nodes[i].staticScope());
        }
        return result;
    }

    /**
     *
     */
    protected Object subEval(EvalContext ctx, boolean forValue) {
        int numAuditors = myAuditorExprs.length;
        int numFields = myOptFieldInits.length;
        Auditor[] auditors;
        if (0 == numAuditors) {
            auditors = NO_AUDITORS;
        } else {
            // Auditing always happens, regardless of forValue.
            auditors = new Auditor[numAuditors];
            for (int i = 0; i < numAuditors; i++) {
                Object aud = myAuditorExprs[i].subEval(ctx, true);
                auditors[i] = (Auditor)AuditorGuard.coerce(aud, null);

            }
        }

        // Change from 0.8.36: All auditors are evaluated before any are
        // asked to audit, so that the bindings passed to the auditors
        // is the one including all instance variable guards.
        Auditor[] approvals;
        if (0 == numAuditors) {
            approvals = NO_AUDITORS;
        } else {
            FlexList approvers = FlexList.fromType(Auditor.class, numAuditors);
            PrimAudition audtition = new PrimAudition(ctx, approvers);
            for (int i = 0; i < numAuditors; i++) {
                audtition.ask(auditors[i]);
            }
            approvals = (Auditor[])approvers.getArray(Auditor.class);
        }

        Object[] fields = new Object[numFields];
        Object result = new EImplByProxy(approvals,
                                         fields,
                                         ctx.outers(),
                                         eMethodTable());
        myOName.testMatch(ctx, result, null);
        for (int i = 0; i < numFields; i++) {
            fields[i] = myOptFieldInits[i].getRepresentation(ctx);
        }
        return result;
    }

    /**
     *
     */
    class PrimAudition implements Audition {

        private final EvalContext myCtx;
        private final FlexList myApprovers;

        PrimAudition(EvalContext ctx, FlexList approvers) {
            myCtx = ctx;
            myApprovers = approvers;
        }

        /**
         *
         */
        public void ask(Auditor auditor) {
            if (null != myOptAuditorCache &&
              myOptAuditorCache.contains(auditor)) {
                // Audit succeeded previously, so we can proceed.
                myApprovers.push(auditor);
            } else {
                if (auditor.audit(this)) {
                    if (Ref.isDeepFrozen(auditor)) {
                        // XXX Is isDeepFrozen a strong enough test?
                        // If so, then, once Auditor implies DeepFrozen, we can
                        // remove this test.
                        if (null == myOptAuditorCache) {
                            myOptAuditorCache =
                              FlexSet.fromType(Auditor.class);
                        }
                        myOptAuditorCache.addElement(auditor);
                    }
                    myApprovers.push(auditor);
                }
            }
        }

        /**
         *
         */
        public Object getSource() {
            return myOptSource;
        }

        /**
         *
         */
        public Guard getOptGuard(String fieldName) {
            T.fail("XXX not yet implemented");
            return null; //make compiler happy
        }
    }

    /**
     *
     */
    public void subMatchBind(ConstList args,
                             Object specimen,
                             OneArgFunc optEjector,
                             FlexList bindings) {
        ObjectExpr other;
        try {
            other = (ObjectExpr)Ref.resolution(specimen);
        } catch (ClassCastException cce) {
            //using a try/catch since success is typical and we have
            //to pay for the test in the cast anyway
            throw Thrower.toEject(optEjector, cce);
        }
        myOName.subMatchBind(args, other.myOName, optEjector, bindings);
        matchBind(myAuditorExprs,
                  args,
                  other.myAuditorExprs,
                  optEjector,
                  bindings);
        myEScript.subMatchBind(args, other.myEScript, optEjector, bindings);
    }

    /**
     *
     */
    public String getDocComment() {
        return myDocComment;
    }

    /**
     *
     */
    public GuardedPattern getOName() {
        return myOName;
    }

    /**
     * Is the name fully qualified?
     * <p>
     * Currently, if it is a "_" or starts with a "$", then it's not fully
     * qualified. Otherwise it is.
     */
    static public boolean isFQName(String qualifiedName) {
        if (qualifiedName.startsWith("$")) {
            return false;
        } else {
            return !("_".equals(qualifiedName));
        }
    }

    /**
     *
     */
    public ConstList getAuditorExprs() {
        return ConstList.fromArray(myAuditorExprs);
    }

    /**
     * @deprecated Use {@link #getScript()}
     */
    public EScript getEScript() {
        return myEScript;
    }

    /**
     *
     */
    public EScript getScript() {
        return myEScript;
    }

    /**
     * Return the nouns used to initialize instance
     * fields from the outer scope. <p>
     *
     * Will be non-null after the appropriate compilation transformation.
     */
    NounExpr[] optFieldNouns() {
        return myOptFieldInits;
    }

    /**
     *
     */
    public void subPrintOn(TextWriter out, int priority) throws IOException {
        MessageDesc.synopsize(out, myDocComment);
        out.print("def ");
        myOName.subPrintOn(out, PR_ORDER);
        int numAuditors = myAuditorExprs.length;
        if (numAuditors >= 1) {
            out.print(" implements ");
            myAuditorExprs[0].subPrintOn(out, PR_ORDER);
            for (int i = 1; i < numAuditors; i++) {
                out.print(", ");
                myAuditorExprs[i].subPrintOn(out, PR_ORDER);
            }
        }
        myEScript.subPrintOn(out, PR_START);
    }

    /**
     *
     */
    private EMethodTable eMethodTable() {
        if (null == myOptEMTableCache) {
            EMethodTable eMTableCache = new EMethodTable(this);
            EMethod[] optMeths = myEScript.myOptMethods;
            if (optMeths != null) {
                SugarMethodNode.defineMembers(eMTableCache,
                                              MirandaMethods.class);
                for (int i = 0, max = optMeths.length; i < max; i++) {
                    EMethodNode methNode = new EMethodNode(getFQName(),
                                                           optMeths[i]);
                    eMTableCache.addMethod(methNode, SafeJ.NONE);
                }
            }

            EMatcher[] matchers = myEScript.myMatchers;
            if (matchers.length >= 1) {
                EMatchersNode matchersNode = new EMatchersNode(getFQName(),
                                                               matchers);
                eMTableCache.setOptOtherwise(matchersNode);
            }
            myOptEMTableCache = eMTableCache;
        }
        return myOptEMTableCache;
    }

    /**
     *
     */
    String getFQName() {
        String fqnPrefix = myEScript.getOptScopeLayout().getFQNPrefix();
        T.require(fqnPrefix.endsWith("$"), "Bad fqnPrefix: ", fqnPrefix);
        return fqnPrefix.substring(0, fqnPrefix.length() -1);
    }
}
