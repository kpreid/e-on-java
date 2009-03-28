#!/usr/bin/env rune

# Copyright 2007 Hewlett Packard, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

def isBadSerial := <unsafe:test.makeIsBadSerial>

def badClasses := [].asMap().diverge()

def addClasses(path, filedir) {
    if (filedir.isDirectory()) {
        for name => sub in filedir {
            addClasses(`$path/$name`, sub)
        }
    } else if (path =~ `./@fqpart.class`) {
        def fqn := fqpart.replaceAll("/",".")
        def clazz := <type>[fqn]
        if (isBadSerial(clazz)) {
            badClasses[fqn] := clazz
        }
    }
}

addClasses(".", <file:~/e/classes>)

def jsrcdir := <file:~/e/src/jsrc>

for fqn => clazz in badClasses {
    if (fqn =~ `@_$$@_`) {
        println(`nested bad class: $fqn`)
        continue
    }
    def fqpart := fqn.replaceAll(".","/")
    def srcFile := jsrcdir[`$fqpart.java`]
    if (!srcFile.exists()) {
        println(`can't find $srcFile`)
        continue
    }
    def src := srcFile.getText()
    if (src =~ `@{pre}class@mid{@post`) {
        println(`fixing $srcFile`)
        def rand := entropy.nextLong()
        def newSrc := `${pre}class$mid{

    static private final long serialVersionUID = ${rand}L;CHECKIT$post`
        srcFile.setText(newSrc)
    } else {
        println(`can't pseudo-parse $srcFile`)
    }
}
