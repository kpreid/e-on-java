#!/usr/bin/env rune

pragma.syntax("0.9")

# Copyright 2006 Hewlett-Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def eParse := <elang:syntax.makeEParser>

def asAuth(arg) {
    switch (arg) {
        match `=@fname` { return <file>[fname].deepReadOnly() }
        match `+@fname` { return <file>[fname] }
        match `^@expr`  { return eParse(expr).eval(privilegedScope) }
        match _         { return arg }
    }
}

if (interp.getArgs() =~ [commandName] + args) {
    def auths := [].diverge()
    for arg in args {
        auths.push(asAuth(arg))
    }
    def command := <import>[commandName]
    def userOut(message :String) {
        print(`Command $commandName said:$\n> `)
        var currentLineCharCount := 0
        def newline() {
            print("\n> ")
            currentLineCharCount := 0
        }
        for c in message {
            if (c == '\n') {
                newline()
            } else if (["Cc","Co"].contains(c.getCategory())) {
                # ignore for now
            } else {
                print(`$c\`)
                currentLineCharCount += 1
                if (currentLineCharCount > 80) {
                    newline()
                }
            }
        }
        println()
    }
    def result := E.call(command, "run", [stdin, userOut] + auths)
    if (null != result) {
        userOut(`$result`)
    }
} else {
    println("\
usage: sash.e <commandName> <arg>...
where
    <commandName> is the fully qualified name of an importable,
                  such as a *.emaker module.
    <arg> is one of
        =<fname>  Gives read-only access to the files at <fname>,
        +<fname>  Gives read-write access to the files at <fname>.
        ^<expr>   The result of evaling <expr> in the privileged scope.
        <string>  Anything else is provided as a literal string.
The command is imported and called with (stdin, userOut, auth,...).
")
}
