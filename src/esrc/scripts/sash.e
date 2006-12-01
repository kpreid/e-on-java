#!/usr/bin/env rune

# Copyright 2006 Hewlett-Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# By Mark S. Miller
# based on Marc Stiegler's sash in Emily 
# and on Matej Kosik's host.e in his "Powerbox Rants"

pragma.syntax("0.9")

def eParse := <elang:syntax.makeEParser>
def makeQuoteln := <elang:interp.makeQuoteln>

def asAuth(arg) {
    switch (arg) {
        match `=@fname` { return <file>[fname].deepReadOnly() }
        match `+@fname` { return <file>[fname] }
        match `^@expr`  { return eParse(expr).eval(privilegedScope) }
        match _         { return arg }
    }
}

def USAGE := "\
usage: sash.e <pluginFname> <arg>...
where
    <pluginFname> is the file path name of a sash plugin (a *.sash file).
    <arg> is one of
        =<fname>  Gives read-only access to the files at <fname>,
        +<fname>  Gives read-write access to the files at <fname>.
        ^<expr>   The result of evaling <expr> in the privileged scope.
        <string>  Anything else is provided as a literal string.
The plugin is called with (stdin, userOut, auth,...).
"

if (interp.getArgs() =~ [pluginFname] + args) {
    def pluginExpr := <file>[pluginFname].getTwine()
    def plugin := eParse(pluginExpr).eval(safeScope)
    
    def userOut := makeQuoteln(println, `Command $pluginFname`, 78)
    def auths := [stdin, userOut].diverge()
    for arg in args {
        auths.push(asAuth(arg))
    }
    try {
        E.call(plugin, "run", auths.snapshot())
    } catch ex {
        println(help(plugin))
        throw(ex)
    }
    
} else {
    println(USAGE)
}
