#!/usr/bin/env rune

# Copyright 2006 Hewlett Packard, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# This is nsieve-kernel.e after hand simulation of source-to-source optimations
# believed to be practically automatable.

pragma.syntax("0.9")

def makeFlexList := elib__uriGetter.get("tables.makeFlexList")
def FlexList := type__uriGetter.get("org.erights.e.elib.tables.FlexList")
def nsieve {
    method run(m :int, isPrime :FlexList) :int {
        def var validFlag__3 := true
        try {
            __makeOrderedSpace.op__thru(2, m).iterate(
                /** For-loop body */
                def _ {
                    method run(_, value__7) {
                        require.run(validFlag__3,
                                    "For-loop body isn't valid after for-loop exits.")
                        def i := value__7
                        isPrime.put(value__7, true)
                        null
                    }
                })
        } finally {
            validFlag__3 := false
        }
        def var count :int := 0
        def var validFlag__13 := true
        try {
            __makeOrderedSpace.op__thru(2, m).iterate(
                /** For-loop body */
                def _ {
                    method run(key__15, value__17) {
                        require.run(validFlag__13,
                                    "For-loop body isn't valid after for-loop exits.")
                        if (isPrime.get(value__17)) {
                            def var k :int := value__17.add(value__17)
                            __loop.run(
                                /** While loop body */
                                def _ {
                                    method run() :boolean {
                                        if (__comparer.leq(k, m)) {
                                            isPrime.put(k, false)
                                            k := k.add(value__17)
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                })
                            count := count.add(1)
                        } else {
                        }
                        null
                    }
                })
        } finally {
            validFlag__13 := false
        }
        count
    }
}

def var n :int := 2
escape ej__21 {
    def [nArg] exit ej__21 := interp.getArgs()
    n := __makeInt.run(nArg)
} catch _ {
}
if (__comparer.lessThan(n, 2)) {
    n := 2
} else {
    null
}
def MAG := 10000
def var m :int := 1.shiftLeft(n).multiply(MAG)
def flags := __makeList.run(false).multiply(m.add(1)).diverge()
println.run(simple__quasiParser.valueMaker(
    "Primes up to ${0} ${1}").substitute(__makeList.run(m, nsieve.run(m, flags))))
m := 1.shiftLeft(n.subtract(1)).multiply(MAG)
println.run(simple__quasiParser.valueMaker(
    "Primes up to ${0} ${1}").substitute(__makeList.run(m, nsieve.run(m, flags))))
m := 1.shiftLeft(n.subtract(2)).multiply(MAG)
println.run(simple__quasiParser.valueMaker(
    "Primes up to ${0} ${1}").substitute(__makeList.run(m, nsieve.run(m, flags))))
