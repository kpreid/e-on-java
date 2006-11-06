#!/usr/bin/env rune

# Copyright 2006, Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")

def makeFlexList := <elib:tables.makeFlexList>

def nsieve(m, isPrime) {
    for i in 2..m { isPrime[i] := true }
    var count := 0
    
    for i in 2..m {
        if (isPrime[i]) {
            var k := i+i
            while (k <= m) {
                isPrime[k] := false
                k += i
            }
            count += 1
        }
    }
    return count
}

var n := 2
if (interp.getArgs() =~ [nArg]) {
    n := __makeInt(nArg)
}
if (n < 2) { n := 2 }

def MAG := 10_000

var m := (1<<n)*MAG
# def flags := makeFlexList.fromType(boolean, m+1)
# flags.setSize(m+1)
def flags := ([false]*(m+1)).diverge()

def start := timer.now()

println(`Primes up to $m ${nsieve(m, flags)}`)
m := (1<<n-1)*MAG
println(`Primes up to $m ${nsieve(m, flags)}`)
m := (1<<n-2)*MAG
println(`Primes up to $m ${nsieve(m, flags)}`)
def delta := timer.now() - start
println(`time: $delta`)
