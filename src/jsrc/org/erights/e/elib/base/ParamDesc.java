package org.erights.e.elib.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Guard;

import java.io.IOException;

/**
 * Documents the name and optional guard of a parameter
 */
public class ParamDesc implements Persistent, EPrintable {

    static private final long serialVersionUID = -2778036173910639865L;

    static private StaticMaker OptParamDescMaker = null;

    static public StaticMaker GetParamDescMaker() {
        if (null == OptParamDescMaker) {
            OptParamDescMaker = StaticMaker.make(ParamDesc.class);
        }
        return OptParamDescMaker;
    }

    /**
     * @serial Writes "_" for anonymous parameter
     */
    private final String myOptName;

    /**
     * @serial Guards the parameter variable.
     * <p/>
     * In order to avoid a circular dependency, this variable can hold a Class
     * instead of a Guard, in which case it will be converted to a Guard as
     * needed.
     */
    private Object myOptGuard;

    /**
     * @param optGuard In order to avoid a circular dependency, this variable
     *                 can hold a Class instead of a Guard, in which case it
     *                 will be converted to a Guard as needed.
     */
    public ParamDesc(String optName, Object optGuard) {
        myOptName = optName;
        myOptGuard = optGuard;
    }

    /**
     *
     */
    public String getName() {
        if (null == myOptName) {
            return "_";
        } else {
            return myOptName;
        }
    }

    /**
     *
     */
    public Guard getOptGuard() {
        if (myOptGuard == null || myOptGuard == Object.class) {
            return null;
        } else if (myOptGuard instanceof Guard) {
            return (Guard)myOptGuard;
        } else if (myOptGuard instanceof Class) {
            myOptGuard = ClassDesc.byJavaRules((Class)myOptGuard);
            return (Guard)myOptGuard;
        } else {
            myOptGuard = E.as(myOptGuard, Guard.class);
            return (Guard)myOptGuard;
        }
    }

    /**
     * Prints 'name', 'name :type', or ':type'
     */
    public void __printOn(TextWriter out) throws IOException {
        Guard optGuard = getOptGuard();
        if (null == optGuard) {
            out.print(getName());
        } else {
            if (null == myOptName) {
                out.print(":");
            } else {
                out.print(myOptName, " :");
            }
            out.print(optGuard);
        }
    }
}
