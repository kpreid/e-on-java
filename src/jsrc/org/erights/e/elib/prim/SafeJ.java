package org.erights.e.elib.prim;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.tables.FlexSet;
import org.erights.e.elib.tables.IdentityCacheTable;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.net.URLSugar;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.term.Term;
import org.quasiliteral.term.TermParser;

import java.io.IOException;
import java.net.URL;

/**
 * @author Mark S. Miller
 */
public final class SafeJ {

    /**
     * For classes for which there's no safej file.
     * <p/>
     * XXX What happens if a class has a safej file and is listed here as
     * well?
     */
    static private final String[] ApprovedClassList = {"boolean",
      "char",
      "byte",
      "short",
      "int",
      "long",
      "float",
      "double",
      "void",

      "com.hp.orc.OrcLexer",
      "com.hp.orc.OrcParser",

      "java.lang.Boolean",
      "java.lang.Byte",
      "java.lang.Character",
      "java.lang.Comparable",
      "java.lang.Double",
      "java.lang.Float",
      "java.lang.Integer",
      "java.lang.Long",
      "java.lang.Number",
      "java.lang.Object",
      "java.lang.Runnable",
      "java.lang.RuntimeException",
      //must enforce DeepPassByCopy
      "java.lang.Short",
      "java.lang.StrictMath",
      "java.lang.String",
      "java.lang.StringBuffer",
      "java.lang.Throwable",
      //XXX must enforce DeepPassByCopy
      "java.lang.Void",

      "java.math.BigInteger",

      "java.io.ByteArrayInputStream",
      "java.io.ByteArrayOutputStream",
      "java.io.DataInputStream",
      "java.io.DataOutputStream",

      "java.security.KeyFactory",
      "java.security.KeyPair",
      "java.security.spec.DSAPrivateKeySpec",
      "java.security.spec.DSAPublicKeySpec",
      "java.security.spec.RSAPrivateKeySpec",
      "java.security.spec.RSAPublicKeySpec",

      "net.captp.jcomm.SturdyRef",
      "net.captp.jcomm.ObjectID",
      "net.vattp.data.NetConfig",

      "org.apache.oro.text.regex.Perl5Compiler",
      "org.apache.oro.text.regex.Perl5Matcher",
      "org.apache.oro.text.regex.PatternMatcherInput",

      "org.capml.dom.Element",
      "org.capml.dom.Text",
      "org.capml.quasi.QuasiContentExprHole",
      "org.capml.quasi.QuasiContentList",
      "org.capml.quasi.QuasiContentPattHole",
      "org.capml.quasi.QuasiElement",
      "org.capml.quasi.QuasiText",
      "org.capml.quasi.XMLQuasiParser",

      "org.erights.e.develop.format.ETimeFormat",

      "org.erights.e.elang.evm.AssignExpr",
      "org.erights.e.elang.evm.CallExpr",
      "org.erights.e.elang.evm.CatchExpr",
      "org.erights.e.elang.evm.DefineExpr",
      "org.erights.e.elang.evm.EMatcher",
      "org.erights.e.elang.evm.EMethod",
      "org.erights.e.elang.evm.EMethodNode",
      "org.erights.e.elang.evm.ENode",
      "org.erights.e.elang.evm.EscapeExpr",
      "org.erights.e.elang.evm.EScript",
      "org.erights.e.elang.evm.FinallyExpr",
      "org.erights.e.elang.evm.FinalPattern",
      "org.erights.e.elang.evm.FrameFinalNounExpr",
      "org.erights.e.elang.evm.FrameSlotNounExpr",
      "org.erights.e.elang.evm.HideExpr",
      "org.erights.e.elang.evm.IfExpr",
      "org.erights.e.elang.evm.IgnorePattern",
      "org.erights.e.elang.evm.ListPattern",
      "org.erights.e.elang.evm.LiteralExpr",
      "org.erights.e.elang.evm.LiteralNounExpr",
      "org.erights.e.elang.evm.LiteralSlotNounExpr",
      "org.erights.e.elang.evm.LocalFinalNounExpr",
      "org.erights.e.elang.evm.LocalSlotNounExpr",
      "org.erights.e.elang.evm.MetaContextExpr",
      "org.erights.e.elang.evm.MetaStateExpr",
      "org.erights.e.elang.evm.NounExpr",
      "org.erights.e.elang.evm.NounPattern",
      "org.erights.e.elang.evm.ObjectExpr",
      "org.erights.e.elang.evm.OuterNounExpr",
      "org.erights.e.elang.evm.QuasiLiteralExpr",
      "org.erights.e.elang.evm.QuasiLiteralPatt",
      "org.erights.e.elang.evm.QuasiPatternExpr",
      "org.erights.e.elang.evm.QuasiPatternPatt",
      "org.erights.e.elang.evm.ParseNode",
      "org.erights.e.elang.evm.SeqExpr",
      "org.erights.e.elang.evm.SimpleNounExpr",
      "org.erights.e.elang.evm.SlotExpr",
      "org.erights.e.elang.evm.SlotPattern",
      "org.erights.e.elang.evm.StaticScope",

      "org.erights.e.elang.interp.E4E",
      "org.erights.e.elang.interp.Loop",
      "org.erights.e.elang.interp.ProtocolDesc",

//        "org.erights.e.elang.scope.Scope",
      "org.erights.e.elang.scope.ScopeLayout",
      "org.erights.e.elang.scope.StaticContext",

      "org.erights.e.elang.smallcaps.SmallcapsExpr",

      "org.erights.e.elang.syntax.ELexer",
      "org.erights.e.elang.syntax.EParser",
      "org.erights.e.elang.syntax.PrettyFeeder",

      "org.erights.e.elang.visitors.VerifyEVisitor",
      "org.erights.e.elang.visitors.KernelECopyVisitor",

      "org.erights.e.elib.base.MessageDesc",
      "org.erights.e.elib.base.ParamDesc",
      "org.erights.e.elib.base.SourceSpan",
      "org.erights.e.elib.base.ValueThunk",

      "org.erights.e.elib.deflect.Callback",
      "org.erights.e.elib.deflect.Deflector",

      "org.erights.e.elib.eio.EIO",

      "org.erights.e.elib.oldeio.TextWriter",
      "org.erights.e.elib.oldeio.UnQuote",

      "org.erights.e.elib.prim.E",
      "org.erights.e.elib.prim.Selector",
      "org.erights.e.elib.prim.Thrower",

      "org.erights.e.elib.ref.Ref",

      "org.erights.e.elib.sealing.Brand",
      "org.erights.e.elib.sealing.crypto.Signer",
      "org.erights.e.elib.sealing.crypto.Verifier",

      "org.erights.e.elib.serial.Loader",
      "org.erights.e.elib.serial.PersistentAuditor",
      "org.erights.e.elib.serial.RemoteCall",
      "org.erights.e.elib.serial.RemoteDelivery",
      "org.erights.e.elib.serial.Serializer",
      "org.erights.e.elib.serial.Uncaller",
      "org.erights.e.elib.serial.Unserializer",

      "org.erights.e.elib.slot.AnyGuard",
      "org.erights.e.elib.slot.BaseAuditor",
      "org.erights.e.elib.slot.FinalSlot",
      "org.erights.e.elib.slot.ListGuard",
      "org.erights.e.elib.slot.MapGuard",
      "org.erights.e.elib.slot.NullOkGuard",
      "org.erights.e.elib.slot.SimpleSlot",
      "org.erights.e.elib.slot.SettableSlot",
      "org.erights.e.elib.slot.TupleGuard",
      "org.erights.e.elib.slot.VoidGuard",

      "org.erights.e.elib.tables.ConstList",
      "org.erights.e.elib.tables.ConstMap",
      "org.erights.e.elib.tables.ConstSet",
      "org.erights.e.elib.tables.TraversalKey",
      "org.erights.e.elib.tables.EList",
      "org.erights.e.elib.tables.EMap",
      "org.erights.e.elib.tables.ESet",
      "org.erights.e.elib.tables.FlexTrijection",
      "org.erights.e.elib.tables.FlexList",
      "org.erights.e.elib.tables.FlexMap",
      "org.erights.e.elib.tables.FlexSet",
      "org.erights.e.elib.tables.Twine",
      "org.erights.e.elib.vat.Queue",

      //must enforce DeepPassByCopy
      "org.erights.e.elib.util.TwineException",

      "org.erights.e.meta.java.math.EInt",

      "org.quasiliteral.astro.AstroTag",
      "org.quasiliteral.astro.BaseSchema",

      "org.quasiliteral.quasiterm.QAtHole",
      "org.quasiliteral.quasiterm.QBuilder",
      "org.quasiliteral.quasiterm.QDollarHole",
      "org.quasiliteral.quasiterm.QEmptySeq",
      "org.quasiliteral.quasiterm.QFunctor",
      "org.quasiliteral.quasiterm.QPairSeq",
      "org.quasiliteral.quasiterm.QSome",
      "org.quasiliteral.quasiterm.QTerm",

      "org.quasiliteral.syntax.URIKit",
      "org.quasiliteral.syntax.TwineFeeder",

      "org.quasiliteral.term.GrammarParser",
      "org.quasiliteral.term.QuasiBuilderAdapter",
      "org.quasiliteral.term.Term",
      "org.quasiliteral.term.TermBuilder",
      "org.quasiliteral.term.TermLexer",
      "org.quasiliteral.term.TermParser",

      "org.quasiliteral.text.FirstCharSplitter",
      "org.quasiliteral.text.Identifiers",
      "org.quasiliteral.text.Substituter",};

