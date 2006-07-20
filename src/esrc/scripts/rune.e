#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def outcome :vow[boolean] := rune(interp.getArgs())

interp.waitAtTop(outcome)

interp.exitAtTop(Ref.optProblem(outcome))
