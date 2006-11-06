#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

println(currentVat)

timer.whenPast(timer.now() + 10_000, thunk{
    println("time's up")
    # interp.exitAtTop("foo")
    interp.continueAtTop()
})

interp.blockAtTop()

println("last")
