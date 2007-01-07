// Copyright 2006 Hewlett Packard, under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elang.tests;

import java.io.IOException;

public class methcall {

    private methcall() {
    }

    public static void main(String[] args) throws IOException {
        int n = Integer.parseInt(args[0]);

        boolean val = true;
        Toggle toggle = new Toggle(val);
        for (int i = 0; i < n; i++) {
            val = toggle.activate().value();
        }
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println((val) ? "true" : "false");

        val = true;
        NthToggle ntoggle = new NthToggle(true, 3);
        for (int i = 0; i < n; i++) {
            val = ntoggle.activate().value();
        }
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println((val) ? "true" : "false");
    }
}
