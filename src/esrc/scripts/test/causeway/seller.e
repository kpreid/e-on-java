#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

introducer.onTheAir()

(
    def tcr := <unsafe:org.erights.e.develop.trace.TraceController>
    tcr.setProperty("TraceLog_causality", "debug")
    
    def seller {
        
        to checkAvailability(partNo :String) :boolean { true }
        
        to verifyCredit(buyer :String) :boolean { true }
        
        to confirmDeliveryOptions(buyer :String) :boolean { true }
        
        to placeOrder(buyer :String, partNo :String) :boolean { true }
    }
    
    def sr := makeSturdyRef.temp(seller, timer.now() + 3_600_000)
    def uri := introducer.sturdyToURI(sr)
    <file:~/Desktop/seller.cap>.setText(uri)
    println("waiting...")
    
    interp.blockAtTop()
)
