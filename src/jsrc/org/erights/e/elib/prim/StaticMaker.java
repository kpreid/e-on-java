package org.erights.e.elib.prim;

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
import org.erights.e.develop.exception.NestedException;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.MessageDesc;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.TypeDesc;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.sealing.SealedBox;
import org.erights.e.elib.serial.PassByConstruction;
import org.erights.e.elib.serial.Persistent;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EMap;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.util.AlreadyDefinedException;
import org.erights.e.elib.util.ClassCache;

import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * How a Java class's static methods are made accessible to E.
 * <p/>
 * We probably need to make these passable over the boot-comm-system as well.
 * Unfortunately, StaticMaker doesn't qualify as DeepPassByCopy.
 *
 * @author Mark S. Miller
 */
public class StaticMaker implements Callable, Persistent, PassByConstruction {

    static private final long serialVersionUID = -9145754747555989915L;

    /**
     *
     */
    static private final String[][] Sugarings = {{"java.awt.Color",
      "org.erights.e.meta.java.awt.ColorMakerSugar"},
      {"java.awt.Component",
        "org.erights.e.meta.java.awt.ComponentMakerSugar"},

      {"java.lang.Character",
        "org.erights.e.meta.java.lang.CharacterMakerSugar"},

      {"java.math.BigInteger", "org.erights.e.meta.java.math.EInt"},

      {"java.security.KeyPairGenerator",
        "org.erights.e.meta.java.security.KeyPairGeneratorMakerSugar"},

      {"javax.swing.ImageIcon",
        "org.erights.e.meta.javax.swing.ImageIconMakerSugar"},
      {"javax.swing.text.JTextComponent",
        "org.erights.e.meta.javax.swing.text.JTextComponentMakerSugar"},

      {"org.eclipse.swt.graphics.Image",
        "org.erights.e.meta.org.eclipse.swt.graphics.ImageMakerSugar"},
      {"org.eclipse.swt.widgets.Control",
        "org.erights.e.meta.org.eclipse.swt.widgets.ControlMakerSugar"},
      {"org.eclipse.swt.widgets.Shell",
        "org.erights.e.meta.org.eclipse.swt.widgets.ShellMakerSugar"}};

    /**
     * Maps fq class names to the fqName of the classes that sugar the maker
     * for that class. TheSugars is initialized lazily in order to avoid a
     * fatal circular static initialization dependency with ConstMap.
     */
    static private EMap TheSugars = null;

    /**
     *
     */
    static private final FlexMap MakerCache =
      FlexMap.fromTypes(Class.class, StaticMaker.class);

    /**
     *
     */
    private Class myClass;

    /**
     * @see #readResolve()
     */
    private transient VTable myVTable;

    /**
     *
     */
    private StaticMaker(Class clazz) {
        myClass = clazz;
        myVTable = new StaticTable(getClassSig(), this);
        Class optSugar = OptSugar(clazz);
        SafeJ safeJ = SafeJ.getSafeJ(clazz, optSugar, true);
        if (null != safeJ) {
            //XXX Kludge: Because the current taming files have misclassified
            //many static methods as instance methods, we union in the
            //instance methods. Assuming this is an accurate explanation of
            //the error in the taming files, and assuming it isn't compounded
            //by other errors, this kludge is safe, since Java will not allow
            //the same signature to be used for both an instance and a static
            //method.
//            SafeJ more = SafeJ.getSafeJ(clazz, null, false);
//            safeJ = safeJ.or(more, false);
        }
        try {
            SugarMethodNode.defineMembers(myVTable, MirandaMethods.class);
            //constructors come first so a static 'run' will override a
            //constructor
            ConstructorNode.defineMembers(myVTable, clazz, safeJ);
            StaticMethodNode.defineMembers(myVTable, clazz, safeJ);
            if (null != optSugar) {
                //note that the static methods for sugaring a maker are used
                //as StaticMethodNodes, not as SugarMethodNodes.
                StaticMethodNode.defineMembers(myVTable, optSugar, SafeJ.ALL);
            }
        } catch (AlreadyDefinedException ade) {
            throw new NestedException(ade, "# can't wrap class: " + clazz);
        }
    }

