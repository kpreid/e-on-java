package org.quasiliteral.term;

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.DeepPassByCopy;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Conformable;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.ListGuard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.ConstSet;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;

import java.io.IOException;
import java.math.BigInteger;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Like an Antlr {@link antlr.collections.AST AST}, but with differences that
 * make it suitable for quasiliteral processing.
 * <p/>
 * The differences are:<ul> <li>It knows how to convert to and from an Antlr
 * AST, in a way that mostly preserves the semantics of vanilla ASTs. <li>It's
 * PassByCopy, necessitating it be Immutable. <li>Each Term only points
 * downward, not rightward, making them sharable by different containing
 * contexts. <li>The functor is a {@link Term}, which is likewise like an Antlr
 * {@link antlr.Token}, rather than just storing a String and an int. <li>It
 * has a more conventional printed form, like a prolog term tree. </ul>
 * <p/>
 * New: A Term may not have both data and children. In other words,
 * data-holding terms are leaves, so only tag-terms can have a non-empty list
 * of children.
 *
 * @author Mark S. Miller
 * @author Many ideas from Danfuzz Bornstein
 * @author Many thanks also to Dean Tribble
 */
public final class Term extends Termish
  implements Persistent, DeepPassByCopy, Astro, Conformable {

    static private final long serialVersionUID = -8268521124096510369L;

    /**
     * Initialized lazily to avoid a circularity.
     * @noinspection StaticNonFinalField
     */
    static private StaticMaker OptTermMaker = null;

    /**
     * @noinspection StaticNonFinalField */
    static private ConstSet OptTermDataGuards = null;

    /**
     * Initialized lazily to avoid a circularity.
     * @noinspection StaticNonFinalField
     */
    static private ListGuard OptListOfTermsGuard = null;

    /**
     *
     */
    static private boolean isTermDataGuard(Guard guard) {
        if (null == OptTermDataGuards) {
            Guard[] termDataGuards = {ClassDesc.make(String.class),
              ClassDesc.make(Twine.class),
              ClassDesc.make(Character.TYPE),
              ClassDesc.make(Byte.TYPE),
              ClassDesc.make(Short.TYPE),
              ClassDesc.make(Integer.TYPE),
              ClassDesc.make(Long.TYPE),
              ClassDesc.make(BigInteger.class),
              ClassDesc.make(EInt.class),
              ClassDesc.make(Float.TYPE),
              ClassDesc.make(Double.TYPE),
              ClassDesc.make(Number.class)};
            //noinspection NonThreadSafeLazyInitialization
            OptTermDataGuards = ConstList.fromArray(termDataGuards).asSet();
        }
        return OptTermDataGuards.contains(guard);
    }

    static private ListGuard ListOfTermsGuard() {
        if (null == OptListOfTermsGuard) {
            ClassDesc TermGuard = ClassDesc.make(Term.class);
            //noinspection NonThreadSafeLazyInitialization
            OptListOfTermsGuard = ListGuard.THE_BASE.get(TermGuard);
        }
        return OptListOfTermsGuard;
    }

    /**
     * @serial Represents the token-type of the functor of this term.
     */
    private final AstroTag myTag;

    /**
     * @serial If the functor represents a literal-data token, then this is the
     * data, and myTag must represent the cononical corresponding token-type
     * for this kind of data in this schema.
     */
    private final Object myOptData;

    /**
     * @serial Where was source text that was originally lexed or parsed to
     * produce this term, or the functor of this term?
     */
    private final SourceSpan myOptSpan;

    /**
     * A term is a functor (the above three instance variables) as
     * parameterized by a list of argument Terms. These are the arguments. A
     * term of zero arguments is often refered to as a "functor", so there's no
     * information beyond the functor-part.
     */
    private final ConstList myArgs;

    /**
     * Just used to decide how to pretty print.
     * <p/>
     * Initialized lazily. 0 if uninitialized, so does not need to be
     * recalculated on revival.
     */
    private transient int myHeight = 0;

    /**
     * Makes a Term that represents a node in an abstract syntax tree, ie, a
     * Term tree.
     * <p/>
     * The invariants of a Term are not checked here, but rather are enforced
     * by the callers in this class and in TermBuilder. XXX Bug This
     * constructor is now public, so the invariants must be enforced here. They
     * aren't yet.
     *
     * @param tag     Identifies a token type in a particular grammar or set of
     *                related grammars, used as the functor (or "label") of
     *                this Term
     * @param optData Either something that promotes to a {@link Character},
     *                {@link EInt}, {@link Double}, or {@link Twine} or null.
     *                If not null, then the tag must represent the canonical
     *                literal type for this kind of data in this schema.
     * @param optSpan Where was the source text this token was extracted from?
     * @param args    This Term's argument list -- a list of Terms
     */
    public Term(AstroTag tag,
                Object optData,
                SourceSpan optSpan,
                ConstList args) {
        myTag = tag;
        myOptData = optData;
        myOptSpan = optSpan;
        // XXX For now, Terms may only contain Terms. See
        // https://sourceforge.net/tracker/index.php?func=detail&aid=1527406&
        // group_id=75274&atid=551529
        myArgs = (ConstList)ListOfTermsGuard().coerce(args, null);

        T.require(null == optData || 0 == args.size(),
                  "Term ",
                  tag.getTagName(),
                  " can't have both data and children");
    }

    /**
     * Uses 'makeTerm(myTag, myOptData, myOptSpan, myArgs)'
     */
    public Object[] getSpreadUncall() {
        if (null == OptTermMaker) {
            //noinspection NonThreadSafeLazyInitialization
            OptTermMaker = StaticMaker.make(Term.class);
        }
        Object[] result =
          {OptTermMaker, "run", myTag, myOptData, myOptSpan, myArgs};
        return result;
    }

    public AstroArg withOptSpan(SourceSpan optSpan) {
        return new Term(myTag, myOptData, optSpan, myArgs);
    }

    public Astro build(AstroBuilder builder) {
        Astro func;
        if (null == myOptData) {
            func = builder.leafTag(myTag, myOptSpan);
        } else {
            //Assumes tag adds no more info.
            func = builder.leafData(myOptData, myOptSpan);
        }
        AstroArg args = builder.empty();
        int len = myArgs.size();
        for (int i = 0; i < len; i++) {
            Astro arg = ((Term)myArgs.get(i)).build(builder);
            args = builder.seq(args, arg);
        }
        return builder.term(func, args);
    }

    /**
     * Represents the token-type of the functor of this term.
     */
    public AstroTag getTag() {
        return myTag;
    }

    /**
     *
     */
    public short getOptTagCode() {
        return myTag.getOptTagCode();
    }

    /**
     * Either literal data or null. If not null, then the tag must represent
     * the canonical literal type for this kind of data in this schema.
     */
    public Object getOptData() {
        return myOptData;
    }

    /**
     *
     */
    public String getOptString() {
        if (null != myOptData && myOptData instanceof Twine) {
            return ((Twine)myOptData).bare();
        } else {
            return null;
        }
    }

    /**
     *
     */
    public Object getOptArgData() {
        if (1 <= myArgs.size()) {
            return ((Term)myArgs.get(0)).getOptData();
        } else {
            return null;
        }
    }

    /**
     *
     */
    public Object getOptArgData(short tagCode) {
        T.require(tagCode == myTag.getOptTagCode(),
                  "Tag mismatch: ",
                  myTag,
                  " vs " + tagCode);
        return getOptArgData();
    }

    /**
     *
     */
    public String getOptArgString(short tagCode) {
        Object optArgData = getOptArgData(tagCode);
        if (null != optArgData && optArgData instanceof Twine) {
            return ((Twine)optArgData).bare();
        } else {
            return null;
        }
    }

    /**
     * What source text was originally lexed or parsed to produce this token?
     */
    public SourceSpan getOptSpan() {
        return myOptSpan;
    }

    /**
     * A term is a functor (the above three instance variables) as
     * parameterized by a list of argument Terms. These are the arguments. A
     * term of zero arguments is often refered to as a "functor", so there's no
     * information beyond the functor-part.
     */
    public ConstList getArgs() {
        return myArgs;
    }

    /**
     *
     */
    public Astro withoutArgs() {
        return new Term(myTag, myOptData, myOptSpan, ConstList.EmptyList);
    }

//    /**
//     *
//     */
//    public Astro withArgs(ConstList args) {
//        T.require(myArgs.size() == 0,
//                  "To use as functor, must not have args: ", this);
//        return new Term(myTag, myOptData, myOptSpan, args);
//    }

    /**
     * Lexicographic comparison of, in order:<ul> <li>the tags <li>the data
     * <li>the args </ul>
     */
    public double op__cmp(Term other) {
        //compare tags first
        double result = myTag.op__cmp(other.myTag);
        if (0.0 != result) {
            return result;
        }
        //then data
        if (null == myOptData) {
            if (null == other.myOptData) {
                //do nothing
            } else {
                //null is less than anything else
                return -1.0;
            }
        } else {
            if (null == other.myOptData) {
                //everything else is greater than null
                return 1.0;
            } else {
                //note that we only compare data when tags match, so the
                //comparison should never give a type mismatch exception
                result =
                  E.asFloat64(E.call(myOptData, "op__cmp", other.myOptData));
            }
        }
        if (0.0 != result) {
            return result;
        }
        //and finally, the args
        return myArgs.op__cmp(other.myArgs);
    }

    /**
     * What's the longest distance to the bottom?
     * <p/>
     * A leaf node is height 1. All other nodes are one more than the height of
     * their highest child. This is used for pretty printing.
     */
    public int getHeight() {
        if (0 >= myHeight) {
            myHeight = 1;
            for (int i = 0; i < myArgs.size(); i++) {
                int h = ((Term)myArgs.get(i)).getHeight();
                myHeight = StrictMath.max(myHeight, h + 1);
            }
        }
        return myHeight;
    }

    /**
     * A leaf Term can coerce to the kind of data it holds.
     * <p/>
     * If it has no data, then it can coerce to the tag's name.
     */
    public Object __conformTo(Guard guard) {
        if (0 == myArgs.size() && isTermDataGuard(guard)) {
            if (null == myOptData) {
                return myTag.getTagName();
            } else {
                return myOptData;
            }
        }
        return MirandaMethods.__conformTo(this, guard);
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out, boolean quasiFlag)
      throws IOException {
        String label;
        if (null == myOptData) {
            label = myTag.getTagName();
        } else if (myOptData instanceof Double || myOptData instanceof Float) {
            double data = ((Number)myOptData).doubleValue();
            // Note: if (Double.NaN == data) doesn't work
            if (Double.isNaN(data)) {
                label = "%NaN";
            } else if (Double.POSITIVE_INFINITY == data) {
                label = "%Infinity";
            } else if (Double.NEGATIVE_INFINITY == data) {
                label = "-%Infinity";
            } else {
                label = "" + data;
            }
        } else {
            label = E.toQuote(myOptData).bare();
        }
        if (quasiFlag) {
            label = StringHelper.replaceAll(label, "$", "$$");
            label = StringHelper.replaceAll(label, "@", "@@");
            label = StringHelper.replaceAll(label, "`", "``");
        }
        int h = getHeight();
        int reps;
        String open;
        String sep;
        String close;
        int numArgs = myArgs.size();
        if (".tuple.".equals(label)) {
            if (1 >= h) {
                out.print("[]");
                return;
            }
            reps = 1;
            open = "[";
            sep = ",";
            close = "]";
        } else if (".bag.".equals(label)) {
            if (1 >= h) {
                out.print("{}");
                return;
            }
            reps = 1;
            open = "{";
            sep = ",";
            close = "}";

        } else if (1 == numArgs &&
          ".bag.".equals(((Term)myArgs.get(0)).getTag().getTagName())) {

            out.print(label);
            reps = label.length();
            open = "";
            sep = null; // Never used
            close = "";

        } else if (2 == numArgs && ".attr.".equals(label)) {

            reps = 4;
            open = "";
            sep = ":";
            close = "";

        } else {
            out.print(label);
            if (1 >= h) {
                //If it's a leaf, don't show parens either
                return;
            }
            reps = label.length() + 1;
            open = "(";
            sep = ",";
            close = ")";
        }
        if (2 == h) {
            //If it only contains leaves, do it on one line
            out.print(open);
            ((Term)myArgs.get(0)).prettyPrintOn(out, quasiFlag);
            for (int i = 1; i < numArgs; i++) {
                out.print(sep, " ");
                ((Term)myArgs.get(i)).prettyPrintOn(out, quasiFlag);
            }
            out.print(close);
            return;
        }
        //print each child lined up.
        out.print(open);
        String spaces = StringHelper.multiply(" ", reps);
        TextWriter sub = out.indent(spaces);

        ((Term)myArgs.get(0)).prettyPrintOn(sub, quasiFlag);
        for (int i = 1; i < numArgs; i++) {
            sub.println(sep);
            ((Term)myArgs.get(i)).prettyPrintOn(sub, quasiFlag);
        }
        sub.print(close);
    }

    /**
     * Returns <tt>[this]</tt>
     */
    ConstList getTerms() {
        return ConstList.EmptyList.with(this);
    }

    /**
     *
     */
    void getTerms(FlexList list) {
        list.push(this);
    }

    /**
     * Returns 1
     */
    int getNumTerms() {
        return 1;
    }
}
