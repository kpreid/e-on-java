#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def fixImports := <import:org.erights.e.tools.java.fixImports>

if (interp.getArgs().size() != 1) {
    throw("usage: fixImports root-path-name")
}

fixImports(<file>[interp.getArgs()[0]])
