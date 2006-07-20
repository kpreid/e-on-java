// Copyright 2005 Mark S. Miller, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.meta.java.util;

import org.erights.e.elib.tables.AssocFunc;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Mark S. Miller
 */
public class MapSugar {

    static public void iterate(Map self, AssocFunc func) {
        Iterator iter = self.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            func.run(entry.getKey(), entry.getValue());
        }
    }
}