    /**
     * Add methods for sugaring the class's maker behavior
     */
    static public Class OptSugar(Class clazz) throws AlreadyDefinedException {
        if (null == TheSugars) {
            TheSugars = FlexMap.fromPairs(Sugarings, true).snapshot();
        }
        String sugarName =
          (String)TheSugars.fetch(clazz.getName(), ValueThunk.NULL_THUNK);
        if (sugarName == null) {
            return null;
        }
        try {
            return ClassCache.forName(sugarName);
        } catch (Exception ex) {
            throw new NestedException(ex,
                                      "# sweetener not found: " + sugarName);
        }
    }

    /**
     *
     */
    static public StaticMaker make(Class clazz) {
        StaticMaker result;
        //Need to synchronize since this is inter-vat mutable state
        synchronized (MakerCache) {
            result =
              (StaticMaker)MakerCache.fetch(clazz, ValueThunk.NULL_THUNK);
        }
        if (null != result) {
            return result;
        }
        T.require(Modifier.isPublic(clazz.getModifiers()),
                  "'",
                  clazz.getName(),
                  "' must be public");

        result = new StaticMaker(clazz);
        synchronized (MakerCache) {
            MakerCache.put(clazz, result);
        }
        return result;
    }

    /**
     *
     */
    public VTable getVTable() {
        return myVTable;
    }

    /**
     * @serialData The only StaticMakers that are serializable are those
     * wrapping importable (approved) classes.
     * <p/>
     * While this restriction is necessary for CapTP's security, we should
     * probably find a way to relax it for persistence.
     */
    private Object readResolve() {
        if (SafeJ.approve(myClass, true)) {
            StaticMaker result = make(myClass);
            //we become valid ourselves as well, just in case there are some
            //too early references to us.
            myVTable = result.myVTable;
            return result;
        } else {
            Object result =
              Ref.broken(E.asRTE("Not approved: " + ClassDesc.sig(myClass)));
            //we become invalid ourselves as well, just in case there are
            //some too early references to us
            myClass = null;
            return result;
        }
    }

    /**
     * Return myVTable, shortened
     */
    public Script optShorten(String verb, int arity) {
        return myVTable.shorten(this, verb, arity);
    }

    /**
     *
     */
    public Object callAll(String verb, Object[] args) {
        return myVTable.execute(this, verb, args);
    }

    /**
     *
     */
    public TypeDesc getAllegedType() {
        FlexList mTypes = FlexList.fromType(MessageDesc.class);
        myVTable.protocol(this, mTypes);
        return new TypeDesc(" Missing docComment ", //XXX for now
                            getClassSig(), null, //XXX for now
                            ConstList.EmptyList, mTypes.snapshot());
    }

    /**
     * @return
     */
    private String getClassSig() {
        return ClassDesc.sig(myClass, "__Maker");
    }

    /**
     *
     */
    public boolean respondsTo(String verb, int arity) {
        return myVTable.respondsTo(this, verb, arity);
    }

    /**
     * Returns the type represented by the wrapped class. By special
     * dispensation, this method is also made available as an E method on an
     * StaticMaker.
     */
    public TypeDesc asType() {
        return ClassDesc.make(myClass);
    }

    /**
     *
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
        if (SafeJ.approve(myClass, true)) {
//            out.print("<import:", ClassDesc.sig(myClass), ">");
            out.print("<make", ClassDesc.flatSig(myClass), ">");
        } else {
//            out.print("<unsafe:", ClassDesc.sig(myClass), ">");
            out.print("<make", ClassDesc.flatSig(myClass), ">");
        }
    }
}
