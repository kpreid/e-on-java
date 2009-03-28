#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

def currDir := <file:.>
def <donut> := <import:com.hp.donutLab.*>
def account
def baseKiosk
var kioskName := "kiosk"
def nameFile := currDir["kioskName.txt"]
if (nameFile.exists()) {kioskName := nameFile.getText()}
def [kiosk, boss] := <donut:makeKioskPair>(account,
                                           baseKiosk,
                                           kioskName,
                                           println)

def makeAtomicFile := <import:org.erights.e.extern.persist.makeAtomicFile>
def vatFile := makeAtomicFile(currDir, "kiosk.vat")
timeMachine.addExit(kiosk, "kiosk", true)
timeMachine.addExit(boss, "boss", true)
timeMachine.revive(vatFile)

introducer.onTheAir()

# bind the account and the baseKiosk
def accountDoc := currDir["account.cap-account"]
if (accountDoc.exists()) {
    def sturdyAccount := introducer.sturdyFromURI(accountDoc.getText())
    bind account := sturdyAccount.getRcvr()
} else {
    def mint := <donut:makeIOUMint>("Sugar")
    bind account := mint.makeDonutAccountPair(300)
}
def baseKioskDoc := currDir["baseKiosk.cap-kiosk"]
if (baseKioskDoc.exists()) {
    def sturdyBase := introducer.sturdyFromURI(baseKioskDoc.getText())
    bind baseKiosk := sturdyBase.getRcvr()
}

println("Kiosk ready to service requests again")
interp.blockAtTop()

