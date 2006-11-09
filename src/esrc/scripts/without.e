#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................


def getReader(name :String) :near {
    return <file>[name].textReader()
}

def reader1
def reader2

# XXX Should use argParser
switch (interp.getArgs()) {
    match [filename1, filename2] {
        bind reader1 := getReader(filename1)
        try {
            bind reader2 := getReader(filename2)
        } catch problem {
            reader1.close()
            throw(problem)
        }
    }
    match [`--help`] {
        println("\
usage: without.e file1 file2

Given that both files contain a sorted list of lines, this outputs those lines
from file1 that are not in file2.")
        interp.exitAtTop()
    }
    match [`--version`] {
        rune(["--version"])
        interp.exitAtTop()
    }
    match _ {
        throw("usage: without.e path1 path2")
    }
}

# XXX Should make a separate emaker
try {
    var optLine1 := reader1.readLine()
    var optLine2 := reader2.readLine()

    def next1() :void {
        require(null != optLine1)
        def optNext1 := reader1.readLine()
        require(null == optNext1 || optLine1 < optNext1,
                fn{`First file must be sorted:
${E.toQuote(optLine1)}
isn't less than
${E.toQuote(optNext1)}`})

        optLine1 := optNext1
    }

    def next2() :void {
        require(null != optLine2)
        def optNext2 := reader2.readLine()
        require(null == optNext2 || optLine2 < optNext2,
                fn{`Second file must be sorted:
${E.toQuote(optLine2)}
isn't less than
${E.toQuote(optNext2)}`})

        optLine2 := optNext2
    }

    while(null != optLine1) {
        if (null == optLine2 || optLine1 < optLine2) {
            println(optLine1)
            next1()
        } else if (optLine1 > optLine2) {
            next2()
        } else {
            next1()
            next2()
        }
    }
} finally {
    reader1.close()
    reader2.close()
}

