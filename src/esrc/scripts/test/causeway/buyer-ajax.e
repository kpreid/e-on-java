#!/usr/bin/env rune

pragma.syntax("0.9")

# Copyright 2008 Teleometry Design under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# This is a toy implementation of a purchase order system,
# written to demonstrate the Causeway Debugger.

def traceline(str) :void { stderr.println(`$str`) }
def report(message) :void { traceline(message) }

def args := interp.getArgs()
def productVow
if (args =~ [`--local`] + _) {
    traceline("local")
    bind productVow := <import:scripts.test.causeway.product-ajax>
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
			       "<import:scripts.test.causeway.product-ajax>")
}

def partNo := "123abc"
def name := "West Coast Buyers"
def profile := "West Coast Buyers Profile"

when (productVow) -> {
    def [=> inventory, => creditBureau, => shipper] := productVow

    # Turn on causality tracing.
    def tcr := <unsafe:org.erights.e.develop.trace.makeTraceController>
    tcr.setProperty("TraceLog_causality", "debug")
    
    def asyncAnd(var expected :int, var tellAreAllTrue) :any {
        
        # As each expected answer is collected, asyncAnd counts down
        # the total and reports true only if teller sees true for every
        # expected answer. A false answer short-circuits the logic. If
        # teller sees false, asyncAnd immediately reports false.
        
        def teller(answer :boolean) :void {
            if (answer) {
                expected -= 1
                if (expected == 0) {
                    if (tellAreAllTrue != null) {
                        tellAreAllTrue <- run(true)
                        tellAreAllTrue := null
                    }
                }
            } else {
                if (tellAreAllTrue != null) {
                    tellAreAllTrue <- run(false)
                    tellAreAllTrue := null
                }
            }
        }
        return teller
    }
    
    # All conditions must be met before an order is placed.
    def checkAnswers(allOK :boolean) :void {
        if (allOK) {
            inventory <- placeOrder(name, partNo, 
				    def done(placed :boolean) :void {
		if (placed) {
		    report(`Order placed for $name, $partNo`)
		} else {
		    report(`Order for $name, $partNo not placed`)
		}
		tcr.setProperty("TraceLog_causality", "warning")
		interp.exitAtTop()
            })
        }
    }
    
    # To collect the 3 answers, teller is passed as an argument to
    # each of the inquiries, serving as a callback function.
    
    def teller := asyncAnd(3, checkAnswers)
    
    # Asynchronous, eventual-sends to objects to check requirements.
    
    inventory <- isAvailable(partNo, teller)
    creditBureau <- doCreditCheck(name, teller)
    shipper <- canDeliver(profile, teller)   
}

interp.blockAtTop()
