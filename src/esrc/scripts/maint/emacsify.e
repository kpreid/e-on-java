#!/usr/bin/env rune

pragma.syntax("0.9")

# Copyright 2006 Hewlett Packard, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def USAGE := "emacsify [file...]
emacsify reads textual reports and outputs to stderr their contents in 
a format emacs expects for compiler output -- where the source positions are
turned into a format emacs \"C-x `\" recognizes.
"

def OLD := "c:/Documents and Settings/millerm1"
def NEW := "~"

def emacsify(file) {
    for rawline in file {
        def line := rawline.replaceAll("%20"," ").replaceAll("<file:/C:/","<file:c:/")
        if (line =~ `@pre<file:$OLD/@path#@kind::@span>@post`) {
            stderr.println(`error: $NEW/$path($span)`)
            stderr.println(`$pre $post`)
            continue
        }
        if (line =~ `@{pre}org.erights.@qname(@className.java:@num)@post`) {
            def slashed := qname.replaceAll(".","/").replaceAll("$","/")
            if (slashed =~ `@pkg/$className/@_`) {
                stderr.println(`~/e/src/jsrc/org/erights/$pkg/$className.java($num)`)
                stderr.println(`$pre $post`)
                continue
            }
        }
        stderr.println(line)
    }
}

switch (interp.getArgs()) {
    match [`--help`] {
        println(USAGE)
    }
    match args {
        for arg in interp.getArgs() {
            emacsify(<file>[arg])
        }
    }
}
