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

import org.erights.e.develop.assertion.T;
import org.erights.e.elib.base.MessageDesc;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.CallCounter;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.debug.Profiler;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.MethodNode;
import org.erights.e.elib.prim.VTable;
import org.erights.e.elib.prim.VTableEntry;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.vat.Runner;

import java.io.IOException;


/**
 * Adapt to a method defined in E.
 *
 * @author Mark S. Miller
 * @author E. Dean Tribble
 * @see org.erights.e.elib.prim.VTable
 */
public class EMethodNode extends MethodNode implements EStackItem {

    private final String myTypeName;

    private final String myVerb;

    private final int myArity;

    private final EMethod myMethod;

    private final CallCounter myCallCounter;

    private transient EMethodTable myOptScript = null;

    /**
     * The constructor interns the verb
     */
    public EMethodNode(String typeName,
                       String verb,
                       int arity,
                       EMethod method) {
        myTypeName = typeName;
        myVerb = verb.intern();
        myArity = arity;
        myMethod = method;
        myCallCounter =
          Profiler.THE_ONE.register(toString(), myMethod.getOptSpan());
    }

    /**
     *
     */
    public EMethodNode(String typeName, EMethod method) {
        this(typeName, method.getVerb(), method.getPatterns().length, method);
    }

    /**
     * Just returns this.
     */
    public Script shorten(Object optSelf, String aVerb, int arity) {
        return this;
    }

    /**
     * @param optShortSelf
     * @return
     */
    public boolean canHandleR(Object optShortSelf) {
        if (null == optShortSelf) {
            return false;
        }
        if (!(optShortSelf instanceof EImpl)) {
            return false;
        }
        EImpl self = (EImpl)optShortSelf;
        if (null == myOptScript) {
            return false;
        }
        return myOptScript == self.script();
    }

    /**
     *
     */
    public VTableEntry forVTable(VTable vTable) {
        if (null == myOptScript) {
            myOptScript = (EMethodTable)vTable;
            return this;
        } else {
            T.fail("EMethod inhertance not implemented");
            return null; // make the compiler happy
        }
    }

    /**
     *
     */
    public Object execute(Object optSelf, String verb, Object[] args) {
        if (myArity != args.length ||
          (myVerb != verb && !myVerb.equals(verb))) {

            NoSuchMethodException nsme = new NoSuchMethodException(
              "internal: " + verb + "/" + args.length);
            throw myCallCounter.bumpBadCount(nsme, optSelf, verb, args);
        }
        Runner.pushEStackItem(this);
        try {
            EImpl self = (EImpl)Ref.resolution(optSelf);
            Object result = myMethod.execute(self, args);
            myCallCounter.bumpOkCount();
            return result;
        } catch (Throwable problem) {
            throw myCallCounter.bumpBadCount(problem, optSelf, verb, args);
        } finally {
            Runner.popEStackItem();
        }
    }

    /**
     *
     */
    public String getDocComment() {
        return myMethod.getDocComment();
    }

    /**
     *
     */
    public String getVerb() {
        return myVerb;
    }

    /**
     *
     */
    public String getOptTypedVerb() {
        return null;
    }

    /**
     *
     */
    public int getArity() {
        return myArity;
    }

    /**
     *
     */
    public EMethod getMethod() {
        return myMethod;
    }

    /**
     * Do nothing
     */
    public void addJavaMemberNodesToMap(FlexMap map) {
    }

    /**
     *
     */
    public MessageDesc makeMessageType(String verb) {
        //XXX we should do better parameters if we can
        return super.makeMessageType(verb);
    }

    /**
     *
     */
    public String toString() {
        String str = "#" + myVerb + "/" + myArity;
        if (null == myTypeName) {
            return "<missing type name>" + str;
        } else {
            return myTypeName + str;
        }
    }

    /**
     *
     */
    public void traceOn(TextWriter out) throws IOException {
        out.print(toString());
    }

    /**
     * @return
     */
    public SourceSpan getOptSpan() {
        return myMethod.getOptSpan();
    }
}
