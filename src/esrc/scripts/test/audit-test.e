#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# @author Ka-Ping Yee

# def parse := <elang:syntax.makeEParser>
# def node := parse(
# "def makeAdder(inc :any) :any {
#      def i := inc + inc
#      def adder {
#          to add(x) :any {
#              x + i
#          }
#      }
#  }")
# def m := node.rValue().eScript().optMethods()[0]

var s := null
def Auditor {
    to audit(audition) :boolean {
        s := audition.getSource()
        return true
    }
}
def a := 1
def b := 2
def pair implements Auditor {
    to getA() :void { a }
    to getB() :any { b }
}

# def DeepFrozen0 implements Functional {
def DeepFrozen0 {
    to audit(audition) :boolean {
        def objExpr := audition.getSource()
        println("auditing ", objExpr)
        def frozenGuardNames := ["DeepFrozen0", "void", "boolean",
                                 "int", "float64", "char", "String"]
        for name => pattern in objExpr.getSynEnv() {
            if (pattern == null) {
                continue
            }
            def ptype := pattern.__getAllegedType()
            if (E.toString(ptype) != "FinalPattern") {
                throw(`failed to audit $objExpr`)
            }
            def guard := pattern.getValueGuard()
            def gtype := guard.__getAllegedType()
            if (E.toString(gtype) != "SimpleNounExpr") {
                throw(`failed to audit $objExpr`)
            }
            if (! (frozenGuardNames.contains(guard.name()))) {
                throw(`failed to audit $objExpr`)
            }
        }
        return true
    }
    to coerce(specimen, optEjector) :any {
        if (specimen == null) { null
        } else if (specimen =~ b :boolean) { b
        } else if (specimen =~ i :int) { i
        } else if (specimen =~ f :float64) { f
        } else if (specimen =~ c :char) { c
        } else if (specimen =~ s :String) { s
        } else if (__auditedBy(DeepFrozen0, specimen)) { specimen
        } else { throw.eject(optEjector, specimen) }
    }
}

var x := true
def y := true
def z :boolean := true

def bad implements DeepFrozen0 {
    to getX() :any { x }
}

def bad2 implements DeepFrozen0 {
    to getY() :any { y }
}

def good implements DeepFrozen0 {
    to getZ() :any { z }
}


# + why do the leaves not get their synEnvs passed along?
# + why does importing of int fail with break already defined?
# + fix binding problem after failed audit
# * fix non-working audit checker
# * enable the DeepFrozen0
# * optimize for auditors okay with per-vtable checking
#
# value toString() printout fooled me: i saw FinalPattern, thought
# i got a string -- actually it was a ClassDesc object; == failed
#
# inconsistency! a SimpleNounExpr prints as "SimpleNoun" but
# returns "SimpleNounExpr" as its name
#
# why not just make return work everywhere?
#
# getName() some places, name() in others



? def passer { to audit(_) :boolean { true } }
# value: <passer>

? def okay implements passer { }
# value: <okay>

? def failer { to audit(audition) :boolean {
>     throw(`failed to audit ${audition.getSource()}`) }
> }
# value: <failer>

? def bad implements failer { }
# problem: <AuditFailedException: object expression \
#            <main>.bad__C failed audit by <failer>>

?
