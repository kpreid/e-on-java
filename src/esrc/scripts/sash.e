#!/usr/bin/env rune

# Copyright 2006 Hewlett-Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# By Mark S. Miller
# based on Marc Stiegler's sash in Emily
# and on Matej Kosik's host.e in his "Powerbox Rants"

pragma.syntax("0.9")

def eParse := <elang:syntax.makeEParser>
def isIdentifier := <elang:syntax.makeELexer>.isIdentifier
def makeQuoteln := <elang:interp.makeQuoteln>

def asAuth(arg) {
    switch (arg) {
        match `ro:@fname` { return <file>[fname].deepReadOnly() }
        match `rw:@fname` { return <file>[fname] }
        match `e:@expr`   { return eParse(expr).eval(privilegedScope) }
        match `q:@str`    { return str }
        match `@id:@_` ? (isIdentifier(id)) { throw (`reserved "$id"`) }
        match _           { return arg }
    }
}

def USAGE := "\
usage: sash.e <pluginFname> <arg>...
where
    <pluginFname> is the file path name of a sash plugin (a *.sash file).
    <arg> is one of
        ro:<fname>  Gives read-only access to the files at <fname>,
        rw:<fname>  Gives read-write access to the files at <fname>.
        e:<expr>    The result of evaling <expr> in the privileged scope.
        q:<string>  <string> is provided as a literal string
        <ident>:*   All other prefixes are reserved
        <string>    Anything else is provided as a literal string.
The plugin is called with (powerbox, auth,...).
"

if (interp.getArgs() =~ [pluginFname] + args) {
    def pluginExpr := <file>[pluginFname].getTwine()
    def plugin := eParse(pluginExpr).eval(safeScope)

    def userOut := makeQuoteln(println, `Command "$pluginFname" said:`, 78)
    def requestOut := makeQuoteln(println, `Command "$pluginFname" asks:`, 78)
    def powerbox {
        to readLine() { return stdin <- readLine() }
        to println(str) { userOut(str) }
        to requestAuth(why :String) {
            requestOut(why)
            print("? ")
            def line := stdin <- readLine()
            return when (line) -> { eParse(line).eval(privilegedScope) }
        }
    }
    def auths := [powerbox].diverge()
    for arg in args {
        auths.push(asAuth(arg))
    }
    try {
        interp.waitAtTop(E.call(plugin, "run", auths.snapshot()))
    } catch ex {
        println(help(plugin))
        throw(ex)
    }

} else {
    println(USAGE)
}
