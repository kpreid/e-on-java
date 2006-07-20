package org.erights.e.elang.syntax;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.exception.NestedException;
import org.erights.e.elang.evm.AssignExpr;
import org.erights.e.elang.evm.AtomicExpr;
import org.erights.e.elang.evm.CallExpr;
import org.erights.e.elang.evm.CatchExpr;
import org.erights.e.elang.evm.DefineExpr;
import org.erights.e.elang.evm.EExpr;
import org.erights.e.elang.evm.EMatcher;
import org.erights.e.elang.evm.EMethod;
import org.erights.e.elang.evm.EScript;
import org.erights.e.elang.evm.EscapeExpr;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.FinallyExpr;
import org.erights.e.elang.evm.HideExpr;
import org.erights.e.elang.evm.IfExpr;
import org.erights.e.elang.evm.IgnorePattern;
import org.erights.e.elang.evm.ListPattern;
import org.erights.e.elang.evm.LiteralExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.ObjectExpr;
import org.erights.e.elang.evm.ParseNode;
import org.erights.e.elang.evm.Pattern;
import org.erights.e.elang.evm.SeqExpr;
import org.erights.e.elang.evm.SimpleNounExpr;
import org.erights.e.elang.evm.SlotExpr;
import org.erights.e.elang.evm.SlotPattern;
import org.erights.e.elang.evm.StaticScope;
import org.erights.e.elang.evm.ViaPattern;
import org.erights.e.elang.evm.VarPattern;
import org.erights.e.elang.evm.GuardedPattern;
import org.erights.e.elang.interp.Rune;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.syntax.LexerFace;
import org.quasiliteral.syntax.SyntaxException;

import java.io.IOException;

/**
 *
 * @author Mark S. Miller
 */
public abstract class BaseENodeBuilder implements BaseEBuilder {

    /**
     *
     */
    ConstMap myProps;

    /**
     *
     */
    final LexerFace myLexer;

    /**
     *
     */
    final TextWriter myWarns;

    /**
     *
     */
    BaseENodeBuilder(ConstMap props,
                     LexerFace lexer,
                     TextWriter warns) {
        myProps = props;
        myLexer = lexer;
        myWarns = warns;
    }

    /**
     *
     */
    static String docComment(Object doco) {
        if (doco instanceof String) {
            return (String)doco;
        } else {
            T.require(doco instanceof Astro,
                      "unrecognized type of doco: ", doco);
            Astro docTerm = (Astro)doco;
            return docTerm.getOptArgString(EParser.DocComment);
        }
    }

    /**
     * Should be put into a more generic place
     */
    static Object[] optTypedArray(Object optVec, Class elementType) {
        if (null == optVec) {
            return null;
        }
        if (elementType.isPrimitive()) {
            throw new IllegalArgumentException("only reference types");
        }
        if (optVec.getClass().isArray()) {
            optVec = ConstList.fromArray(optVec);
        }
        return (Object[])((EList)optVec).getArray(elementType);
    }

    /**
     *
     */
    static Pattern[] optPatterns(Object optVec) {
        return (Pattern[])optTypedArray(optVec, Pattern.class);
    }

    /**
     * XXX should be made properly polymorphic
     */
    static NounExpr noun(Object identOrStr) {
        return new SimpleNounExpr(optSpan(identOrStr),
                                  idStr(identOrStr),
                                  null);
    }

    /**
     *
     */
    static SourceSpan optSpan(Object poser) {
        if (null == poser) {
            return null;
        } else if (poser instanceof AstroArg) {
            return ((AstroArg)poser).getOptSpan();
        } else if (poser instanceof String) {
            return null;
        } else if (poser instanceof ParseNode) {
            SourceSpan optSpan = ((ParseNode)poser).getOptSpan();
            if (null == optSpan) {
                return null;
            } else {
                return optSpan.notOneToOne();
            }
        } else {
            T.fail("Internal: not a poser: " + E.toQuote(poser));
            return null; // make compiler happy
        }
    }

