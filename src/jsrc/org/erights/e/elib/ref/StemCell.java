package org.erights.e.elib.ref;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.MirandaMethods;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.serial.JOSSPassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Guard;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;

/**
 * An object can writeReplace itself with a StemCell when serialized in order
 * to resolve to the StemCell's readResolve, but with circular references
 * handled as correctly as possible.
 * <p/>
 * A known problem with Java Unserialization is that a circular reference to an
 * encoded object A may get bound to A itself rather than what A readResolves
 * to. For statically typed circular references, or circular references that
 * are synchronously used before the cycle is closed, there's nothing we can do
 * about this other than fail safely (which, unlike raw Java serialization, we
 * do).
 * <p/>
 * For the sake of other circular references, an unserialized StemCell starts
 * as a promise for what it will readResolve to. Once it readResolves, the
 * promise is also resolved, so further references encoded as references to the
 * StemCell will be decoded as this resolution.
 * <p/>
 * On the encoding side, a made StemCell (as opposed to an unserialized
 * StemCell) is a very weird creature. It appears to be an eventual resolved
 * reference (a far reference), but will not deliver any messages, since the
 * object it designates doesn't exist until the StemCell is unserialized. By
 * the same token, we did not make it appear to be a broken reference, because
 * it will become a live reference to the object it makes once it's
 * unserialized. In any case, a made Stem should only be used for an object to
 * instruct its serializer on how to encode it.
 * <p/>
 * XXX StemCell should be declared abstract, but this is commented out in order
 * to work around a {@link test.foo.Class2 bug} in Sun's j2sdk1.4.1's javac
 * compiler.
 *
 * @author Mark S. Miller
 */
public /*abstract*/ class StemCell extends Ref
  implements JOSSPassByConstruction, Persistent, ObjectInputValidation {

    static private final long serialVersionUID = 3574625250011981396L;

    /**
     * null on the encoding side for a made StemCell. Unresolved during the
     * unserializing of the StemCell itself. Resolved to the StemCell's
     * resolution once the StemCell is fully unserialized.
     */
    private transient Ref myOptPromise;

    /**
     * Subclasses must define a readResolve() that both returns the resolution
     * of the StemCell, and resolves this resolver to that resolution.
     */
    protected transient Resolver myOptResolver;

    /**
     * Makes a made StemCell
     */
    protected StemCell() {
        myOptPromise = null;
        myOptResolver = null;
    }

    /**
     * Initializes the promise, does the default thing, and then resolves to
     * the result of readResolve(), to be provided by a subclass.
     */
    private void readObject(ObjectInputStream ois)
      throws ClassNotFoundException, IOException {
        Object[] pair = Ref.promise();
        myOptPromise = (Ref)pair[0];
        myOptResolver = (Resolver)pair[1];
        ois.defaultReadObject();
        T.noop();
    }

    /**
     * A made StemCell returns null; an unserialized one returns its promise's
     * problem.
     * <p/>
     * All implementations of <tt>optProblem/0</tt> must be thread safe, in
     * order for {@link Ref#state() Ref.state/0} to be thread safe.
     */
    public Throwable optProblem() {
        if (null == myOptPromise) {
            return null;
        } else {
            return myOptPromise.optProblem();
        }
    }

    /**
     * A made StemCell is resolved, and so appears to be a far ref.
     */
    public boolean isResolved() {
        if (null == myOptPromise) {
            return true;
        } else {
            return myOptPromise.isResolved();
        }
    }

    /**
     * A made StemCell's resolution is itself. An unserialized one's is
     * according to its promise.
     * <p/>
     * All implementations of <tt>resolutionRef/0</tt> must be thread safe, in
     * order for {@link Ref#resolution() Ref.resolution/0} to be thread safe:
     * myOptPromise is not meaningfully mutable, so this implementation is
     * trivially inductively thread safe.
     */
    Ref resolutionRef() {
        if (null == myOptPromise) {
            return this;
        } else {
            return myOptPromise.resolutionRef();
        }
    }

    /**
     * A made StemCell returns EVENTUAL, and unserialized one returns what its
     * promise returns.
     * <p/>
     * All implementations of <tt>state/0</tt> must be thread safe, in order
     * for {@link Ref#isNear(Object) Ref.isNear/1} to be thread safe.
     */
    public String state() {
        if (null == myOptPromise) {
            return EVENTUAL;
        } else {
            return myOptPromise.state();
        }
    }

    /**
     *
     */
    public Object callAll(String verb, Object[] args) {
        if (null == myOptPromise) {
            T.fail("not on a made StemCell");
            return null; //make compiler happy
        } else {
            return myOptPromise.callAll(verb, args);
        }
    }

    /**
     *
     */
    public Ref sendAll(String verb, Object[] args) {
        if (null == myOptPromise) {
            //XXX should it return a broken reference instead?
            T.fail("not on a made StemCell");
            return null; //make compiler happy
        } else {
            return myOptPromise.sendAll(verb, args);
        }
    }

    /**
     *
     */
    public Throwable sendAllOnly(String verb, Object[] args) {
        if (null == myOptPromise) {
            //XXX should it return the problem instead?
            T.fail("not on a made StemCell");
            return null; //make compiler happy
        } else {
            return myOptPromise.sendAllOnly(verb, args);
        }
    }

    /**
     *
     */
    void setTarget(Ref newTarget) {
        T.fail("internal: shouldn't happen");
    }

    /**
     *
     */
    void commit() {
        T.fail("internal: should not happen");
    }

    /**
     * @throws InvalidObjectException
     */
    public void validateObject() throws InvalidObjectException {
        if (!myOptResolver.isDone()) {
            throw new InvalidObjectException("StemCell didn't readResolve()");
        }
    }

    /**
     *
     */
    public SealedBox __optSealedDispatch(Object brand) {
        if (null == myOptPromise) {
            return MirandaMethods.__optSealedDispatch(this, brand);
        } else {
            return myOptPromise.__optSealedDispatch(brand);
        }
    }

    /**
     *
     */
    public Object __conformTo(Guard guard) {
        if (null == myOptPromise) {
            return MirandaMethods.__conformTo(this, guard);
        } else {
            return myOptPromise.__conformTo(guard);
        }
    }

    /**
     * XXX Once this class can be declared abstract, this method should be
     * removed.
     */
    public void __printOn(TextWriter out) throws IOException {
        if (null == myOptPromise) {
            out.print("<Unresolved StemCell>");
        } else {
            myOptPromise.__printOn(out);
        }
    }
}
