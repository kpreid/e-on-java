#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.8")
pragma.disable("explicit-result-guard")

#introducer.onTheAir()

def printLater(prefix :String, info :vow) {
    when (info) -> goodData(infoReady) {
        println(prefix + info)
    } catch prob {println("failed: " + prefix + prob)}
}

def <donut> := <import:com.hp.donutLab.*>

# boot comm vat mint
#def makeVat := <unsafe:org.erights.e.elib.vat.Vat>
#def mintVat := makeVat.make("headless", "mint")
#def seedVat := <elang:interp.seedVatAuthor>(<unsafe>)
#def mint :ref := seedVat(mintVat,
#    `<import:com.hp.donutLab.makeIOUMint>("Sugar")`)

#local mint
#def mint := <donut:makeIOUMint>("Sugar")

println("about to make mint in vat")
#true vat mint
def makeServer := <elang:interp.makeServerAuthor>(<unsafe>, introducer)
introducer.onTheAir()
def [makeMint, mintEnv, mintVat] :=
  makeServer("<import:com.hp.donutLab.makeIOUMint>")
def mint :ref := makeMint <- run("Sugar")

println("made mint in vat")

printLater("mint brand:" , mint <- getBrand() <- getNickName())
def pairVow := mint <- makeDonutAccountPair(300)
def account := pairVow <- get(0)
def accountRevoker := pairVow <- get(1)

#interp.waitAtTop(pairVow)
#def [account, accountRevoker] := mint <- makeDonutAccountPair(300)

printLater("account300: ", account <- getBalance())

println("about to make kiosk pair")

def makeKioskPair := <donut:makeKioskPair>
def [k1, kboss1] := makeKioskPair(account, null, "K1", println)
def blah{}
def blahPosted := k1 <- postAdvertisement("blah", account<-offer(1), 10, blah)
when (blahPosted) -> blahed(bl) {
    def blahServers := k1 <- getAdvertisedServers("blah")
    printLater("got blah servers: " , blahServers)
} catch blahProb {println("blah post failed: " + blahProb)}

def [k2, kboss2] := makeKioskPair(account, k1, "K2", println)

#wait for account balance so the payments settle
when (account <- getBalance() ) -> gotBal(bal) {
    printLater("got k2 early kiosks: ", k2 <- getAdvertisedServers("kiosk"))
    printLater("got k1 kiosks: " , k1 <- getAdvertisedServers("kiosk"))
} catch deadBal {println("getBalance failed: " + deadBal)}


interp.blockAtTop()
