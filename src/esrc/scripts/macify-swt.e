#!/usr/bin/env rune

# Copyright 2005-2006 Kevin Reid, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

def chmod := makeCommand("chmod")
def setExecutable(file) {
    chmod("+x", file.getPlatformPath())
}

def xmlesc(s :String) {
    return s.replaceAll("&", "&amp;").
      replaceAll("<", "&lt;").
      replaceAll(">", "&gt;").
      replaceAll("'","&apos;").
      replaceAll("\"", "&quot;")
}
def shesc(s :String) {
    return "'" + s.replaceAll("'", "'\''") + "'"
}

def macifySwt(script, target, bundleID, eHome) {
    def contents := target["Contents"]
    def platformDir := contents["MacOS"]
    platformDir.mkdirs(null)

    def shehome := shesc(eHome)

    def executable := platformDir["run"]
    executable.setText(`$\
#!/bin/sh
exec $shehome/rune \
    -J-XstartOnFirstThread \
    -cpb $shehome/src/bin/mac/swt.jar \
    -Djava.library.path=$shehome/src/bin/mac/ppc \
    "``dirname $$0``/script.e-swt"
`)
    setExecutable(executable)

    platformDir["script.e-swt"].setText(script.getText())

    # XXX use an XML (or plist) generator instead
    contents["Info.plist"].setText(`$\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple Computer//DTD PLIST 1.0//EN" "http://
www.apple.com/DTDs/PropertyList-1.0.dtd">
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

def [scriptPath, targetPath] := interp.getArgs()
macifySwt(def script := <file>[scriptPath],
          <file>[targetPath],
          `org.erights.e.mac-bundle.${script.getName()}-${entropy.nextSwiss()}`,
          interp.getProps()["e.home"])


