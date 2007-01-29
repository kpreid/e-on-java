package com.zooko.tray;

/*
 * Copyright (c) 2002 Bryce "Zooko" Wilcox-O'Hearn
 * See the end of this file for the free software, open source license
 * (BSD-style).
 */

import javax.swing.JPanel;
import java.awt.Graphics;

/**
 * Allows an E object to virtually subclass <tt>JPanel</tt> in order to
 * override its <tt>paintComponent(Graphics)</tt> method.
 * <p/>
 * This object follows half the E inheritance pattern -- it causes
 * sends-to-self of paintComponent/1 to be delegated to its 'self' object. It
 * doesn't follow the other half -- the self object delegated to has no 'super'
 * object it can invoke to obtain the stock paintComponent/1 behavior. In
 * addition, since most contexts at which an instance can appear require an
 * object of Java type JComponent, the instance of EPainter is normally used,
 * rather than the E object providing the paintComponent/1 behavior. This is
 * probably an inheritance pattern variation that has other uses as well.
 *
 * @author Bryce "Zooko" Wilcox-O'Hearn
 * @author Mods by Mark S. Miller
 */
public class EPainter extends JPanel {

    static private final long serialVersionUID = 6580304757913416555L;

    /**
     * The type of a sub-object that can override an EPainter's
     * paintComponent/1 method
     */
    static public interface IPaint {

        void paintComponent(Graphics g);
    }

    private final IPaint mySelf;

    /**
     * Makes an EPainter that delegates {@link #paintComponent(Graphics)
     * paintComponent/1} to <tt>self</tt>.
     *
     * @param self an E object which has a <tt>paintComponent/1</tt> method
     */
    public EPainter(IPaint self) {
        mySelf = self;
    }

    /**
     * Delegates to my self.
     */
    protected void paintComponent(Graphics g) {
        mySelf.paintComponent(g);
    }
}

/*
 * Copyright (c) 2002 Bryce "Zooko" Wilcox-O'Hearn
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software to deal in this software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of this software, and
 * to permit persons to whom this software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of this software.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THIS SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THIS SOFTWARE.
 */
