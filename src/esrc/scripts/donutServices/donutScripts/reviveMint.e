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


timeMachine.revive(vatFile)
introducer.onTheAir()

timeMachine.save()

println("Mint ready to service accounts again")
interp.blockAtTop()
