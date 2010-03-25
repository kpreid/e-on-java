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

import org.erights.e.develop.exception.EBacktraceException;
import org.erights.e.elib.base.Callable;
import org.erights.e.elib.base.Script;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EMap;
import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.util.ClassCache;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * From a Java class, obtain a corresponding Script enabling its tamed behavior
 * to be invoked from ELib.
 * 
 * @author Mark S. Miller
 */
public class ScriptMaker {

    /**
     * All instances of the left hand (key) types act (in E) as if they are
     * instances of the right hand (value) types. <p>
     * <p/>
     * This often happens by converting them first. <p>
     * <p/>
     * This is much like the Simplifications in Equalizer, with some
     * differences.
     */
    static private final String[][] Promotions = {{"java.lang.Byte",
      "java.lang.Integer"},
      {"java.lang.Short", "java.lang.Integer"},
      {"java.lang.Long", "java.math.BigInteger"},

      {"java.lang.Float", "java.lang.Double"},

      {"java.lang.Class", "org.erights.e.elib.base.ClassDesc"},
      {"java.lang.String", "org.erights.e.elib.tables.Twine"},};

    /**
     * Maps fq class names to the fqName of the classes they promote to.
     * 
     * <p>
     * ThePromotions is initialized lazily in order to avoid possible circular
     * static initialization dependencies. Uses legacy HashMap rather than EMap
     * in order to avoid a circular dependency by way of the Equalizer.
     * <p>
     * Thread safety note: Reads of this HashMap are done without any
     * synchronization, but this is safe since nothing ever mutates the map
     * except before it is assigned to ThePromotions.
     */
    static private HashMap ThePromotions = null;

    /**
     * Map a class to the class it promotes to, or null if none
     */
    static public Class OptPromotion(Class clazz) {
        if (null == ThePromotions) {
            HashMap promotions = new HashMap();
            for (int i = 0; i < Promotions.length; i++) {
                promotions.put(Promotions[i][0], Promotions[i][1]);
            }
            ThePromotions = promotions;
        }
        String promotionName = (String)ThePromotions.get(clazz.getName());
        if (null == promotionName) {
            return null;
        }
        try {
            return ClassCache.forName(promotionName);
        } catch (Exception ex) {
            throw new EBacktraceException(ex, "# promotion not found");
        }
    }

    /**
     * To the E programmer, the left hand (key) types seem to have methods
     * according to this Java type, but as modified (sugared) by the right hand
     * (value) types.
     *
     * XXX This table is DEPRECATED in favor of definitions in the safej files
     * using the sugaredBy("fqn") term. These entries exist solely because
     * these particular classes do not have safej files at all.
     */
    static private final String[][] Sugarings = {
      {"java.lang.Process", "org.erights.e.meta.java.lang.ProcessSugar"},

      {"java.io.DataOutput", "org.erights.e.meta.java.io.DataOutputSugar"},

      {"java.security.KeyFactory",
        "org.erights.e.meta.java.security.KeyFactorySugar"},
      {"java.security.KeyPair",
        "org.erights.e.meta.java.security.KeyPairSugar"},
      {"java.security.PublicKey",
        "org.erights.e.meta.java.security.PublicKeySugar"},

      {"java.security.interfaces.DSAPrivateKey",
        "org.erights.e.meta.java.security.interfaces.DSAPrivateKeySugar"},
      {"java.security.interfaces.DSAPublicKey",
        "org.erights.e.meta.java.security.interfaces.DSAPublicKeySugar"},
      {"java.security.interfaces.RSAPrivateKey",
        "org.erights.e.meta.java.security.interfaces.RSAPrivateKeySugar"},
      {"java.security.interfaces.RSAPublicKey",
        "org.erights.e.meta.java.security.interfaces.RSAPublicKeySugar"},

      {"java.security.spec.DSAPrivateKeySpec",
        "org.erights.e.meta.java.security.spec.DSAPrivateKeySpecSugar"},
      {"java.security.spec.DSAPublicKeySpec",
        "org.erights.e.meta.java.security.spec.DSAPublicKeySpecSugar"},
      {"java.security.spec.RSAPrivateKeySpec",
        "org.erights.e.meta.java.security.spec.RSAPrivateKeySpecSugar"},
      {"java.security.spec.RSAPublicKeySpec",
        "org.erights.e.meta.java.security.spec.RSAPublicKeySpecSugar"},

      {"java.util.Collection", "org.erights.e.meta.java.util.CollectionSugar"},
      {"java.util.Dictionary", "org.erights.e.meta.java.util.DictionarySugar"},
      {"java.util.Enumeration",
        "org.erights.e.meta.java.util.EnumerationSugar"},
      {"java.util.Iterator", "org.erights.e.meta.java.util.IteratorSugar"},
      {"java.util.Map", "org.erights.e.meta.java.util.MapSugar"},
      {"java.util.Properties", "org.erights.e.meta.java.util.PropertiesSugar"},
      {"java.util.Vector", "org.erights.e.meta.java.util.VectorSugar"},

      {"java.net.URL", "org.erights.e.meta.java.net.URLSugar"},

      {"java.awt.event.WindowEvent",
        "org.erights.e.meta.java.awt.event.WindowEventSugar"},

      {"javax.swing.JEditorPane",
        "org.erights.e.meta.javax.swing.JEditorPaneSugar"},

      {"org.eclipse.swt.dnd.DragSourceEvent",
        "org.erights.e.meta.org.eclipse.swt.dnd.DragSourceEventSugar"},
      {"org.eclipse.swt.dnd.DropTargetEvent",
        "org.erights.e.meta.org.eclipse.swt.dnd.DropTargetEventSugar"},

      {"org.eclipse.swt.widgets.Composite",
        "org.erights.e.meta.org.eclipse.swt.widgets.CompositeSugar"}};

