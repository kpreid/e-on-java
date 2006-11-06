#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def <donut> := <import:com.hp.donutLab.*>
def mint := <donut:makeIOUMint>("Sugar")
println("mint brand:" + mint.getBrand().getNickName())
def [account, accountRevoker] := mint.makeRevocableAccountPair(300)
println("account300: " + account.getBalance())

def [acct2, acct2Revoker] := mint.makeRevocableAccountPair(100)
def offer := account.offer(35)
println("account after offer of 35: " + account.getBalance())
def acceptor := acct2.accept()
def transferer := mint.makeTransferer()
transferer.transfer(offer, acceptor)
acceptor.refund()
println("acct2 after 35 added: " + acct2.getBalance())
acct2Revoker.revoke()
println("acct2 balance after revocation: ")
try {println(acct2.getBalance())} catch err {println("failed correctly")}

def [doAcct1, r1] := mint.makeDonutAccountPair(400)
def [doA2, r2] := mint.makeDonutAccountPair(500)
def doOffer := doAcct1.offer(75)
println("doAcct1 after offer of 75: " + doAcct1.getBalance())
def acceptor3 := doA2.accept()
def amountTransferred := doA2.transfer(doOffer, acceptor3)
acceptor3.refund()
println("do transferred: " + amountTransferred)
println("after trans, doA2 has: " + doA2.getBalance())
r2.revoke()
try {
    println("after revoke, doA2 has: " + doA2.getBalance())
} catch probl {println("fails properly after revoke")}


interp.blockAtTop()
