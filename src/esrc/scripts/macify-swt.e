#!/usr/bin/env rune

# Copyright 2005-2006 Kevin Reid, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

# TODO:
#   bundle options like icons, name, specific bundle ID
#   option to invoke PATH's rune

def chmod := makeCommand("chmod")
def setExecutable(file) {
    chmod("+x", file.getPlatformPath())
}

def xmlesc(s :String) {
    return s.replaceAll("&", "&amp;").
      replaceAll("<", "&lt;").
      replaceAll(">", "&gt;").
      replaceAll("'", "&apos;").
      replaceAll("\"", "&quot;")
}
def shesc(s :String) {
    return "'" + s.replaceAll("'", "'\''") + "'"
}

def scriptOptions := {[
    "copy" => fn script, platformDir { 
        platformDir["script.e-swt"].setText(script.getText())
        `"``dirname $$0``/script.e-swt"`
    },
    "name" => fn script, _ { 
        shesc(script.getPlatformPath())
    },
]}

def macifySwt(script, target, [=> bundleID, 
                               => optEHome,
                               => scriptOption := scriptOptions["copy"]]) {
    def contents := target["Contents"]
    def platformDir := contents["MacOS"]
    platformDir.mkdirs(null)

    def runeCmd := shesc(if (optEHome != null) { `$optEHome/rune` } \
                                          else { "rune" })

    def scriptArg := scriptOption(script, platformDir)

    def executable := platformDir["run"]
    executable.setText(`$\
#!/bin/sh
exec $runeCmd -J-XstartOnFirstThread $scriptArg
`)
    setExecutable(executable)
    
    # XXX use an XML (or plist) generator instead  
    contents["Info.plist"].setText(`$\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
        <key>CFBundleInfoDictionaryVersion</key>
        <string>6.0</string>
        <key>CFBundleExecutable</key>
        <string>run</string>
        <key>CFBundleIdentifier</key>
        <string>${xmlesc(bundleID)}</string>
</dict>
</plist>`)
}

def main(var args, <file>, entropy, eHome) {
    var scriptOption := scriptOptions["copy"]

    while (true) {
        switch (args) {
            match [`--@what-script`] + r { 
                args := r
                scriptOption := scriptOptions[what] 
            }
            match [`--`] + r { break }
            match [`-@o`] + _ { throw(`unrecognized option: -$o`) }
            match _ { break }
        }
    }
    def [scriptPath, targetPath] := args

    macifySwt(def script := <file>[scriptPath].deepReadOnly(), 
              <file>[targetPath],
              ["bundleID" => `org.erights.e.mac-bundle.${script.getName()}-${entropy.nextSwiss()}`, 
               "optEHome" => eHome,
               => scriptOption])
}

main(interp.getArgs(), <file>, entropy, interp.getProps()["e.home"])

