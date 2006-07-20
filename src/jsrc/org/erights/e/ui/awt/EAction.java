package org.erights.e.ui.awt;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.develop.exception.NestedException;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elib.oldeio.EPrintable;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.IntTable;
import org.erights.e.elib.vat.Vat;
import org.erights.e.meta.java.math.EInt;
import org.quasiliteral.syntax.BaseLexer;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Enables an E-language programmer to effectively parameterize an
 * {@link javax.swing.AbstractAction}, even though E cannot subclass Java.
 * <p/>
 * AbstractAction was designed to be parameterized by subclassing.
 * E-language code cannot subclass Java classes, so EAction provides a bridge
 * between these issues. EAction is a subclass of AbstractAction whose
 * behavior is determined by its properties, which is settable by E language
 * code.
 * <p/>
 * In addition, it provides various conveniences absent from Action and
 * AbstractAction, such as the ability to use a description string like
 * "Save &As" to set the Name ("Save As"), action Verb ("doSaveAs"), and
 * mnemonic ('A').
 * <p/>
 * The actual action represented by an EAction is the eventual sending of a
 * message (verb and arguments) to a recipient.
 *
 * @author Mark S. Miller
 */
public class EAction extends AbstractAction implements EPrintable {

    /**
     *
     */
    static private final IntTable ModNames = new IntTable(String.class);

    static {
        ModNames.putInt("SHIFT", Event.SHIFT_MASK);
        ModNames.putInt("CTRL", Event.CTRL_MASK);
        ModNames.putInt("META", Event.META_MASK);
        ModNames.putInt("ALT", Event.ALT_MASK);
    }

    /**
     * Captures the current vat at the time of creation so it can be restored
     * when this EAction is invoked.
     * <p/>
     * This is necessary since this EAction can be (and generally is) invoked
     * during an AWT event that's not a vat turn.
     */
    private final Vat myVat;

    /**
     * The recipient of the myVerb(myArgs...) message
     */
    private Object myRecip;

    /**
     *
     */
    private String myVerb;

    /**
     *
     */
    private Object[] myArgs;

    /**
     * When created this way, it's useless until parameterized with the
     * property setting methods.
     */
    public EAction() {
        myVat = Vat.getCurrentVat();
    }

    /**
     * Creates an action with a name, verb, and mnemonic derived from desc,
     * which will 'recip <- verb()'. <p>
     * <p/>
     * For example, 'EAction new(foo, "Save &As")' will have the name "Save
     * As", the mnemonic 'A', and when invoked will 'foo <- doSaveAs()'.
     */
    public EAction(Object recip, String desc) {
        this();
        setDesc(recip, desc);
    }

    /**
     * As with the two argument constructor, but will also associate the
     * described acceleration key with the action. <p>
     * <p/>
     * The acceleration parameter is in approximately the format in which
     * acceleration are displayed on Windows menus. For example, the
     * acceleration parameter "Ctrl+A" sets the acceleration keystroke to be
     * Control-A.
     */
    public EAction(Object recip, String desc, String acceleration) {
        this(recip, desc);
        accelerate(acceleration);
    }

    /**
     * Extract the string to show in the menu.
     * <pre>
     *     "Save &As.." => "Save As.."
     * </pre>
     */
    static public String descToName(String desc) {
        return StringHelper.replaceAll(desc, "&", "");
    }