    static private final FlexSet ApprovedClasses;

    static {
        int len = ApprovedClassList.length;
        ApprovedClasses = FlexSet.fromType(String.class, len);
        for (int i = 0; i < len; i++) {
            ApprovedClasses.addElement(ApprovedClassList[i], true);
        }
    }

    /**
     *
     */
    static private final String[] ALWAYS_REMOVE = {"clone()",
      "equals(Object)",
      "finalize()",
      "getClass()",
      "hashCode()",
      "notify()",
      "notifyAll()",
      "toString()",
      "wait()",
      "wait(long)",
      "wait(long, int)"};

    /**
     *
     */
    static private final ConstMap AlwaysRemove;

    static {
        FlexMap map =
          FlexMap.fromTypes(String.class, String.class, ALWAYS_REMOVE.length);
        for (int i = 0, len = ALWAYS_REMOVE.length; i < len; i++) {
            map.put(ALWAYS_REMOVE[i], "reject", true);
        }
        AlwaysRemove = map.snapshot();
    }

    /**
     *
     */
    static private final IdentityCacheTable SAFEJ_CACHE =
      new IdentityCacheTable(Term.class, 100);

//    static private final String[] SafeJTagNames = {
//        ".char.",
//        ".int.",
//        ".float64.",
//        ".String.",
//
//        "class",
//        "name",
//        "method",
//        "static",
//        "signature",
//        "suppress",
//        "comment",
//        "byproxy",
//        "selfless",
//        "byconstruction",
//        "persistent",
//        "safe",
//        "honorary"
//    };

//    /**
//     *
//     */
//    static private AstroSchema SAFEJ_SCHEMA = null;
//
//    /**
//     *
//     */
//    static public AstroSchema GetSafeJSchema() {
//        if (null == SAFEJ_SCHEMA) {
//            SAFEJ_SCHEMA =
//              new BaseSchema("safej",
//                             ConstList.fromArray(SafeJTagNames));
//        }
//        return SAFEJ_SCHEMA;
//    }
//
//    /**
//     *
//     */
//    static private AstroBuilder SAFEJ_BUILDER = null;
//
//    /**
//     *
//     */
//    static public AstroBuilder GetSafeJBuilder() {
//        if (null == SAFEJ_BUILDER) {
//            SAFEJ_BUILDER = new TermBuilder(GetSafeJSchema());
//        }
//        return SAFEJ_BUILDER;
//    }
//
//    /**
//     *
//     */
//    static private QuasiBuilder SAFEJ_QBUILDER = null;
//
//    /**
//     *
//     */
//    static public QuasiBuilder GetSafeJQBuilder() {
//        if (null == SAFEJ_QBUILDER) {
//            SAFEJ_QBUILDER = new QuasiBuilderAdaptor(GetSafeJBuilder());
//        }
//        return SAFEJ_QBUILDER;
//    }

