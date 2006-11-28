package org.quasiliteral.quasiterm;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.quasiliteral.astro.Astro;
import org.quasiliteral.astro.AstroArg;
import org.quasiliteral.astro.AstroBuilder;
import org.quasiliteral.astro.AstroTag;
import org.quasiliteral.term.QuasiBuilder;
import org.quasiliteral.term.Termish;

import java.io.IOException;

/**
 * A quasi-literal Term, that matches or generates an actual {@link Astro}.
 *
 * @author Mark S. Miller
 */
public final class QTerm extends QAstro {

    static private final long serialVersionUID = 8957426444620247360L;

    /**
     *
     */
    static public final StaticMaker QTermMaker = StaticMaker.make(QTerm.class);

    /**
     * @serial The functor info
     */
    private final QAstro myQFunctor;

    /**
     * @serial
     */
    private final QAstroArg myQArgs;

    /**
     * Makes a QTerm that matches or generates an Astro.
     * <p/>
     * The invariants of a QTerm are not checked here, but rather are enforced
     * by the callers in this class and in QTermBuilder.
     *
     * @param builder  Used to build the results of a substitute
     * @param qFunctor Matches or generates functor information
     * @param qArgs    This QTerm's argument list -- a QAstroArg
     */
    public QTerm(AstroBuilder builder,
                 QAstro qFunctor,
                 QAstroArg qArgs,
                 SourceSpan optSpan) {
        super(builder, optSpan);
        myQFunctor = qFunctor.asFunctor();
        myQArgs = qArgs;
    }

    /**
     * Uses 'QTermMaker(myBuilder, myQFunctor, myQArgs)'
     */
    public Object[] getSpreadUncall() {
        Object[] result =
          {QTermMaker, "run", myBuilder, myQFunctor, myQArgs, myOptSpan};
        return result;
    }

    /**
     *
     */
    public AstroArg withOptSpan(SourceSpan optSpan) {
        return new QTerm(myBuilder, myQFunctor, myQArgs, optSpan);
    }

    /**
     *
     */
    public AstroArg qbuild(QuasiBuilder qbuilder) {
        return qbuilder.term((Astro)myQFunctor.qbuild(qbuilder),
                             myQArgs.qbuild(qbuilder));
    }

    /**
     * Represents the token-type of the functor of this term.
     */
    public AstroTag getTag() {
        return myQFunctor.getTag();
    }

    /**
     *
     */
    public short getOptTagCode() {
        return myQFunctor.getOptTagCode();
    }

    /**
     * Either literal data or null. If not null, then the tag must represent
     * the canonical literal type for this kind of data in this schema.
     */
    public Object getOptData() {
        return myQFunctor.getOptData();
    }

    /**
     *
     */
    public String getOptString() {
        return myQFunctor.getOptString();
    }

    /**
     * @return :QAstroArg[]
     */
    public ConstList getArgs() {
        return ConstList.EmptyList.with(myQArgs);
    }

    /**
     * Returns a QTerm like with one, but without args.
     * <p/>
     * Unlike myQFunctor, this will only match a Term of zero-arity.
     */
    public Astro withoutArgs() {
        return new QTerm(myBuilder,
                         myQFunctor,
                         new QEmptySeq(myBuilder, null),
                         myOptSpan);
    }

//    /**
//     *
//     */
//    public Astro withArgs(ConstList qArgs) {
//        T.require(myQArgs.getHeight() == 1,
//                  "To use as quasi-functor, must not have quasi-args: ",
//                  this);
//        return new QTerm(myBuilder, myQFunctor,
//                         QSeq.run(myBuilder, qArgs));
//    }

    /**
     * @return A single list of a single Astro, whose functor is based on
     *         literal functor info of this qterm, and whose args are the
     *         concatentation of the substSlice of the qargs of this qterm.
     */
    public ConstList substSlice(ConstList args, int[] index) {
        Astro tFunctor = (Astro)myQFunctor.substSlice(args, index).get(0);
        AstroArg tArgs = Termish.run(myQArgs.substSlice(args, index));
        Astro tTerm = myBuilder.term(tFunctor, tArgs);
        return ConstList.EmptyList.with(tTerm);
    }

    /**
     * Attempts to match against the Astro specimenList[0].
     * <p/>
     * Matches the arg list by a naive greedy algorithm.
     *
     * @return -1 or 1, depending on whether specimenList[0] matches this
     *         qterm.
     */
    public int matchBindSlice(ConstList args,
                              ConstList specimenList,
                              FlexList bindings,
                              int[] index) {
        if (0 >= specimenList.size()) {
            return -1;
        }
        Astro optSpecimen = optCoerce(specimenList.get(0));
        if (null == optSpecimen) {
            return -1;
        }
        ConstList singletonFunctorList =
          ConstList.EmptyList.with(optSpecimen.withoutArgs());
        int matches = myQFunctor.matchBindSlice(args,
                                                singletonFunctorList,
                                                bindings,
                                                index);
        if (0 >= matches) {
            return -1;
        }
        T.requireSI(1 == matches,
                    "Functor may only match 0 or 1 specimen: ",
                    matches);
        ConstList tArgs = optSpecimen.getArgs();
        int num = myQArgs.matchBindSlice(args, tArgs, bindings, index);
        if (tArgs.size() == num) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     *
     */
    public int getHeight() {
        return myQArgs.getHeight();
    }

    /**
     *
     */
    public void prettyPrintOn(TextWriter out) throws IOException {
        String label = myQFunctor.toString();
        out.print(label);
        String spaces = StringHelper.multiply(" ", label.length() + 1);
        TextWriter sub = out.indent(spaces);
        sub.print("(");
        myQArgs.prettyPrintOn(sub);
        sub.print(")");
    }

    /**
     *
     */
    QAstro asFunctor() {
        T.require(1 == myQArgs.getHeight(),
                  "Terms with args can't be used as functors: ",
                  this);
        //could 'return this;', but may as well shed unnecessary structure
        return myQFunctor;
    }

    /**
     * A QTerm has whatever shape its children agree on
     */
    int startShape(ConstList args,
                   FlexList optBindings,
                   int[] prefix,
                   int shapeSoFar) {
        shapeSoFar =
          myQFunctor.startShape(args, optBindings, prefix, shapeSoFar);
        shapeSoFar = myQArgs.startShape(args, optBindings, prefix, shapeSoFar);
        return shapeSoFar;
    }

    /**
     * Just delegate to all children
     */
    void endShape(FlexList optBindings, int[] prefix, int shape) {
        myQFunctor.endShape(optBindings, prefix, shape);
        myQArgs.endShape(optBindings, prefix, shape);
    }

    /**
     *
     */
    Astro optCoerce(Object termoid) {
        if (termoid instanceof Astro) {
            Astro astro = (Astro)termoid;
            Astro newOptFunctor = myQFunctor.optCoerce(astro.withoutArgs());
            if (null == newOptFunctor) {
                return null;
            }
            ConstList argList = astro.getArgs();
            AstroArg args = myBuilder.empty();
            for (int i = 0, max = argList.size(); i < max; i++) {
                args = myBuilder.seq(args, (AstroArg)argList.get(i));
            }
            return myBuilder.term(newOptFunctor, args);
        } else {
            return myQFunctor.optCoerce(termoid);
        }
    }
}
