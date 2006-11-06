#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2005 Mark S. Miller, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

for `@name.tex` => f in <file:markm-thesis> {
    for ln => line in f {
        for cn => c in line {
            def i := c.asInteger()
            if (i >= 127 || (c < ' ' && c != '\n')) {
                println(`$i at $name.tex::$ln:$cn`)
            }
        }
    }
}
