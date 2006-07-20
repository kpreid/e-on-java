#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

introducer.onTheAir()

def traceline(str) :void { stderr.print(`$str`) }
def report(message) :void { traceline(message) }

def asyncAnd := <import:org.erights.e.examples.concurrency.asyncAnd>

def uri := <file:~/Desktop/seller.cap>.getText().trim()
def sr := introducer.sturdyFromURI(uri)
def seller := sr.getRcvr()

def partNo := "123abc"
def name := "West Coast Buyers"
(
    def tcr := <unsafe:org.erights.e.develop.trace.TraceController>
    tcr.setProperty("TraceLog_causality", "debug")
    
    def promises := [seller <- checkAvailability(partNo),
                     seller <- verifyCredit(name),
                     seller <- confirmDeliveryOptions(name)]
    
    def allCanDo := asyncAnd(promises)
    
    when (allCanDo) -> anddone(_) :any {
        
        if (allCanDo == true) {
            when (seller <- placeOrder(name, partNo)) -> orderdone(_) :any {
                report(`Order placed for $name, $partNo`)
            } catch problem {
                report(problem)
            }
        }
        
    } catch problem {
        report(problem)
    } finally {
#        tcr.setProperty("TraceLog_causality", "warning")
        interp.exitAtTop()
    }
    
    interp.blockAtTop()
)
