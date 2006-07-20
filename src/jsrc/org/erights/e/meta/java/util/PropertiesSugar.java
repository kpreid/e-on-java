// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Mark S. Miller
 */
public class PropertiesSugar {

    /**
     * Adds properties read from propsFile to self
     */
    static public void loadFromFile(Properties self, File propsFile)
      throws IOException {
        InputStream ins = new FileInputStream(propsFile);
        try {
            self.load(ins);
        } finally {
            ins.close();
        }

    }

    /**
     * Adds properties read from propsURL to self
     */
    static public void loadFromURL(Properties self, URL propsURL)
      throws IOException {
        InputStream ins = propsURL.openStream();
        try {
            self.load(ins);
        } finally {
            ins.close();
        }

    }
}
