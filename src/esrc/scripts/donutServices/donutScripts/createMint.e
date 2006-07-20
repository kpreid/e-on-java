#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.disable("explicit-result-guard")

def currDir := <file:.>

def <donut> := <import:com.hp.donutLab.*>

def mintName := currDir["mintName.txt"].getText().trim()
def mint := <donut:makeIOUMint>(mintName)

def [k1acct, k1acctboss] := mint.makeDonutAccountPair(500, "k1acct")
def [k2acct, k2acctboss] := mint.makeDonutAccountPair(500, "k2acct")
def [ss1acct, ss1acctboss] := mint.makeDonutAccountPair(500, "ss1acct")
def [ss2acct, ss2acctboss] := mint.makeDonutAccountPair(500, "ss2acct")
def [ss3acct, ss3acctboss] := mint.makeDonutAccountPair(500, "ss3acct")
def [ss4acct, ss4acctboss] := mint.makeDonutAccountPair(500, "ss4acct")
def [db1acct, db1acctboss] := mint.makeDonutAccountPair(500, "db1acct")

def makeAtomicFile := <import:org.erights.e.extern.persist.makeAtomicFile>
def vatFile := makeAtomicFile(currDir, "mint.vat")


timeMachine.addExit(mint, "mint", true)
timeMachine.addExit(k1acct, "k1acct", true)
timeMachine.addExit(k1acctboss, "k1acctboss", true)
timeMachine.addExit(k2acct, "k2acct", true)
timeMachine.addExit(k2acctboss, "k2acctboss", true)
timeMachine.addExit(ss1acct, "ss1acct", true)
timeMachine.addExit(ss1acctboss, "ss1acctboss", true)
timeMachine.addExit(ss2acct, "ss2acct", true)
timeMachine.addExit(ss2acctboss, "ss2acctboss", true)
timeMachine.addExit(ss3acct, "ss3acct", true)
timeMachine.addExit(ss3acctboss, "ss3acctboss", true)
timeMachine.addExit(ss4acct, "ss4acct", true)
timeMachine.addExit(ss4acctboss, "ss4acctboss", true)
timeMachine.addExit(db1acct, "db1acct", true)
timeMachine.addExit(db1acctboss, "db1acctboss", true)

timeMachine.new()
timeMachine.createAs(vatFile)
introducer.onTheAir()

def sturdyMint := makeSturdyRef(mint)

def sturdyk1acct := makeSturdyRef(k1acct)
def sturdyk1acctboss := makeSturdyRef(k1acctboss)
def sturdyk2acct := makeSturdyRef(k2acct)
def sturdyk2acctboss := makeSturdyRef(k2acctboss)
def sturdyss1acct := makeSturdyRef(ss1acct)
def sturdyss1acctboss := makeSturdyRef(ss1acctboss)
def sturdyss2acct := makeSturdyRef(ss2acct)
def sturdyss2acctboss := makeSturdyRef(ss2acctboss)
def sturdyss3acct := makeSturdyRef(ss3acct)
def sturdyss3acctboss := makeSturdyRef(ss3acctboss)
def sturdyss4acct := makeSturdyRef(ss4acct)
def sturdyss4acctboss := makeSturdyRef(ss4acctboss)
def sturdydb1acct := makeSturdyRef(db1acct)
def sturdydb1acctboss := makeSturdyRef(db1acctboss)




timeMachine.save()

def makeCap(sturdy, filename) {
    currDir[filename].setText(introducer.sturdyToURI(sturdy))
}

makeCap(sturdyMint, "mint.cap")
makeCap(sturdyk1acct, "k1acct.cap-acct")
makeCap(sturdyk1acctboss, "k1acct.cap-acctboss")
makeCap(sturdyk2acct, "k2acct.cap-acct")
makeCap(sturdyk2acctboss, "k2acct.cap-acctboss")
makeCap(sturdyss1acct, "ss1acct.cap-acct")
makeCap(sturdyss1acctboss, "ss1acct.cap-acctboss")
makeCap(sturdyss2acct, "ss2acct.cap-acct")
makeCap(sturdyss2acctboss, "ss2acct.cap-acctboss")
makeCap(sturdyss3acct, "ss3acct.cap-acct")
makeCap(sturdyss3acctboss, "ss3acct.cap-acctboss")
makeCap(sturdyss4acct, "ss4acct.cap-acct")
makeCap(sturdyss4acctboss, "ss4acct.cap-acctboss")
makeCap(sturdydb1acct, "db1acct.cap-acct")
makeCap(sturdydb1acctboss, "db1acct.cap-acctboss")

println("Mint ready to service accounts")
interp.blockAtTop()
