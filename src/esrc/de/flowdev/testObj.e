#!/usr/bin/env rune

pragma.syntax("0.9")

# From http://www.eros-os.org/pipermail/e-lang/2007-January/011762.html
# by Martin Scheffler
# XXX Need copyright notice

# ? rune(["~/e/src/esrc/de/flowdev/testObj.e"])

def seedVat := <elang:interp.seedVatAuthor>(<unsafe>)
def vsv := seedVat.virtualize((introducer.onTheAir();
                                introducer))

def othersrc:="
    def tester{
        to testPrint() :void { println(\"Tester is alive\") }
        to print(other) :void {
            println(`Tester got object: ${other.__getAllegedType()}`)
        }
    }
    tester
"
def [tester, vat] := vsv(othersrc)


def obj1 implements pbc {
    to __optUncall() :any {
        println("UNCALLING Obj 1")
        return [<elib:tables.makeFlexList>, "make", []]
    }
}
def obj2 := <import:de.flowdev.makeTestObj>()

interp.waitAtTop(tester <- testPrint())
interp.waitAtTop(tester <- print(obj1))
interp.waitAtTop(tester <- print(obj2))
