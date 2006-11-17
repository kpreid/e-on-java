package org.erights.e.elang.visitors;

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.AssignExpr;
import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.CallExpr;
import org.erights.e.elang.evm.CatchExpr;
import org.erights.e.elang.evm.DefineExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.EscapeExpr;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.FinallyExpr;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.HideExpr;
import org.erights.e.elang.evm.IfExpr;
import org.erights.e.elang.evm.IgnorePattern;
import org.erights.e.elang.evm.ListPattern;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.MetaContextExpr;
import org.erights.e.elang.evm.MetaStateExpr;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.SeqExpr;
import org.erights.e.elang.evm.SimpleNounExpr;
import org.erights.e.elang.evm.SlotExpr;
import org.erights.e.elang.evm.SlotPattern;
import org.erights.e.elang.evm.VarPattern;
import org.erights.e.elang.evm.ViaPattern;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;

import java.lang.reflect.Array;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public class KernelECopyVisitor implements ETreeVisitor {

    private final ETreeVisitor myDecorator;

    /**
     *
     */
    public KernelECopyVisitor(ETreeVisitor decorator) {
        myDecorator = decorator;
    }

    /**
     * Defaults to auto-decoration
     */
    public KernelECopyVisitor() {
        myDecorator = this;
    }

    /**
     *
     */
    static SourceSpan getOptSpan(ENode optNode) {
        if (null == optNode) {
            return null;
        } else {
            return optNode.getOptSpan();
        }
    }

    /**
     * Return the ScopeLayout a newly created ENode should have.
     * <p/>
     * The default here just returns null.
     */
    public ScopeLayout getOptScopeLayout() {
        return null;
    }

    /**
     * Return a version of the receiver for a nested scope.
     * <p/>
     * The default is that there is no difference.
     * <p/>
     * XXX: this needs to generate a new visitor by talking to the decorator.
     *
     * @param oName
     */
    KernelECopyVisitor nest(GuardedPattern oName) {
        return this;
    }

    /**
     * Return a version of the receiver for a nested scope.
     * <p/>
     * The default is that there is no difference.
     * <p/>
     * XXX: this needs to generate a new visitor by talking to the decorator.
     */
    KernelECopyVisitor nest() {
        return this;
    }

    /**
     * For use from E. Java/ELib users should use the more strongly typed
     * xform* methods.
     *
     * @param optENode must be either null, an ENode, or an array of ENodes.
     */
    public Object run(Object optENode) {
        if (optENode == null) {
            return null;
        } else if (optENode instanceof ENode) {
            return xformNode((ENode)optENode);
        } else {
            ENode[] eNodes = (ENode[])optENode;
            Class elementClass = eNodes.getClass().getComponentType();
            ENode[] result =
              (ENode[])Array.newInstance(elementClass, eNodes.length);
            for (int i = 0; i < eNodes.length; i++) {
                result[i] = (ENode)run(eNodes[i]);
            }
            return result;
        }
    }

    /**
     *
     */
    public ENode xformNode(ENode eNode) {
        return (ENode)eNode.welcome(myDecorator);
    }

    /**************************** EExprs **************************/

    /**
     *
     */
    public EExpr xformEExpr(EExpr optEExpr) {
        return (EExpr)run(optEExpr);
    }

    /**
     *
     */
    public EExpr[] xformEExprs(EExpr[] eExprs) {
        return (EExpr[])run(eExprs);
    }

    /**
     *
     */
    public Object visitAssignExpr(ENode optOriginal,
                                  AtomicExpr noun,
                                  EExpr rValue) {
        return new AssignExpr(getOptSpan(optOriginal),
                              (AtomicExpr)xformEExpr(noun),
                              xformEExpr(rValue),
                              getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitCallExpr(ENode optOriginal,
                                EExpr recip,
                                String verb,
                                EExpr[] args) {
        return new CallExpr(getOptSpan(optOriginal),
                            xformEExpr(recip),
                            verb,
                            xformEExprs(args),
                            getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitDefineExpr(ENode optOriginal,
                                  Pattern patt,
                                  EExpr optEjectorExpr,
                                  EExpr rValue) {
        return new DefineExpr(getOptSpan(optOriginal),
                              xformPattern(patt),
                              xformEExpr(optEjectorExpr),
                              xformEExpr(rValue),
                              getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitEscapeExpr(ENode optOriginal,
                                  Pattern hatch,
                                  EExpr body,
                                  Pattern optArgPattern,
                                  EExpr optCatcher) {
        KernelECopyVisitor t = nest();
        KernelECopyVisitor catchScope = nest();
        return new EscapeExpr(getOptSpan(optOriginal),
                              t.xformPattern(hatch),
                              t.xformEExpr(body),
                              catchScope.xformPattern(optArgPattern),
                              catchScope.xformEExpr(optCatcher),
                              getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitHideExpr(ENode optOriginal, EExpr body) {
        return new HideExpr(getOptSpan(optOriginal),
                            nest().xformEExpr(body),
                            getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitIfExpr(ENode optOriginal,
                              EExpr test,
                              EExpr then,
                              EExpr els) {
        KernelECopyVisitor t = nest();
        return new IfExpr(getOptSpan(optOriginal),
                          t.xformEExpr(test),
                          t.xformEExpr(then),
                          nest().xformEExpr(els),
                          getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitLiteralExpr(ENode optOriginal, Object value) {
        return new LiteralExpr(getOptSpan(optOriginal),
                               value,
                               getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitNounExpr(ENode optOriginal, String varName) {
        return new SimpleNounExpr(getOptSpan(optOriginal),
                                  varName,
                                  getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitObjectExpr(ENode optOriginal,
                                  String docComment,
                                  GuardedPattern oName,
                                  EExpr[] auditors,
                                  EScript eScript) {
        GuardedPattern guarded = (GuardedPattern)xformPattern(oName);
        KernelECopyVisitor t = nest(guarded);
        return new ObjectExpr(getOptSpan(optOriginal),
                              docComment,
                              guarded,
                              t.xformEExprs(auditors),
                              t.xformEScript(eScript),
                              getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitMetaStateExpr(ENode optOriginal) {
        return new MetaStateExpr(getOptSpan(optOriginal), getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitMetaContextExpr(ENode optOriginal) {
        return new MetaContextExpr(getOptSpan(optOriginal),
                                   getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitSeqExpr(ENode optOriginal, EExpr[] subs) {
        EExpr[] newSubs = new EExpr[subs.length];
        for (int i = 0, max = subs.length; i < max; i++) {
            newSubs[i] = xformEExpr(subs[i]);
        }
        return new SeqExpr(getOptSpan(optOriginal),
                           newSubs,
                           getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitSlotExpr(ENode optOriginal, AtomicExpr noun) {
        return new SlotExpr(getOptSpan(optOriginal),
                            (AtomicExpr)xformEExpr(noun),
                            getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitCatchExpr(ENode optOriginal,
                                 EExpr attempt,
                                 Pattern patt,
                                 EExpr catcher) {
        KernelECopyVisitor t = nest();
        KernelECopyVisitor catchScope = nest();
        return new CatchExpr(getOptSpan(optOriginal),
                             t.xformEExpr(attempt),
                             catchScope.xformPattern(patt),
                             catchScope.xformEExpr(catcher),
                             getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitFinallyExpr(ENode optOriginal,
                                   EExpr attempt,
                                   EExpr unwinder) {
        return new FinallyExpr(getOptSpan(optOriginal),
                               nest().xformEExpr(attempt),
                               nest().xformEExpr(unwinder),
                               getOptScopeLayout());
    }

    /***************************** Patterns *************************/

    /**
     *
     */
    public Pattern xformPattern(Pattern optPatt) {
        return (Pattern)run(optPatt);
    }

    /**
     *
     */
    public Pattern[] xformPatterns(Pattern[] patts) {
        return (Pattern[])run(patts);
    }

    /**
     *
     */
    public Object visitFinalPattern(ENode optOriginal,
                                    AtomicExpr nounExpr,
                                    EExpr optGuardExpr) {
        return new FinalPattern(getOptSpan(optOriginal),
                                (AtomicExpr)xformEExpr(nounExpr),
                                xformEExpr(optGuardExpr),
                                getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitVarPattern(ENode optOriginal,
                                  AtomicExpr nounExpr,
                                  EExpr optGuardExpr) {
        return new VarPattern(getOptSpan(optOriginal),
                              (AtomicExpr)xformEExpr(nounExpr),
                              xformEExpr(optGuardExpr),
                              getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitSlotPattern(ENode optOriginal,
                                   AtomicExpr nounExpr,
                                   EExpr optGuardExpr) {
        return new SlotPattern(getOptSpan(optOriginal),
                               (AtomicExpr)xformEExpr(nounExpr),
                               xformEExpr(optGuardExpr),
                               getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitIgnorePattern(ENode optOriginal, EExpr optGuardExpr) {
        return new IgnorePattern(getOptSpan(optOriginal),
                                 xformEExpr(optGuardExpr),
                                 getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitViaPattern(ENode optOriginal,
                                  EExpr viaExpr,
                                  Pattern subPattern) {
        return new ViaPattern(getOptSpan(optOriginal),
                              xformEExpr(viaExpr),
                              xformPattern(subPattern),
                              getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitListPattern(ENode optOriginal, Pattern[] subs) {
        return new ListPattern(getOptSpan(optOriginal),
                               xformPatterns(subs),
                               getOptScopeLayout());
    }

    /**************************** Other **************************/

    /**
     *
     */
    public EScript xformEScript(EScript eScript) {
        return (EScript)run(eScript);
    }

    /**
     *
     */
    public EMethod[] xformMethods(EMethod[] optMeths) {
        return (EMethod[])run(optMeths);
    }

    /**
     *
     */
    public EMatcher[] xformMatchers(EMatcher[] matchers) {
        return (EMatcher[])run(matchers);
    }

    /**
     *
     */
    public Object visitEScript(ENode optOriginal,
                               EMethod[] optMethods,
                               EMatcher[] matchers) {
        return new EScript(getOptSpan(optOriginal),
                           xformMethods(optMethods),
                           xformMatchers(matchers),
                           getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitEMethod(ENode optOriginal,
                               String docComment,
                               String verb,
                               Pattern[] patterns,
                               EExpr optResultGuard,
                               EExpr body) {
        KernelECopyVisitor t = nest();
        return new EMethod(getOptSpan(optOriginal),
                           docComment,
                           verb,
                           t.xformPatterns(patterns),
                           t.xformEExpr(optResultGuard),
                           t.xformEExpr(body),
                           getOptScopeLayout());
    }

    /**
     *
     */
    public Object visitEMatcher(ENode optOriginal,
                                Pattern pattern,
                                EExpr body) {
        KernelECopyVisitor t = nest();
        return new EMatcher(getOptSpan(optOriginal),
                            t.xformPattern(pattern),
                            t.xformEExpr(body),
                            getOptScopeLayout());
    }

    /**
     * Complains
     */
    public Object visitQuasiLiteralExpr(ENode optOriginal, int index) {
        T.fail("not in Kernel-E");
        return null; //make compiler happy
    }

    /**
     * Complains
     */
    public Object visitQuasiPatternExpr(ENode optOriginal, int index) {
        T.fail("not in Kernel-E");
        return null; //make compiler happy
    }

    /**
     * Complains
     */
    public Object visitQuasiLiteralPatt(ENode optOriginal, int index) {
        T.fail("not in Kernel-E");
        return null; //make compiler happy
    }

    /**
     * Complains
     */
    public Object visitQuasiPatternPatt(ENode optOriginal, int index) {
        T.fail("not in Kernel-E");
        return null; //make compiler happy
    }
}
