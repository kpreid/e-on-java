package org.erights.e.elang.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.IgnorePattern;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elib.base.SourceSpan;

/**
 * Just a bundling of state for use during parsing
 *
 * @author Mark S. Miller
 */
class ObjDecl {

    static private final EExpr[] NONE = {};

    static final ObjDecl EMPTY = new ObjDecl(null, "", null, NONE, NONE, null);

    private final SourceSpan myOptSpan;

    private final String myDocComment;

    private final Pattern myOptOName;

    private final EExpr[] mySupers;

    private final EExpr[] myAuditors;

    private final EScriptDecl myOptScript;

    /**
     *
     */
    private ObjDecl(SourceSpan optSpan,
                    String docComment,
                    Pattern optOName,
                    EExpr[] supers,
                    EExpr[] auditors,
                    EScriptDecl optScript) {
        myOptSpan = optSpan;
        myDocComment = docComment;
        myOptOName = optOName;
        mySupers = supers;
        myAuditors = auditors;
        myOptScript = optScript;
    }

    /**
     *
     */
    public SourceSpan getOptSpan() {
        return myOptSpan;
    }

    /**
     *
     */
    String getDocComment() {
        return myDocComment;
    }

    /**
     *
     */
    Pattern getOptOName() {
        return myOptOName;
    }

    /**
     *
     */
    Pattern getOName() {
//        T.notNull(myOptOName,
//                  "Internal: Missing qualified name");
        if (null == myOptOName) {
            return new IgnorePattern(null, null, null);
        } else {
            return myOptOName;
        }
    }

    /**
     *
     */
    EExpr getOptExtends() {
        if (0 == mySupers.length) {
            return null;
        } else if (1 == mySupers.length) {
            return mySupers[0];
        } else {
            T.fail("Only single inheritance allowed: " + this);
            return null; // make compiler happy
        }
    }

    /**
     *
     */
    public EExpr[] getSupers() {
        return mySupers;
    }

    /**
     *
     */
    EExpr[] getAuditors() {
        return myAuditors;
    }

    /**
     *
     */
    EScriptDecl getOptScript() {
        return myOptScript;
    }

    /**
     *
     */
    ObjDecl withOptSpan(SourceSpan optSpan) {
        return new ObjDecl(optSpan,
                           myDocComment,
                           myOptOName,
                           mySupers,
                           myAuditors,
                           myOptScript);
    }

    /**
     *
     */
    ObjDecl withDoco(Object doco) {
        return new ObjDecl(myOptSpan,
                           BaseENodeBuilder.docComment(doco),
                           myOptOName,
                           mySupers,
                           myAuditors,
                           myOptScript);
    }

    /**
     *
     */
    ObjDecl withOName(Object oName) {
        return new ObjDecl(myOptSpan,
                           myDocComment,
                           (Pattern)oName,
                           mySupers,
                           myAuditors,
                           myOptScript);
    }

    /**
     *
     */
    ObjDecl withExtends(BaseEBuilder b, Object extendsExpr) {
        EExpr[] supers = b.optExprs(extendsExpr);
        return new ObjDecl(myOptSpan,
                           myDocComment,
                           myOptOName,
                           supers,
                           myAuditors,
                           myOptScript);
    }

    /**
     *
     */
    ObjDecl withAuditors(BaseEBuilder b, Object auditors) {
        EExpr[] auditorExprs = b.optExprs(auditors);
        return new ObjDecl(myOptSpan,
                           myDocComment,
                           myOptOName,
                           mySupers,
                           auditorExprs,
                           myOptScript);
    }

    /**
     *
     */
    ObjDecl withScript(Object script) {
        return new ObjDecl(myOptSpan,
                           myDocComment,
                           myOptOName,
                           mySupers,
                           myAuditors,
                           (EScriptDecl)script);
    }
}
