#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

/**
 * The find function from the E Tutorial, modified to show the
 * pathname.
 * <p>
 * Prints all lines of a given file that contains a given substring.
 */
def find(file, substring) :void {
    for num => line in file {
        if (line.startOf(substring) != -1) {
            print(`${file.getPath()}:$num:$line`)
        }
    }
}

/**
 * The findall function from the E Tutorial, modified to take an
 * extension parameter.
 * <p>
 * Recursively walks a directory tree, and prints all lines in files
 * ending with that extension that contain the given substring.
 */
def findall(dirfile, ext, substring) :void {
    if (dirfile.isDirectory()) {
        for file in dirfile {
            findall(file, ext, substring)
        }
    } else if (dirfile.getName().endsWith(ext)) {
        find(dirfile, substring)
    }
}

def args := interp.getArgs()
if (args.size() != 3) {
    throw("usage: findall.e rootname extension substring")
}
def [rootName, ext, substring] := args
def root := <file>[rootName]
findall(root, ext, substring)
