package org.erights.e.elang.evm;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.elang.interp.ProtocolDesc;
import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.MessageDesc;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.slot.Auditable;
import org.erights.e.elib.slot.Auditor;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;

import java.io.IOException;

/**
 * What an object expression evaluates to.
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 */
public abstract class EImpl extends Auditable implements Callable {

    private final Object[] myFields;

    private final Slot[] myOuters;

    private final EMethodTable myScript;

    /**
     *
     */
    EImpl(Auditor[] approvers,
          Object[] fields,
          Slot[] outers,
          EMethodTable script) {
        super(approvers);
        myFields = fields;
        myOuters = outers;
        myScript = script;
    }

    /**
     * Returns myScript
     */
    public Script optShorten(String verb, int arity) {
        return myScript.shorten(this, verb, arity);
    }

    /**
     *
     */
    public Object callAll(String verb, Object[] args) {
        Script script = myScript.shorten(this, verb, args.length);
        return script.execute(this, verb, args);
    }

    /**
     *
     */
    public TypeDesc getAllegedType() {
        FlexList mTypes = FlexList.fromType(MessageDesc.class);
        myScript.protocol(this, mTypes);
        String fqName = myScript.getFQName();
        return new ProtocolDesc(myScript.getObjExpr().getDocComment(),
                                fqName,
                                null, //XXX for now
                                ConstList.EmptyList,
                                mTypes.snapshot());
    }

    /**
     *
     */
    public boolean respondsTo(String verb, int arity) {
        return myScript.respondsTo(this, verb, arity);
    }

    /**
     * All security rests on not making this more visible than "package"
     */
    EvalContext newContext(int localCount) {
        return EvalContext.make(localCount, myFields, myOuters);
    }

    /**
     * XXX controversial whether this should be made public
     */
    EMethodTable script() {
        return myScript;
    }

    /**
     * Invoked by the Miranda __optSealedDispatch method.
     */
    public SealedBox __optSealedDispatch(Object brand) {
        Object box = E.call(this, "__optSealedDispatch", brand);
        return (SealedBox)E.as(box, SealedBox.class);
    }

    /**
     *
     */
    public Object __conformTo(Guard guard) {
        return E.call(this, "__conformTo", guard);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) {
        E.call(this, "__printOn", out);
    }

    /**
     *
     */
    public String toString() {
        return E.toString(this);
    }

    /**
     * 
     */
    public void mirandaPrintOn(TextWriter out) throws IOException {
        out.print("<", ClassDesc.simpleName(myScript.getFQName()), ">");
    }
}
