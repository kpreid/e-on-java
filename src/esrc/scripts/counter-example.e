#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

introducer.onTheAir()

var x := 0

def counter {
    to incr() :any {
        x += 1
    }
}

def sr := makeSturdyRef(counter)

def uri := introducer.sturdyToURI(sr)

switch (interp.getArgs()) {
    match [] { println(uri) }
    match [`-`] { println(uri) }
    match [fname] { <file>[fname].setText(uri) }
}

interp.blockAtTop()