    /**
     *
     */
    static public Term getOptSafeJTerm(String fqName) {
        Twine tfqn = Twine.fromString(fqName);
        Term optResult = (Term)SAFEJ_CACHE.fetch(tfqn, ValueThunk.NULL_THUNK);
        if (null != optResult) {
            return optResult;
        }
        String path = StringHelper.replaceAll(fqName, ".", "/") + ".safej";
        URL optTermURL = ClassLoader.getSystemResource(path);
        if (null == optTermURL) {
            return null;
        }
        String termSrc;
        try {
            termSrc = URLSugar.getText(optTermURL);
        } catch (IOException ioe) {
            throw ExceptionMgr.asSafe(ioe);
        }
// XXX Bug: Investigate why the commented out version doesn't work.
//        Term result = (Term)TermParser.run(Twine.fromString(termSrc),
//                                           GetSafeJQBuilder());
        Term result = TermParser.run(Twine.fromString(termSrc));
        SAFEJ_CACHE.put(tfqn, result);
        return result;
    }

    /**
     * Is clazz approved at this safety level?
     * <p/>
     * If approved when safe==true this means a {@link StaticMaker} on clazz
     * follows capability discipline and provides no authority, and so can be
     * imported with &lt;import:...&gt;.
     * <p/>
     * If approved when safe==false, then a StaticMaker on clazz can be
     * imported with &lt;unsafe:...&gt;, even once we enforce the invisibility
     * of untamed classes.
     */
    static public boolean approve(Class clazz, boolean safe) {
        String fqName = clazz.getName();
        if (ApprovedClasses.contains(fqName)) {
            return true;
        }
        if (clazz.isArray()) {
            //Array types are safe
            ApprovedClasses.addElement(fqName, true);
            return true;
        }
        Term optTerm = getOptSafeJTerm(fqName);
        if (null == optTerm) {
            return false;
        }
        if (safe) {
            return null !=
              AstroTag.optFirstAttribute(optTerm, "class", "safe");
        } else {
            return true;
        }
    }

