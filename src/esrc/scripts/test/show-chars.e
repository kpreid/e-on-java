#!/usr/bin/env rune

# Copyright 2006 Hewlett Packard, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

# XXX make more controllable by command-line options

def showChar(c :char) {
    println(`"${c.escaped()}" ${c.asInteger()} ${c.getCategory()} $c`)
}

for i in 0..300 { showChar(i.asChar()) }
