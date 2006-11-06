#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def argParser := <import:org.erights.e.tools.args.argParser>


def extensions := [].asMap().diverge()

def gather(filedir) :void {
    if (filedir.isDirectory()) {
        for sub in filedir {
            gather(sub)
        }
    } else {
        def ext := argParser.getExtension(filedir.getName())
        def dirs := extensions.fetch(ext, thunk{[].asSet().diverge()})
        dirs.addElement(filedir.getParent())
        extensions.put(ext, dirs)
    }
}

def prefix := <file:.>.getPath()

def [props, options, fname, args] := argParser(["--help", "--version"].asSet(),
                                               interp.getArgs())

switch (fname) {
    match `--help` {
        println(`
Usage: extensions.e <option>* <fname>*
  or:  extensions.e --help
  or:  extensions.e --version

The first form lists all the extensions in the fname roots, one per line

<option> may be
  --showDirs    Shows the directories each extension was found in
<fname>         A file or directory path. If left out, defaults to .
`)
    }
    match `--version` {
        println("extensions.e")
        rune(["--version"])
    }
    match _ {
        def fnames := if (null == fname) {
            ["."]
        } else {
            [fname] + args
        }
        def showDirs := argParser.getFlag("--showDirs", options, false)

        for fname in fnames {
            gather(<file>[fname])
        }
        for ext => dirs in extensions.sortKeys() {
            print(ext)
            if (showDirs) {
                for var dir in dirs.sort() {
                    if (dir =~ `$prefix@suffix`) {
                        dir := suffix
                    }
                    print(" ", dir)
                }
            }
            println()
        }
    }
}