    /**
     *
     */
    static String idStr(Object identOrStr) {
        if (null == identOrStr) {
            return null;
        }
        if (identOrStr instanceof String) {
            return (String)identOrStr;
        }
        Astro astro = (Astro)identOrStr;
        short tagCode = astro.getTag().getOptTagCode();
        String result;
        if (EParser.ID == tagCode){
            result = astro.getOptArgString(EParser.ID);
        } else if (EParser.LiteralString == tagCode) {
            result = astro.getOptString();
        } else {
//            T.fail("Unrecognized tag: " + astro.getTag());
            result = astro.getTag().getTagName();
        }
        T.require(null != result,
                  "Identifier must not be null");
        return result;
    }

    /**
     *
     * @param poser XXX Currently ignored.
     * @param msg
     * @throws SyntaxException
     */
    public void syntaxError(Object poser, String msg) throws SyntaxException {
        try {
            myLexer.syntaxError(msg);
        } catch (SyntaxException sex) {
            SourceSpan optSpan = optSpan(poser);
            if (null == optSpan) {
                throw sex;
            } else {
                throw new NestedException(sex, "@ " + optSpan);
            }
        }
    }

    /**
     *
     * @param poser
     * @param msg
     */
    public void warning(Object poser, String msg) {
        try {
            myWarns.print("# warning: ", msg);
            SourceSpan optSpan = optSpan(poser);
            if (null != optSpan) {
                myWarns.print(": ", optSpan);
            } else {
                T.noop();
            }
            myWarns.println();
            myWarns.println();
        } catch (IOException e) {
            throw ExceptionMgr.asSafe(e);
        }

    }

    /**
     *
     */
    public void reserved(Object poser, String desc) {
        syntaxError(poser, "reserved: " + desc);
    }

    /**
     *
     */
    public void pocket(Object poser, String pName) {
        String propName = "e.enable." + pName;
        String propValue =
          ((String)myProps.fetch(propName, new ValueThunk("false"))).intern();
        if ("true" == propValue) {
            // succeed silently
        } else if ("warn" == propValue) {
            warning(poser,
                    "The optional " + propName +
                    " feature (see " + Rune.SYN_PROPS_PATH +
                    ") is set to \"warn\".");
        } else if ("false" == propValue || "allow" == propValue) {
            syntaxError(poser,
                        "The optional " + propName +
                        " feature (see " + Rune.SYN_PROPS_PATH +
                        ") is currently off.");
        } else {
            throw new IllegalArgumentException
              (propValue + " must be 'true', 'false', or 'allow'");
        }
    }

    /**
     *
     */
    public void antiPocket(Object poser, String pName) {
        String propName = "e.enable." + pName;
        String propValue =
          ((String)myProps.fetch(propName, new ValueThunk("false"))).intern();
        if ("true" == propValue) {
            syntaxError(poser,
                        "The optional " + propName +
                        " feature (see " + Rune.SYN_PROPS_PATH +
                        ") is currently on" +
                        " disallowing this construct.");
        } else if ("warn" == propValue) {
            warning(poser,
                    "The optional " + propName +
                    " feature (see " + Rune.SYN_PROPS_PATH +
                    ") is set to \"warn\".");
        } else if ("false" == propValue || "allow" == propValue) {
            // succeed silently
        } else {
            throw new IllegalArgumentException
              (propValue + " must be 'true', 'false', or 'allow'");
        }
    }

    /**
     *
     */
    Astro ident(Object identOrStr) {
        if (null == identOrStr) {
            return null;
        } else if (identOrStr instanceof Astro) {
            return (Astro)identOrStr;
        } else {
            String str = (String)identOrStr;
            return myLexer.composite(EParser.ID, str, null);
        }
    }

    /**
     *
     */
    public FlexList list() {
        return FlexList.make();
    }

    /**
     *
     */
    public FlexList list(Object a) {
        return with(list(), a);
    }

    /**
     *
     */
    public FlexList list(Object a, Object b) {
        return with(list(a), b);
    }