    /**
     * Derive a message name.
     * <pre>
     *     "Save &As.." => "doSaveAs"
     * </pre>
     */
    static public String descToVerb(String desc) {
        StringBuffer buf = new StringBuffer();
        buf.append("do");
        for (int i = 0; i < desc.length(); i++) {
            char c = desc.charAt(i);
            if (BaseLexer.isIdentifierPart(c)) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * The mnemonic key is the character following the '&', or -1 if there
     * isn't one. <p>
     * <pre>
     *     "Save &As" => 'A'
     * </pre>
     */
    static public int descToMnemonic(String desc) {
        int i = desc.indexOf('&');
        if (i == -1 || i == desc.length() - 1) {
            return -1;
        }
        char c = Character.toUpperCase(desc.charAt(i + 1));
        if ('A' <= c && c <= 'Z') {
            return c;
        } else {
            return -1;
        }
    }

    /**
     * Sets the name, verb, and mnemonic according to desc, and the action to
     * 'recip <- verb()'.
     * <p/>
     * For example, 'ea.setDesc(foo, "Save &As")' will set the name to
     * "Save As", the mnemonic 'A', and the action to be invoked to
     * 'foo <- doSaveAs()'.
     */
    public void setDesc(Object recip, String desc) {
        setName(descToName(desc));
        setAction(recip, descToVerb(desc));
        int mnemonic = descToMnemonic(desc);
        if (-1 != mnemonic) {
            setMnemonic(mnemonic);
        }
    }

    /**
     * Invoked by Swing when the EAction's action should be invoked.
     * <p/>
     * Does a sendOnly of 'recip <- verb(args...)'
     */
    public void actionPerformed(ActionEvent e) {
        myVat.qSendAllOnly(myRecip, true, myVerb, myArgs);
    }

    /**
     * Sets the action to 'recip <- run()', so action should be a Runnable or
     * a Thunk.
     */
    public void setAction(Object recip) {
        setAction(recip, "run", E.NO_ARGS);
    }

    /**
     * Sets the action to 'recip <- verb()'
     */
    public void setAction(Object recip, String verb) {
        setAction(recip, verb, E.NO_ARGS);
    }

    /**
     * Sets the action to 'recip <- verb(args...)'
     */
    public void setAction(Object recip, String verb, Object[] args) {
        myRecip = recip;
        myVerb = verb;
        myArgs = args;
    }

    /**
     * Actually gets the AbstractAction's ACCELERATOR_KEY property.
     */
    public KeyStroke getAccelerator() {
        return (KeyStroke)getValue(ACCELERATOR_KEY);
    }

    /**
     * Actually sets the AbstractAction's ACCELERATOR_KEY property.
     */
    public void setAccelerator(KeyStroke newKey) {
        putValue(ACCELERATOR_KEY, newKey);
    }

    /**
     * Parse an accelation description string into an acceleration keyStroke,
     * and setAccelerator to that keyStroke.
     */
    public void accelerate(String acceleration) {
        String accel = acceleration.toUpperCase();
        int mask = 0;
        int i;
        while ((i = accel.indexOf('+')) != -1) {
            String prefix = accel.substring(0, i);
            accel = accel.substring(i + 1);
            int maskBit = ModNames.getInt(prefix, -1);
            if (maskBit == -1) {
                //XXX trace a diagnostic about prefix not understood
                return;
            }
            mask |= maskBit;
        }

        Field field;
        try {
            field = KeyEvent.class.getField("VK_" + accel);
        } catch (NoSuchFieldException nsfe) {
            throw new NestedException(nsfe, "# building menu: " + accel);
        }
        int c;
        try {
            c = field.getInt(null);
        } catch (IllegalAccessException iae) {
            throw new NestedException(iae, "# building menu: " + field);
        }

        KeyStroke stroke = KeyStroke.getKeyStroke(c, mask);
        setAccelerator(stroke);
    }

    /**
     * Actually gets the AbstractAction's NAME property.
     */
    public String getName() {
        return (String)getValue(NAME);
    }

    /**
     * Actually sets the AbstractAction's NAME property.
     */
    public void setName(String newName) {
        putValue(NAME, newName);
    }

    /**
     * Actually gets the AbstractAction's MNEMONIC_KEY property.
     */
    public int getMnemonic() {
        Object val = getValue(MNEMONIC_KEY);
        if (null == val) {
            return -1;
        } else {
            return ((Integer)val).intValue();
        }
    }

    /**
     * Actually sets the AbstractAction's MNEMONIC_KEY property.
     */
    public void setMnemonic(int newMnemonic) {
        if (-1 == newMnemonic) {
            putValue(MNEMONIC_KEY, null);
        } else {
            putValue(MNEMONIC_KEY, EInt.valueOf(newMnemonic));
        }
    }

    /**
     * Actually gets the AbstractAction's SHORT_DESCRIPTION property.
     */
    public String getTip() {
        return (String)getValue(SHORT_DESCRIPTION);
    }

    /**
     * Actually sets the AbstractAction's SHORT_DESCRIPTION property.
     */
    public void setTip(String newTip) {
        putValue(SHORT_DESCRIPTION, newTip);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("<- ", myVerb);
        Object[] keys = getKeys();
        if (null == keys) {
            return;
        }
        TextWriter indent = out.indent();
        indent.print(" [");
        for (int i = 0; i < keys.length; i++) {
            String key = (String)keys[i];
            indent.lnPrint(key);
            indent.print(" => ", getValue(key));
        }
        indent.print("]");
    }
}
