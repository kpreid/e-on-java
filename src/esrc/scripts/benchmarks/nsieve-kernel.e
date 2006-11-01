#!/usr/bin/env rune

# Copyright 2006 Hewlett Packard, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# This is nsieve.e as translated to Kernel-E

pragma.syntax("0.9")

def makeFlexList := elib__uriGetter.get("tables.makeFlexList")
def FlexList := type__uriGetter.get("org.erights.e.elib.tables.FlexList")
def nsieve {
    
    method run(m :int, isPrime :FlexList) :int {
        escape __return {
            escape __break {
                def var validFlag__3 := true
                try {
                    __makeOrderedSpace.op__thru(2, m).iterate(
                        /** For-loop body */
                        def _ {
                            
                            method run(key__5, value__7) {
                                require.run(validFlag__3,
                                            "For-loop body isn't valid after for-loop exits.")
                                escape ej__9 {
                                    def _ exit ej__9 := key__5
                                    def i exit ej__9 := value__7
                                    escape __continue {
                                        isPrime.put(i, def ares__1 := true)
                                        ares__1
                                        null
                                    }
                                } catch _ {
                                    null
                                }
                            }
                        })
                } finally {
                    validFlag__3 := false
                }
                null
            }
            def var count :int := 0
            escape __break {
                def var validFlag__13 := true
                try {
                    __makeOrderedSpace.op__thru(2, m).iterate(
                        /** For-loop body */
                        def _ {
                            
                            method run(key__15, value__17) {
                                require.run(validFlag__13,
                                            "For-loop body isn't valid after for-loop exits.")
                                escape ej__19 {
                                    def _ exit ej__19 := key__15
                                    def i exit ej__19 := value__17
                                    escape __continue {
                                        if (isPrime.get(i)) {
                                            def var k :int := i.add(i)
                                            escape __break {
                                                __loop.run(
                                                    /** While loop body */
                                                    def _ {
                                                        
                                                        method run() :boolean {
                                                            if (__comparer.leq(k, m)) {
                                                                escape __continue {
                                                                    isPrime.put(k, def ares__11 := false)
                                                                    ares__11
                                                                    k := k.add(i)
                                                                }
                                                                true
                                                            } else {
                                                                false
                                                            }
                                                        }
                                                    })
                                            }
                                            count := count.add(1)
                                        } else {
                                            null
                                        }
                                        null
                                    }
                                } catch _ {
                                    null
                                }
                            }
                        })
                } finally {
                    validFlag__13 := false
                }
                null
            }
            __return.run(count)
        }
    }
}
def var n :int := 2
escape ej__21 {
    def [nArg] exit ej__21 := interp.getArgs()
    n := __makeInt.run(nArg)
} catch _ {
    null
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
