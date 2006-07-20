#!/usr/bin/env rune

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
