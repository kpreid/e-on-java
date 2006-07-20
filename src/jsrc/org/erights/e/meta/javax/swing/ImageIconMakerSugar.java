package org.erights.e.meta.javax.swing;

import javax.swing.ImageIcon;
import java.io.File;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * @author Mark S. Miller
 */
public class ImageIconMakerSugar {

    /**
     * prevent instantiation
     */
    private ImageIconMakerSugar() {
    }

    /**
     * Instead of the security-breaking suppressed 'new(String)'.
     */
    static public ImageIcon run(File file) {
        return new ImageIcon(file.getAbsolutePath());
    }

    /**
     * Instead of the security-breaking suppressed 'new(String, String)'.
     */
    static public ImageIcon run(File file, String description) {
        return new ImageIcon(file.getAbsolutePath(), description);
    }
}
