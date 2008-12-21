#!/usr/bin/env rune

# Copyright 2005-2008 Kevin Reid, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# This program generates Mac OS X application bundles (.app) wrapping E-SWT
# programs, so that they will run properly on Mac OS X.
#
# Usage: macify-swt.e [--name-program] [--arg <arg>] <source>.e-swt <output>.app
#
# If --name-program is passed, then the program will be referenced at
# its original absolute path from the bundle, rather than being copied
# into the bundle. --arg specifies a command-line argument to be
# passed to the program at startup; it may be used several times for
# multiple arguments.

# TODO:
#   bundle options like icons, name, specific bundle ID
#   option to invoke PATH's rune
#   option to prompt for args when app is launched?
#   catch open events at launch time and turn into args
#   establish protocol for catching open events after launch

pragma.syntax("0.9")
pragma.enable("accumulator")

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

def programOptions := {[
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
                               => programOption := programOptions["copy"],
                               => bakedArgs := []]) {
    def contents := target["Contents"]
    def platformDir := contents["MacOS"]
    platformDir.mkdirs(null)

    def runeCmd := shesc(if (optEHome != null) { `$optEHome/rune` } \
                                          else { "rune" })

    def scriptArg := programOption(script, platformDir)

    def executable := platformDir["run"]
    executable.setText(`$\
#!/bin/sh
exec $runeCmd $scriptArg${accum "" for x in bakedArgs {_ + (" " + shesc(x))}}
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
    var programOption := programOptions["copy"]
    var bakedArgs := []

    while (true) {
        switch (args) {
            match [`--@what-program`] + r { 
                args := r
                programOption := programOptions[what] 
            }
            match [`--arg`, ba] + r {
              args := r
              bakedArgs with= ba
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
               => programOption, => bakedArgs])
}

main(interp.getArgs(), <file>, entropy, interp.getProps()["e.home"])