    /**
     *
     */
    static SafeJ getSafeJ(Class clazz, Class optSugar, boolean staticFlag) {
        SafeJ result = ALL;
        Term optSafeJTerm = getOptSafeJTerm(clazz.getName());
        if (null != optSafeJTerm) {
            //Hard code Term-tree walking in order to avoid having elib depend
            //on quasi-Terms
            String attrName = staticFlag ? "statics" : "methods";
            Astro optMeths =
              AstroTag.optAttribute(optSafeJTerm, "class", attrName);
            if (null != optMeths) {
                ConstList methList = optMeths.getArgs();
                FlexMap safeJMap = FlexMap.fromTypes(String.class,
                                                     String.class,
                                                     methList.size());
                for (int i = 0, len = methList.size(); i < len; i++) {
                    Term meth = (Term)methList.get(i);
                    Term methArg0 = (Term)meth.getArgs().get(0);
                    String arg0tag = "allow";
                    String optSig = methArg0.getOptString();
                    if (null == optSig) {
                        arg0tag = methArg0.getTag().getTagName();
                        Term methArg1 = (Term)meth.getArgs().get(1);
                        optSig = methArg1.getOptString();
                    }
                    T.require(1 <= optSig.indexOf('('),
                              "Must be a method signature: ",
                              optSig);
                    safeJMap.put(optSig, arg0tag, true);
                }
                result =
                  new SafeJ(clazz.getName(), safeJMap.snapshot(), false);
            }
        }
        return result;
    }

//    /**
//     * Gets the value of clazz's static public field named 'fieldName'.
//     * <p>
//     * If there is no such field, return <tt>insteadThunk()</tt> instead.
//     */
//    static Object getStaticValue(Class clazz,
//                                 String fieldName,
//                                 Object instead) {
//        Field staticField;
//        try {
//            staticField = clazz.getField(fieldName);
//        } catch (NoSuchFieldException nsfe) {
//            return instead;
//        }
//        try {
//            return staticField.get(null);
//        } catch (IllegalAccessException iae) {
//            throw ExceptionMgr.asSafe(iae);
//        }
//    }

    static public final SafeJ NONE =
      new SafeJ("none", ConstMap.EmptyMap, false);

    static public final SafeJ ALL = new SafeJ("all", null, false);

    ///////////////////// instance stuff ////////////////////

    /**
     *
     */
    private final String myBaseName;

    /**
     *
     */
    private final ConstMap myOptMap;