    /**
     *
     */
    public FlexList list(Object a, Object b, Object c) {
        return with(list(a, b), c);
    }

    /**
     *
     */
    public FlexList list(Object a, Object b, Object c, Object d) {
        return with(list(a, b, c), d);
    }

    /**
     *
     */
    public FlexList list(Object a, Object b, Object c, Object d, Object e) {
        return with(list(a, b, c, d), e);
    }

    /**
     *
     */
    public FlexList with(Object sofar, Object next) {
        FlexList result = (FlexList)sofar;
        result.push(next);
        return result;
    }

    /**
     *
     */
    public FlexList append(Object sofar, Object nexts) {
        FlexList result = (FlexList)sofar;
        Object[] nextArray = optTypedArray(nexts, Object.class);
        for (int i = 0; i < nextArray.length; i++) {
            result = with(result, nextArray[i]);
        }
        return result;
    }

    /**
     *
     */
    public LiteralExpr literal(Object tokenOrData) {
        Object data;
        if (tokenOrData instanceof Astro) {
            data = ((Astro)tokenOrData).getOptData();
        } else {
            data = tokenOrData;
        }
        if (data instanceof Twine) {
            data = ((Twine)data).bare();
        }
        //XXX tag with source position
        return new LiteralExpr(null, data, null);
    }

    /**
     *
     */
    public EExpr assign(Object lValue, Object rValue) {
        //XXX tag with source position
        return new AssignExpr(null,
                              (AtomicExpr)lValue,
                              forValue(rValue, null),
                              null);
    }

    /**
     *
     */
    public EExpr call(Object recipientExpr,
                      Object verb,
                      Object args) {
        return new CallExpr(optSpan(verb),
                            forValue(recipientExpr, null),
                            idStr(verb),
                            optExprs(args),
                            null);
    }

    /**
     *
     */
    public EExpr kerneldef(Object pattern,
                           Object optEjectorExpr,
                           Object rValue) {
        return new DefineExpr(null,
                              (Pattern)pattern,
                              forValue(optEjectorExpr, null),
                              forValue(rValue, null),
                              null);
    }

    /**
     *
     */
    public EExpr slotExpr(Object poser, Object eExpr) {
        AtomicExpr noun = (AtomicExpr)eExpr;
        return new SlotExpr(noun.getOptSpan(), noun, null);
    }

    /**
     *
     */
    public EExpr escape(Object pattern,
                        Object bodyExpr,
                        Object optArgPattern,
                        Object optCatcher) {
        return new EscapeExpr(null,
                              (Pattern)pattern,
                              forValue(bodyExpr, StaticScope.EmptyScope),
                              (Pattern)optArgPattern,
                              (null == optCatcher) ? null :
                                forValue(optCatcher, StaticScope.EmptyScope),
                              null);
    }

    /**
     *
     */
    public EExpr hide(Object body) {
        return new HideExpr(null,
                            forValue(body, StaticScope.EmptyScope),
                            null);
    }

    /**
     *
     */
    public EExpr ifx(Object condExpr, Object thenExpr, Object elseExpr) {
        return new IfExpr(null,
                          (EExpr)condExpr,
                          forValue(thenExpr, StaticScope.EmptyScope),
                          forValue(elseExpr, StaticScope.EmptyScope),
                          null);
    }

    /**
     *
     */
    public EMatcher matcher(Object matchHead, Object bodyExpr) {
        return new EMatcher(null,
                            (Pattern)matchHead,
                            forValue(bodyExpr, StaticScope.EmptyScope),
                            null);
    }

    /**
     *
     */
    public EMethod method(Object doco, Object msgPatt, Object bodyExpr) {
        MsgPatt patt = (MsgPatt)msgPatt;
        return new EMethod(patt.getOptSpan(),
                           docComment(doco),
                           patt.getVerb(),
                           patt.getPatterns(),
                           patt.getOptResultGuard(),
                           forValue(bodyExpr, StaticScope.EmptyScope),
                           null);
    }

