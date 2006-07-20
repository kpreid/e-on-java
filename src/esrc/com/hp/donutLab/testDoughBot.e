#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.disable("explicit-result-guard")

def printLater(prefix :String, info :vow) {
    when (info) -> goodData(infoReady) {
        println(prefix + info)
    } catch prob {println("failed: " + prefix + prob)}
}

def latePair(basePair :rcvr) :near {
    return [basePair <- get(0), basePair <- get(1)]
}

def <donut> := <import:com.hp.donutLab.*>
def doughBot := <donut:makeDoughBot>

println("about to make mint in vat")
#true vat mint
def makeServer := <elang:interp.makeServerAuthor>(<unsafe>, introducer)
introducer.onTheAir()
def [makeMint, mintEnv, mintVat] :=
  makeServer("<import:com.hp.donutLab.makeIOUMint>")
def mint :rcvr := makeMint <- run("Sugar")

printLater("mint brand:" , mint <- getBrand() <- getNickName())
def pairVow := mint <- makeDonutAccountPair(300)
def account := pairVow <- get(0)
def accountRevoker := pairVow <- get(1)

printLater("account300: ", account <- getBalance())

def makeKioskPair := <donut:makeKioskPair>
def [k1, kboss1] := makeKioskPair(account, null, "K1", println)


def [k2, kboss2] := makeKioskPair(account, k1, "K2", println)

#wait for account balance so the payments settle
when (account <- getBalance()) -> gotBal(bal) {
    printLater("got k2 early kiosks: ", k2 <- getAdvertisedServers("kiosk"))
    printLater("got k1 kiosks: " , k1 <- getAdvertisedServers("kiosk"))
} catch deadBal {println("getBalance failed: " + deadBal)}

def makeSliverServerPair :=
  <donut:sliverServerPair>(println, timer, makeServer, introducer)
def [ss1, ss1boss] :=
  latePair( makeSliverServerPair <- run("ss1", account, k1, <file:.>))

when (makeSliverServerPair) -> yup(mssp) {
    println("got sliverserverpairmaker: " + makeSliverServerPair)
} catch prob {println("no sliverserverpairmaker: " + prob)}

printLater("ss1: ", ss1)
printLater("ss1boss " ,ss1boss)

def sliverSource := "
    def computer {
        to runSliver(s1, t1) :near {return 5}
    }"
def ss1Answer := ss1 <- makeSliver(sliverSource, account <- offer(20))
printLater("got computation, should be 5: ", ss1Answer)

#do doughBot
def source := <resource:com/hp/donutLab/makeDoughBot.emaker>.getText()
def [doughAcct, doughAcctRevoker] := latePair(mint <- makeDonutAccountPair(4))
doughBot.makeSlice(<unsafe>, doughAcct, k1, source, println)


interp.blockAtTop()
