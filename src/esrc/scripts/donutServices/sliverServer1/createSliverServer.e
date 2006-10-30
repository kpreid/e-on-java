#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

def currDir := <file:.>

def <donut> := <import:com.hp.donutLab.*>
def account
def kiosk
def name := currDir["name.txt"].getText().trim()
def makeBootServer := <elang:interp.makeServerAuthor>.boot(<unsafe>)

def makeSliverServerPair := <donut:sliverServerPair>(println,
                                                     timer,
                                                     makeBootServer,
                                                     introducer)
def [sliverServer, boss] := makeSliverServerPair(name, account, kiosk, currDir)

def makeAtomicFile := <import:org.erights.e.extern.persist.makeAtomicFile>
def vatFile := makeAtomicFile(currDir, "sliverServer.vat")

timeMachine.addExit(sliverServer, "sliverServer", true)
timeMachine.addExit(boss, "boss", true)

timeMachine.new()
timeMachine.createAs(vatFile)
introducer.onTheAir()

def accountDoc := currDir["account.cap-account"]
def sturdyAccount := introducer.sturdyFromURI(accountDoc.getText())
bind account := sturdyAccount.getRcvr()

def kioskDoc := currDir["kiosk.cap-kiosk"]
def sturdyBase := introducer.sturdyFromURI(kioskDoc.getText())
bind kiosk := sturdyBase.getRcvr()

def sturdyServer := makeSturdyRef(sliverServer)
def sturdyBoss := makeSturdyRef(boss)

timeMachine.save()

currDir["sliverServer.cap-ss"].setText(introducer.sturdyToURI(sturdyServer))
currDir["sliverBoss.cap-ssboss"].setText(introducer.sturdyToURI(sturdyBoss))

println(`SliverServer $name ready to service requests`)
interp.blockAtTop()
