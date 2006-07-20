#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def usage := "usage: substProps.e <fname> (<name>=<value>)*"

def fname
def args
if (interp.getArgs() !~ [bind fname] + bind args) {
    interp.exitAtTop(usage)
}

var txt := <file>[fname].getText()

for `@name=@value` in args {
    if (txt =~ `@left$${{$name}}@right`) {
        txt := `$left$value$right`
    } else {
        stderr.println(`not found: $name`)
    }
}

<file>[fname].setText(txt)
