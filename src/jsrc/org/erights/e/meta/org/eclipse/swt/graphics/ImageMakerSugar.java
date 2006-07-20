// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.org.eclipse.swt.graphics;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Device;

import java.net.URL;
import java.io.IOException;

/**
 * @author Mark S. Miller
 */
public class ImageMakerSugar {

    /** prevents instantiation */
    private ImageMakerSugar() {
    }

    /** To make up for the removed makeImage(device, filename) */
    static public Image run(Device device, URL resource) throws IOException {
        return new Image(device, resource.openStream());
    }
}
