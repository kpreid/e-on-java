package org.erights.e.elang.visitors;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.ENode;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elang.evm.SimpleNounExpr;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexMap;

/**
 * Transforms Expanded-E or (unbound or bound) Kernel-E to bound Kernel-E,
 * verifying in the process.
 * <p>
 * This transformation turns all qualified names into fully qualified names,
 * sets the ScopeLayouts to ScopeLayouts it newly calculates (ignoring the old
 * ones), and expands out certain contructs that are part of Expanded-E but
 * not Kernel-E -- "meta.getState()", circular defines, XXX more to come...
 *
 * @author Mark S. Miller
 */
public class VerifyEVisitor extends BaseBindVisitor {

    /**
     *
     */
    public VerifyEVisitor(ScopeLayout bindings) {
        super(bindings);
    }

    /**
     *
     */
    KernelECopyVisitor nest(GuardedPattern oName) {
        return new VerifyEVisitor(myLayout.nest(oName.getOptName()));
    }

    /**
     *
     */
    KernelECopyVisitor nest() {
        return new VerifyEVisitor(myLayout.nest());
    }

    /**
     *
     */
    private VerifyEVisitor nestObject(ConstMap newSynEnv) {
        ScopeLayout inner = ScopeLayout.make(-1,
                                             newSynEnv,
                                             myLayout.getFQNPrefix());
        return new VerifyEVisitor(inner.nest());
    }

    /**
     *
     */
    NounExpr newFinal(SourceSpan optSpan, String varName) {
        return new SimpleNounExpr(optSpan, varName, getOptScopeLayout());
    }

    /**
     *
     */
    NounExpr newVar(SourceSpan optSpan, String varName) {
        return new SimpleNounExpr(optSpan, varName, getOptScopeLayout());
    }

    /**************************** EExprs **************************/

    /**
     *
     */
    public Object visitObjectExpr(ENode optOriginal,
                                  String docComment,
                                  GuardedPattern oName,
                                  EExpr[] auditors,
                                  EScript eScript) {
        GuardedPattern guarded = (GuardedPattern)xformPattern(oName);
        VerifyEVisitor t = (VerifyEVisitor)nest(guarded);
        EExpr[] xauds = t.xformEExprs(auditors);

        StaticScope ss = eScript.staticScope();
        ConstMap usedMap = ss.namesUsed();
        String[] used = (String[])usedMap.getKeys(String.class);
        FlexMap newSynEnv = FlexMap.fromTypes(String.class,
                                              NounPattern.class,
                                              used.length);
        for (int i = 0, max = used.length; i < max; i++) {
            String varName = used[i];
            NounPattern optNamer = t.myLayout.getOptPattern(varName);
            if (null == optNamer) {
                ParseNode.fail("Undefined variable: " + varName,
                               (ENode)usedMap.get(varName));
            }
            newSynEnv.put(varName, optNamer);
        }
        VerifyEVisitor nested = t.nestObject(newSynEnv.snapshot());
        return new ObjectExpr(getOptSpan(optOriginal),
                              docComment,
                              guarded,
                              xauds,
                              nested.xformEScript(eScript),
                              getOptScopeLayout());
    }

    /**
     * XXX We should expand a 'meta.getState()' expression into an inline
     * ConstMap creation expression representing the current environment.
     */
    public Object visitMetaStateExpr(ENode optOriginal) {
        return super.visitMetaStateExpr(optOriginal);
    }
}
