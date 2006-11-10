#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

def currDir := <file:.>

def <donut> := <import:com.hp.donutLab.*>
def account
def kiosk
def name := currDir["name.txt"].getText().trim()
println("DoughBot: " + name)


introducer.onTheAir()

def accountDoc := currDir["account.cap-account"]
def sturdyAccount := introducer.sturdyFromURI(accountDoc.getText())
bind account := sturdyAccount.getRcvr()
def balance :=
when (def balance := account <- getBalance()) -> {
    println("doughbot starting balance: " + balance)
}catch prob {}

def kioskDoc := currDir["kiosk.cap-kiosk"]
def sturdyBase := introducer.sturdyFromURI(kioskDoc.getText())
bind kiosk := sturdyBase.getRcvr()

def doughBot := <donut:makeDoughBot>
def source := <resource:com/hp/donutLab/makeDoughBot.emaker>.getText()
println(`DoughBot $name about to attack`)
doughBot.makeSlice(<unsafe>, account, kiosk, source, println)


interp.blockAtTop()

