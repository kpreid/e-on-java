#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def copyOnly(old, new, ext) :void {
    if (old.isDirectory()) {
        # stderr.println(`copyOnly($old, $new, $ext)`)
        if (! new.exists()) {
            new.mkdirs(null)
        }
        require(new.isDirectory())
        for name => sub in old {
            copyOnly(sub, new[name], ext)
        }
    } else if (old.getName().endsWith(ext)) {
        new.setBytes(old.getBytes())
    }
}

if (interp.getArgs() =~ [oldPath, newPath, extension]) {
    # stderr.println(`copyOnly.e $oldPath, $newPath, $extension`)
    copyOnly(<file>[oldPath], <file>[newPath], extension)
} else {
    throw("usage: copyOnly oldPath newPath extension")
}
