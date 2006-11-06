#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def traceline(str) :void { stderr.println(str) }

def upgradeComments :=
  <import:com.skyhunter.convertForEDoc.makeUpgradeComments>(traceline)

def endsWithAny(name, suffixList) :boolean {
    for suffix in suffixList {
        if (name.endsWith(suffix)) {
            return true
        }
    }
    false
}

/**
 * @author Mark S. Miller
 */
def upgrade(oldFiledir, newFiledir) :void {
    if (oldFiledir.isDirectory()) {
        require(newFiledir.exists() || newFiledir.mkdirs(null),
                thunk{`Couldn't make ${newFiledir.getPath()}`})
        for name => sub in oldFiledir {
            upgrade(sub, newFiledir[name])
        }
    } else if (endsWithAny(oldFiledir.getName(), [".updoc",
                                                  ".caplet",
                                                  ".e", ".e-awt", ".e-swt",
                                                  ".emaker",
                                                  ".txt"])) {
        def oldText := oldFiledir.getText()
        def newText := upgradeComments(oldText)
        if (oldText != newText) {
            traceline(newFiledir.getPath())
            newFiledir.setText(newText)
        }
    }
}

if (interp.getArgs() =~ [oldName, newName]) {
    upgrade(<file>[oldName], <file>[newName])
} else if (interp.getArgs() =~ []) {
    # XXX eBrowser run-button hack
#    upgrade(<c:/e/src/esrc/com/skyhunter/\
#convertForEDoc/makeUpgradeComments.emaker>,
#            <c:/e16b/e/src/esrc/com/skyhunter/\
#convertForEDoc/makeUpgradeComments.emaker>)
    upgrade(<c:/e/src/esrc>, <c:/e16b/e/src/esrc>)
} else {
    throw("usage: upgradeComments <oldFilename> <newFilename>")
}
