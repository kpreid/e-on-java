package org.erights.e.develop.boot;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Mark S. Miller
 */
public class PropertiesHelper {

    private static final String VERSION = "PropertiesHelper version 1.0";

    private static final String HELPSTR =
      "java ... org.erights.e.develop.boot.PropertiesHelper [propertyName]\n" +
        "Given a propertyName, its value is printed out literally.\n" +
        "If absent, all key-value pairs are printed out escaped.";

    /**
     * Flattens 'self' into a single Properties containing all the associations
     * in 'self'
     */
    private static Properties flatten(Properties self) {
        Properties result = new Properties();
        for (Enumeration iter = self.propertyNames(); iter.hasMoreElements();)
        {
            String key = (String)iter.nextElement();
            String value = self.getProperty(key);
            result.setProperty(key, value);
        }
        return result;
    }

    /**
     *
     */
    static public void main(String[] args) throws IOException {
        Properties sysProps = System.getProperties();
        if (args.length == 0) {
            Properties flatSys = flatten(sysProps);
            flatSys.store(System.out, null);
            return;
        }
        if ("--help".equals(args[0])) {
            System.out.println(HELPSTR);
            return;
        }
        if ("--version".equals(args[0])) {
            System.out.println(VERSION);
            return;
        }
        String optValue = System.getProperty(args[0], null);
        if (null == optValue) {
            System.err.println("There is no '" + args[0] + "' property");
            System.exit(-1);
        }
        System.out.println(optValue);
    }
}
