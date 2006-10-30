#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

def currDir := <file:.>

def <donut> := <import:com.hp.donutLab.*>
def name := currDir["kioskName.txt"].getText().trim()
def account
def baseKiosk

def [kiosk, boss] := <donut:makeKioskPair>(account, baseKiosk, name, println)
def makeAtomicFile := <import:org.erights.e.extern.persist.makeAtomicFile>

def vatFile := makeAtomicFile(currDir, "kiosk.vat")


timeMachine.addExit(kiosk, "kiosk", true)
timeMachine.addExit(boss, "boss", true)

timeMachine.new()
timeMachine.createAs(vatFile)
introducer.onTheAir()

def accountDoc := currDir["account.cap-account"]
if (accountDoc.exists()) {
    def sturdyAccount := introducer.sturdyFromURI(accountDoc.getText())
    bind account := sturdyAccount.getRcvr()
}

def baseKioskDoc := currDir["baseKiosk.cap-kiosk"]
if (baseKioskDoc.exists()) {
    def sturdyBase := introducer.sturdyFromURI(baseKioskDoc.getText())
    bind baseKiosk := sturdyBase.getRcvr()
}

def sturdyKiosk := makeSturdyRef(kiosk)
def sturdyBoss := makeSturdyRef(boss)
timeMachine.save()

currDir["kiosk.cap-kiosk"].setText(introducer.sturdyToURI(sturdyKiosk))
currDir["kioskboss.cap-kioskboss"].setText(introducer.sturdyToURI(sturdyBoss))

println("Kiosk ready to service requests")
interp.blockAtTop()