    /**
     *
     */
    public MsgPatt methHead(Object verb, Object patts, Object optResultGuard) {
        return new MsgPatt(optSpan(verb),
                           idStr(verb),
                           optPatterns(patts),
                           forValue(optResultGuard, null));
    }

    /**
     *
     */
    public EScriptDecl vTable(Object optMethods,
                              Object matchers) {
        return new EScriptDecl((EMethod[])optTypedArray(optMethods,
                                                        EMethod.class),
                               (EMatcher[])optTypedArray(matchers,
                                                         EMatcher.class));
    }

    /**
     *
     */
    EExpr object(String docComment,
                 GuardedPattern oName,
                 EExpr[] impls,
                 EScript script) {
        return new ObjectExpr(null,
                              docComment,
                              oName,
                              impls,
                              script,
                              null);
    }

    /**
     * XXX Currently, parts.length must be >= 1
     */
    public EExpr sequence(EExpr[] parts) {
        return new SeqExpr(null, parts, null);
    }

    /**
     *
     */
    public EExpr tryx(Object eExpr,
                      Object optCatchers,
                      Object optFinally) {
        if (null == optCatchers) {
            if (null == optFinally) {
                return hide(eExpr);
            } else {
                return new FinallyExpr(null,
                                       forValue(eExpr, StaticScope.EmptyScope),
                                       forValue(optFinally, StaticScope.EmptyScope),
                                       null);
            }
        } else {
            EMatcher catcher = (EMatcher)optCatchers;
            EExpr result = new CatchExpr(null,
                                         forValue(eExpr, StaticScope.EmptyScope),
                                         catcher.getPattern(),
                                         catcher.getBody(),
                                         null);
            if (null == optFinally) {
                return result;
            } else {
                return new FinallyExpr(null,
                                       result,
                                       forValue(optFinally, StaticScope.EmptyScope),
                                       null);
            }
        }
    }

    /**
     *
     */
    AtomicExpr atomic(Object atom) {
        if (null == atom) {
            return null;
        } else if (atom instanceof AtomicExpr) {
            return (AtomicExpr)atom;
        } else {
            return new SimpleNounExpr(optSpan(atom), idStr(atom), null);
        }
    }

    /**
     *
     */
    public Pattern finalPattern(Object atom) {
        return finalPattern(atom, null);
    }

    /**
     *
     */
    public Pattern finalPattern(Object atom, Object optGuardExpr) {
        return new FinalPattern(null,
                                atomic(atom),
                                forValue(optGuardExpr, null),
                                null);
    }

    /**
     *
     */
    public Pattern varPattern(Object atom) {
        return varPattern(atom, null);
    }

    /**
     *
     */
    public Pattern varPattern(Object atom, Object optGuardExpr) {
        return new VarPattern(null,
                              atomic(atom),
                              forValue(optGuardExpr, null),
                              null);
    }

    /**
     *
     */
    public Pattern slotPattern(Object atom) {
        return slotPattern(atom, null);
    }

    /**
     *
     */
    public Pattern slotPattern(Object atom, Object optGuardExpr) {
        return new SlotPattern(null,
                               atomic(atom),
                               forValue(optGuardExpr, null),
                               null);
    }

    /**
     *
     */
    public Pattern ignore() {
        return ignore(null);
    }

    public Pattern ignore(Object optGuardExpr) {
        return new IgnorePattern(null,
                                 forValue(optGuardExpr, null),
                                 null);
    }

    /**
     *
     */
    public ListPattern listPattern(Object subs) {
        return new ListPattern(null,
                               optPatterns(subs),
                               null);
    }

    /**
     *
     */
    public Pattern via(Object viaExpr, Object subPattern) {
        return new ViaPattern(null,
                              forValue(viaExpr, null),
                              (Pattern)subPattern,
                              null);
    }

    /**
     *
     */
    public EExpr[] optExprs(Object optVec) {
        EExpr[] result = (EExpr[])optTypedArray(optVec, EExpr.class);
        for (int i = 0, len = result.length; i < len; i++) {
            result[i] = forValue(result[i], null);
        }
        return result;
    }
}
