#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def System := <unsafe:java.lang.makeSystem>
def makeFileWriter := <unsafe:java.io.makeFileWriter>
def leaves := <import:org.erights.e.tools.files.leaves>

def write(file, text) :void {
    def fw := makeFileWriter(file)
    try {
        E.call(fw, "write(String)", [text])
    } finally {
        fw.close()
    }
}

def extensions := [
    "gif" => false,
    "jpg" => false,
    "ico" => false,
    "png" => false,
    "jar" => false,
    "exe" => false,
    "data" => false,
    "byaccj" => false,
    "dll" => false,
    "jnilib" => false,
    "sl" => false,
    "so" => false,
    "a" => false,
    "out" => false,
    "1" => false, # for ../linux-*/x86/libXm.so.2.1
    "2" => false, # for ../linux-*/x86/libXm.so.2

    "txt" => true,
    "html" => true,
    "htm" => true,
    "xml" => true,
    "emaker" => true,
    "updoc" => true,
    "caplet" => true,
    "java" => true,
    "y" => true,
    "sh" => true,
    "e" => true,
    "e-awt" => true,
    "e-swt" => true,
    "edev" => true,
    "Makefile" => true,
    "rune" => true,
    "mk" => true,
    "h" => true,
    "c" => true,
    "cpp" => true,
    "cp" => true,
    "README" => true,
    "properties" => true,
    "g" => true,
    "el" => true,
    "bash" => true,
    "terml" => true,
    "safej" => true,
    "schema" => true,
    "grammar" => true,
    "json" => true,
    "in" => true,
    "sash" => true,
    "bot" => true,
    "log" => true,

    "cap" => true,
    "cap-acct" => true,
    "cap-acctboss" => true,
    "cap-account" => true,
    "cap-kiosk" => true,
    "cap-kioskboss" => true,
    "cap-ss" => true,
    "cap-ssboss" => true,

    "eps" => true,
    "fig" => true,
    "svg" => true,
    "project" => true,
    "texlipse" => true,
    "tex" => true,
    "ltx" => true,
    "bib" => true,
    "clo" => true,
    "cls" => true,

    "Makefile-w32" => true, #
    "Makefile-unix" => true, #
    "Makefile-gcj" => true, #
    "bat" => false, #

    "at" => false, # (don't know whether this is correct)
]

def usage :=
"e newlines.e [options] file...
Options:
    --help          Print this text
    --verbose       Show files processed and ignored
    --notabs        Disallow tabs in non-makefile text files
    --onlyKnown     Disallow extensions this program hasn't heard of

    --unix          Convert to unix newlines (default)
    --msdos         Convert to msdos newlines (not yet implemented)
    --mac           Convert to macintosh newlines (not yet implemented)
Files/Directories are traversed recursively, but only text files are
transformed. Text files are recognized according to a built-in set of
extensions. This should eventually be made specifiable."

var format := "unix" # default
var verboseFlag := false
var notabsFlag := false
var onlyKnownFlag := false

def files := [].diverge()

for arg in interp.getArgs() {
    switch (arg) {
        match `--help` {
            println(usage)
            System."exit"(0)
        }
        match `--verbose` {
            verboseFlag := true
        }
        match `--notabs` {
            notabsFlag := true
        }
        match `--onlyKnown` {
            onlyKnownFlag := true
        }
        match `--unix` {
            format := "unix"
        }
        match `--msdos` {
            format := "msdos"
        }
        match `--mac` {
            format := "mac"
        }
        match _ {
            files.push(<file>[arg])
        }
    }
}

if (format != "unix") {
    println(`format $format not yet implemented`)
    System."exit"(-1)
}

if (files.size() == 0) {
    println("Must provide some files")
    println(usage)
    System."exit"(-1)
}

var errorMsg := null

for topFile in files {
    for name => file in leaves(topFile) {
        def lastDot := name.lastIndexOf(".")
        def ext := name(lastDot+1, name.size())
        def isText := extensions.fetch(ext, fn{})
        if (isText == null) {
            if (onlyKnownFlag) {
                errorMsg := "unrecognized extensions"
                stderr.println(`"$file" unregognized extension`)
            }
        } else if (isText) {
            if (verboseFlag) {
                println(file)
            }
            def text := file.getText()
            if (notabsFlag &&
                  ext != "mk" &&
                  ext != "Makefile" &&
                  text.startOf("\t") != -1) {

                # Textfiles which aren't makefiles shouldn't have tabs
                errorMsg := "Tab in non-makefile text file"
                stderr.println(`"$file" contains a tab"`)
            }
            write(file, text)
        } else if (verboseFlag) {
            println(`# $file`)
        }
    }
}

if (errorMsg != null) {
    throw(errorMsg)
}
