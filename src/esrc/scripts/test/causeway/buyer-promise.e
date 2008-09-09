#!/usr/bin/env rune

pragma.syntax("0.9")

# Copyright 2008 Teleometry Design under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# This is a toy implementation of a purchase order system,
# written to demonstrate the Causeway Debugger.

def asyncAnd := <import:org.erights.e.examples.concurrency.asyncAnd>

def traceline(str) :void { stderr.println(`$str`) }
def report(message) :void { traceline(message) }

def args := interp.getArgs()
def productVow
if (args =~ [`--local`] + _) {
    traceline("local")
    bind productVow := <import:scripts.test.causeway.product-promise>
} else {
    def makeVat := <unsafe:org.erights.e.elib.vat.makeVat>
    def productVat := makeVat.make("headless", "product")
    var seedVat := <elang:interp.seedVatAuthor>(<unsafe>)
    if (args =~ [`--captp`] + _) {
	traceline("captp")
	introducer.onTheAir()
	seedVat := seedVat.virtualize(introducer)
    } else {
	traceline("boot")
    }
    bind productVow := seedVat(productVat,
			       "<import:scripts.test.causeway.product-promise>")
}

def partNo := "123abc"
def name := "West Coast Buyers"
def profile := "West Coast Buyers Profile"

when (productVow) -> {
    def [=> inventory, => creditBureau, => shipper] := productVow

    # Turn on causality tracing.
    def tcr := <unsafe:org.erights.e.develop.trace.makeTraceController>
    tcr.setProperty("TraceLog_causality", "debug")
    
    def promises := [inventory <- isAvailable(partNo),
		     creditBureau <- doCreditCheck(name),
		     shipper <- canDeliver(profile)]
    def allOK := asyncAnd(promises)
    
    when (allOK) -> {
	# All conditions must be met before an order is placed.
        if (allOK) {
            def placed := inventory <- placeOrder(name, partNo)
	    when (placed) -> {
		if (placed) {
		    report(`Order placed for $name, $partNo`)
		} else {
		    report(`Order for $name, $partNo not placed`)
		}
		tcr.setProperty("TraceLog_causality", "warning")
		interp.exitAtTop()
            }
        }
    }
}

interp.blockAtTop()
