#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# Used during the linux build to insert the version into the installed
# eprops.txt file.

if (interp.getArgs().size() != 3) {
    throw("usage: replace <subjectFile> <varName> <contents>")
}

def subject := <file>[interp.getArgs()[0]]
def varName := interp.getArgs()[1]
def contents := interp.getArgs()[2]

def `@left$$($varName)@right` := subject.getText()
subject.setText(`$left$contents$right`)
