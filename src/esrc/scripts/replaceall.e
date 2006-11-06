#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def replaceAll(filedir, ext, oldstr, newstr) :void {
    if (filedir.isDirectory()) {
        for sub in filedir {
            replaceAll(sub, ext, oldstr, newstr)
        }
    } else if (filedir.getName().endsWith(ext)) {
        stderr.println(filedir.getPath())
        filedir.setText(filedir.getText().replaceAll(oldstr, newstr))
    }
}


if (interp.getArgs() =~ [rootName, ext, oldstr, newstr]) {
    replaceAll(<file>[rootName], ext, oldstr, newstr)
} else {
    throw("usage: replaceall.e rootname extension oldstr newstr")
}
