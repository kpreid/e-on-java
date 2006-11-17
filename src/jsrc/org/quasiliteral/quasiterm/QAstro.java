package org.quasiliteral.quasiterm;

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.elib.util.OneArgFunc;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.base.MatchMaker;
import org.quasiliteral.base.ValueMaker;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public abstract class QAstro extends QAstroArg
  implements Astro, ValueMaker, MatchMaker {

    static private final long serialVersionUID = 7338296043865833760L;

    /**
     *
     */
    QAstro(AstroBuilder builder, SourceSpan optSpan) {
        super(builder, optSpan);
    }

    /**
     * @return :Astro
     */
    public Object substitute(ConstList args) {
        ConstList list = substSlice(args, EMPTY_INDEX);
        T.require(1 == list.size(), "Must be singleton: ", list);
        return list.get(0);
    }

    /**
     *
     */
    public ConstList matchBind(ConstList args,
                               Object specimen,
                               OneArgFunc optEjector) {
        FlexList bindings = FlexList.make();
        if (1 == matchBindSlice(args,
                                ConstList.EmptyList.with(specimen),
                                bindings,
                                EMPTY_INDEX)) {
            return bindings.snapshot();
        } else {
            throw Thrower.toEject(optEjector,
                                  "" + this + " doesn't match: " + specimen);
        }
    }

    /**
     *
     */
    public Astro build(AstroBuilder builder) {
        T.fail("Can't build. Try qbuild instead");
        return null; //make compiler happy
    }

    /**
     *
     */
    public short getOptTagCode() {
        return getTag().getOptTagCode();
    }

    /**
     *
     */
    public String getOptString() {
        return ((Twine)getOptData()).bare();
    }

    /**
     *
     */
    public Object getOptArgData() {
        if (1 != getArgs().size()) {
            return null;
        }
        QAstroArg qaa = (QAstroArg)getArgs().get(0);
        if (!(qaa instanceof QAstro)) {
            return null;
        }
        return ((QAstro)qaa).getOptData();
    }

    /**
     *
     */
    public Object getOptArgData(short tagCode) {
        T.require(tagCode == getTag().getOptTagCode(),
                  "Tag mismatch: ",
                  getTag(),
                  " vs " + tagCode);
        return getOptArgData();
    }

    /**
     *
     */
    public String getOptArgString(short tagCode) {
        return ((Twine)getOptArgData(tagCode)).bare();
    }

    /**
     * Returns a variant of this Astro that may serve as a functor of a QTerm.
     * <p/>
     * If this Astro (or derivatives) should not be used as a functor, then
     * this operation should throw.
     */
    abstract QAstro asFunctor();

    /**
     *
     */
    private Astro leafTag(String tagName, SourceSpan optSpan) {
        AstroTag tag = myBuilder.getSchema().obtainTagForName(tagName);
        return myBuilder.leafTag(tag, optSpan);
    }

    /**
     * Does 'termoid' coerced to an Astro match the pattern represented by this
     * hole?
     * <p/>
     * When this hole is a dollar-hole, termoid will be a substitution-arg.
     * When this hole is an at-hole, termoid will be the specimen. In either
     * case, the same criteria are applied: <ul> <li>If termoid doesn't coerce
     * to an Astro, then null -- we have no match <li>If we have a tag, and it
     * doesn't match termoid's tag, then null. <li>If we are a functor-hole
     * (rather than a term-hole) and termoid has one or more arguments, then
     * null. <li>Otherwise, we match, so return the coerced termoid. </ul> The
     * coercion rules are:<ul> <li>An integer coerces to a literal integer
     * term. <li>A floating point number coerces to a literal float64 term.
     * <li>A character coerces to a literal character term. <li>null coerces to
     * term`null`, ie, a term with tag "null" and no arguments. <li>A boolean
     * coerces to either term`true` or term`false`. <li>A String or Twine
     * normally coerces to a zero-arity term whose tag is that String. But if
     * this hole's tag is ".String.", then the String will instead convert to a
     * literal string term. </ul>
     */
    Astro optCoerce(Object termoid, boolean isFunctorHole, AstroTag optTag) {
        termoid = Ref.resolution(termoid);
        Astro result;
        if (null == termoid) {
            result = leafTag("null", null);

        } else if (termoid instanceof Astro) {
            result = (Astro)termoid;

        } else if (termoid instanceof String) {
            if (null != optTag && Twine.class == optTag.getOptDataType()) {
                result = myBuilder.leafString((String)termoid, null);
            } else {
                result = leafTag((String)termoid, null);
            }
        } else if (termoid instanceof Twine) {
            Twine twine = (Twine)termoid;
            if (null != optTag && Twine.class == optTag.getOptDataType()) {
                result = myBuilder.leafTwine(twine, null);
            } else {
                result = leafTag(twine.bare(), twine.getOptSpan());
            }

        } else if (termoid instanceof Boolean) {
            if (((Boolean)termoid).booleanValue()) {
                result = leafTag("true", null);
            } else {
                result = leafTag("false", null);
            }
        } else if (termoid instanceof Number) {
            result = myBuilder.leafData(termoid, null);

        } else if (termoid instanceof Character) {
            result =
              myBuilder.leafChar(((Character)termoid).charValue(), null);
        } else {
            return null;
        }
        if (null != optTag && 0.0 != optTag.op__cmp(result.getTag())) {
            return null;
        }
        if (isFunctorHole && 0 != result.getArgs().size()) {
            return null;
        }
        return result;
    }

    /**
     * Uses my own optTag
     */
    abstract Astro optCoerce(Object termoid, boolean isFunctorHole);
}
