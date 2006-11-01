#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def makeIdentifiers := <import:org.quasiliteral.text.makeIdentifiers>

def names := [].asSet().diverge()

def accumNames(filedir, exts) :void {
    if (filedir.isDirectory()) {
        stderr.print("[")
        for sub in filedir {
            accumNames(sub, exts)
        }
        stderr.print("]")
    } else if (filedir.getName() =~ `@_.@ext` && exts.contains(ext)) {
        for delim => ident in makeIdentifiers.fromFile(filedir) {
            if (ident.size() >= 2 && ident == ident.toUpperCase()) {
                if (true || delim.endsWith(".")) {
                    names.addElement(ident)
                }
            }
        }
    }
}

def exts := ["e", "e-awt", "e-swt",
             "emaker", "caplet", "updoc", "safej"
            ].asSet()

switch (interp.getArgs()) {
    match [dirname] {
        accumNames(<file>[dirname], exts)
        for name in names.sort() {
            println(name)
        }
    }
    match _ {
        throw("usage: findOldConstNames.e dirname")
    }
}