    /**
     *
     */
    private final boolean myInheritFlag;

    /**
     *
     */
    public SafeJ(String baseName, ConstMap optMap, boolean inheritFlag) {
        myBaseName = baseName;
        myOptMap = optMap;
        myInheritFlag = inheritFlag;
    }

    /**
     *
     */
    public String toString() {
        if (myInheritFlag) {
            return "<SafeJ inherit for " + myBaseName + ">";
        } else {
            return "<SafeJ " + myBaseName + ">";
        }
    }

    /**
     * Only for debugging purposes.
     */
    public String getBaseName() {
        return myBaseName;
    }

    /**
     * An optional mapping from flat signatures to interned strings
     * representing an enumeration of cases of what to do with methods matching
     * that signature. <p><ul> <li>If myOptMap is null, then all methods may be
     * added or inherited. <li>If the signature is absent or maps to null, then
     * the method may be inherited but not added. This corresponds to the
     * absence of an entry for the method in the safej file, but does not
     * correspond to the absence of a tag on the method. The absence of a tag
     * is treated as an implicit "allow" and translated on input. <li>If the
     * signature maps to <b>allow</b>, then the method may be added or
     * inherited. "allow" should only be used where the method is introduced,
     * or a warning will be traced. The signature should only be enumerated by
     * subclasses of the introducing class in order to "reject" it. <li>If the
     * signature maps to <b>suppress</b>, then the method not may be added, or
     * inherited. "suppress" should only be used where the method is
     * introduced, not where it is inherited, or a warning will be traced. In
     * order to suppress the inheriting of a method allowed by a supertype, one
     * should use "reject" instead. <li>If the signature maps to <b>reject</b>,
     * then the method not may be added, or inherited. "reject" should only be
     * used where the method is inherited, not where it is introduced, or a
     * warning will be traced. In order to suppress the introduction of a
     * method, one should use "suppress" instead. <li>If the signature maps to
     * anything else, an error is traced and the method may not be added or
     * inherited. </ul> XXX Some of the warnings above are not yet implemented,
     * and will require enhancements to this API in order to support.
     */
    public ConstMap getOptMap() {
        return myOptMap;
    }

    /**
     * Is this SafeJ object being used to check addition or inheritance?
     */
    public boolean getInheritFlag() {
        return myInheritFlag;
    }

    /**
     * @see #getOptMap()
     */
    public boolean shouldAllow(String flatSig) {
        if (AlwaysRemove.maps(flatSig)) {
            return false;
        }
        if (null == myOptMap) {
            return true;
        }
        String optVal = (String)myOptMap.fetch(flatSig, ValueThunk.NULL_THUNK);
        if (null == optVal) {
            return myInheritFlag;
        }
        if ("allow" == optVal) {
            //XXX should warn if we're allowing an addition of a method that's
            //already inherited.
            return true;
        }
        if ("suppress" == optVal) {
            if (myInheritFlag) {
                //XXX should use the tracing system
                System.err
                  .println("*** safej warning: should reject " +
                    "rather than suppress " + myBaseName + "." + flatSig);
            }
            return false;
        }
        if ("reject" == optVal) {
            if (!myInheritFlag) {
                //XXX should use the tracing system
//                System.err.println("*** safej warning: should suppress " +
//                                   "rather than reject " +
//                                   myBaseName + "." + flatSig);
            }
            return false;
        }
        System.err
          .println("*** safej error: unrecognized tag \"" + optVal + " for " +
            myBaseName + "." + flatSig);
        return false;
    }

    /**
     *
     */
    public SafeJ or(SafeJ other, boolean strict) {
        ConstMap optNewMap;
        if (null == myOptMap) {
            if (null == other.myOptMap) {
                optNewMap = null;
            } else {
                //XXX null is also a plausible answer
                optNewMap = other.myOptMap;
            }
        } else {
            if (null == other.myOptMap) {
                //XXX null is also a plausible answer
                optNewMap = myOptMap;
            } else {
                optNewMap = myOptMap.or(other.myOptMap, strict);
            }
        }
        return new SafeJ(myBaseName, optNewMap, myInheritFlag);
    }

    /**
     *
     */
    public SafeJ forInheritance() {
        return new SafeJ(myBaseName, myOptMap, true);
    }
}