    /**
     * Maps fq class names to the fqName of the classes that sugar them.
     * TheSugars is initialized lazily in order to avoid possible circular
     * static initialization dependencies.
     * XXX once Sugarings is empty, delete this
     */
    static private EMap TheSugars = null;

    /**
     * Map a class to its sugaring class, or null if it doesn't have one
     * XXX once Sugarings is empty, delete this
     */
    static public Class OptSugar(Class clazz) {
        if (null == TheSugars) {
            // thread safe because reference assignment is atomic; the
            // worst that will happen is the cache gets cleared
            TheSugars = FlexMap.fromPairs(Sugarings, true).snapshot();
        }
        String sugarName =
          (String)TheSugars.fetch(clazz.getName(), ValueThunk.NULL_THUNK);
        if (null == sugarName) {
            return null;
        }
        try {
            return ClassCache.forName(sugarName);
        } catch (Exception ex) {
            throw new EBacktraceException(ex, "# sweetener not found: "
                                              + sugarName);
        }
    }

    /**
     *
     */
    static public final ScriptMaker THE_ONE = new ScriptMaker();

    /**
     * maps java classes to scripts
     * 
     * <p>
     * NOTE: This is shared among threads, thus it must be a thread-safe map.
     * However, the identity of Scripts is not significant, so our lookup-or-
     * create need not be careful to ensure that each class's script is only
     * created once.
     */
    private final ConcurrentHashMap myScripts;

    /**
     *
     */
    private ScriptMaker() {
        myScripts = new ConcurrentHashMap/*<Class, Script>*/();

        //preload with special cases

        VTable topTable = new InstanceTable(Object.class);
        SugarMethodNode.defineMembers(topTable, MirandaMethods.class);
        myScripts.put(Object.class, topTable);

        //All arrays act like a ConstList, but we're careful not to say
        //they *promote* to ConstList because this would break the
        //Equalizer.
        VTable arrayTable = new InstanceTable(Object[].class);
        inherit(arrayTable, ConstList.class, SafeJ.ALL);
        myScripts.put(Object[].class, arrayTable);
    }

    /**
     *
     */
    private void inherit(VTable vTable, Class donor, SafeJ safeJ) {
        Script script = instanceScript(donor);
        if (script instanceof VTable) {
            VTable donorVTable = ((VTable)script);
            vTable.addMethods(donorVTable.methods(), safeJ);
            VTableEntry optMatcher = donorVTable.getOptOtherwise();
            if (null != optMatcher) {
                vTable.setOptOtherwise(optMatcher);
            }
        }
    }

    /**
     *
     */
    public Script instanceScript(Class clazz) {
        Script result = (Script)myScripts.get(clazz);
        if (null != result) {
            return result;
        }

        if (Callable.class.isAssignableFrom(clazz)) {
            myScripts.putIfAbsent(clazz, CallableScript.THE_ONE);
            return CallableScript.THE_ONE;
        }

        VTable vTable = new InstanceTable(clazz);

        //If this class promotes, cache the optPromotion's script for both
        //classes
        Class optPromotion = OptPromotion(clazz);
        if (null != optPromotion) {
            inherit(vTable, optPromotion, SafeJ.ALL);
            myScripts.putIfAbsent(clazz, vTable);
            return vTable;
        }

        if (clazz.isArray()) {
            //all arrays share the same behavior
            inherit(vTable, Object[].class, SafeJ.ALL);
        } else {
            SafeJ safeJ = SafeJ.getSafeJ(clazz, false);
            SafeJ inheritJ = safeJ.forInheritance();
            Class optSuper = clazz.getSuperclass();
            if (null != optSuper) {
                inherit(vTable, optSuper, inheritJ);
            }
            //XXX ambiguous multiple inheritance should ruin a binding
            Class[] faces = clazz.getInterfaces();
            for (int i = 0; i < faces.length; i++) {
                inherit(vTable, faces[i], inheritJ);
            }
            InstanceMethodNode.defineMembers(vTable, clazz, safeJ);
            Class optSugar = safeJ.getOptSugaredBy();
            if (null == optSugar) {
                optSugar = OptSugar(clazz);
            }
            if (null != optSugar) {
                SugarMethodNode.defineMembers(vTable, optSugar);
            }
        }
        myScripts.putIfAbsent(clazz, vTable);
        return vTable;
    }
}
