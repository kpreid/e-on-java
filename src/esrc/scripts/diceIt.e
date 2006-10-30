#!/usr/bin/env rune

# Copyright 2002-2006 Dean Tribble under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ...

# This code is an example of using inheritance in E.
# It implements the myriad ways of rolling dice in
# role playing games.  Since this code is for
# exploratory and illustrative pusposes, the only
# requirements were:
#  - good flexibility for implementing and constrasting
#    different dice-rolling schemes
#  - ability to perform sampling and other operations
#    (e.g., histograms) across each of the schemes

# HISTORY
# I first attempted this with the 'class' special form;
# it was too painful and confusing because of the impedance
# mismatch between defining objects as instances, but using
# class construct to define makers. (e.g. 'class AbstractFooMaker'
# but 'def Foo').
#
# I tried composition: it didn't work because the aggregate
# operations could only be implemented as procedures.  Also,
# the composition operations (e.g., add two dice) would end
# up as procedures.  That may seem like a fine implementation
# path, but it is basically funtional, not object-oriented.
#
# I finally figured out the class construct and the delegate
# construct have essentially nothing to do with each other.
# I replaced all the class constructs with simple functions
# that take a "self" as the first argument.  This worked
# very well, except that the outer object needs a pointer to
# the inner object and vice-versa.  This requires something
# like:
#     def self
#     def super := abstractClass(self...)
#     def bind self {
#        to ...
#        delegate{super}
#     }
# The above would get very cumbersome over time.  This
# motivated the 'extends' construct, which combines the
# declaration of super with the delegate clause into the
# syntax that declares an object.  Thus, the above becomes:
#     def self extends abstractClass(self) {
#         to ...
#     }
#

# Basic design
# There are three type of dice objects:
# - simple dice
# - multidice that combine the rolls of several dice
# - intermediate groups of dice that are used by aggregaters
# the reason for the third class of dice is so that groups
# can be generated in several ways, while still allowing
# multidice to combine their results.
# simple dice further breaks down into leaf dice and modifier dice.

pragma.syntax("0.9")


## HELPER FUNCTIONS #############################################

def rand := <unsafe:java.util.Random>()

def makeResult(total, source) :any {
    def Result {
        to getTotal() :any { return total }
        to getSource() :any { return source }
        to printOn(oo) :any { oo.print(`$total <$source>`) }
    }
    return Result
}

# forward references to subclasses
def makeBonus
def dupDice
def maxDice
def keepDice
def makePair

# operations available to all kinds of dice
def abstractDice(self) :any {
    def innerDice {

        # adding an integer gives a bonus.  Adding dice, however, adds
        # the dice to the collection of dice being computed from.  This
        # makes creation of dice combinations much more convenient.
        to add(addend) :any {
            if (addend =~ x :int) {
                return makeBonus(self, addend)
            } else {
                return makePair(self, addend)
            }
        }

        # multiplying by a factor rolls the dice that many times.
        # It does not take the result of a single roll and multiply it.
        to multiply(factor) :any { return dupDice(self, factor) }
        to keep(count) :any      { return keepDice(count, self) }
        to max() :any            { return maxDice(self) }

        # Roll all the involved dice and add them into the supplied collection.
        # The default implementation is for the case in which the receiver is
        # a single die.
        to rollsInto(rolls) {
            rolls.push(self.roll())
        }
        to rolls() :any {
            return [self.roll()]
        }
        to printOn(oo) :any {
            oo.print("some dice")
        }

        # return a list of n rolls of the dice
        to sample(n) :any {
            def res := [].diverge()
            for i in 1..n {
                res.push(self.roll())
            }
            return res.snapshot()
        }

        # return a mapping from result value to number of times the result
        # was rolled in a sample of a given size
        to distribution(n) :any {
            def res := [].asMap().diverge()
            for i in 1..n {
                def roll := self.roll().getTotal()
                res.put(roll, res.fetch(roll, thunk{0}) + 1)
            }
            return res.sortKeys().snapshot()
        }
    }
    return innerDice
}

# roll a die from 1 to n
def simpleRoll(n) :any {
    return rand.nextInt(n) + 1
}
# roll a die from 1 to n, but if the number is n, roll again and add it
def explodingRoll(n) :any {
    def t := simpleRoll(n)
    return if (n == t) { n + explodingRoll(n) } else { t }
}

# implement an exploding die
def makeExploding(d) :any {
    def exploder extends abstractDice(exploder) {
        to roll() :any {
            def t := explodingRoll(d)
            return makeResult(t, t)
        }
        to printOn(oo) {
            oo.print("X")
            oo.print(d)
        }
    }
    return exploder
}

# implement a normal die
def makeDie(d) :any {
    def die extends abstractDice(die) {
        to roll() :any {
            def t := simpleRoll(d)
            return makeResult(t, t)
        }
        to printOn(oo) {
            oo.print("D")
            oo.print(d)
        }
        to explode() :any {
            return makeExploding(d)
        }
    }
    return die
}

# produce a die that adds a bonus to a dice
def bind makeBonus(dice, addend) :any {
    def bonus extends abstractDice(bonus) {
        to roll() :any {
            def inner := dice.roll()
            return makeResult(inner.getTotal() + addend,
                       `${inner.getSource()}+$addend`)
        }
        to printOn(oo) :any { oo.print(`$dice + $addend`) }
    }
    return bonus
}

def abstractDiceSet(self) :any {
    def dSet extends abstractDice(self) {
        to rolls() :any {
            def rolls := [].diverge()
            self.rollsInto(rolls)
            return rolls.snapshot()
        }
        to rollsInto(rolls) {
            throw("subclass responsibility")
        }
        to roll() :any {
            def rolls := self.rolls()
            var total := 0
            for die in rolls {
                total += die.getTotal()
            }
            return makeResult(total, rolls)
        }
    }
    return dSet
}

# define a set of dice that is the union of two other sets of dice (possibly singleton sets)
def bind makePair(diceA, diceB) :any {
    def dPair extends abstractDiceSet(dPair) {
        to rollsInto(rolls) {
            diceA.rollsInto(rolls)
            diceB.rollsInto(rolls)
        }
        to printOn(oo) {
            oo.print(`$diceA + $diceB`)
        }
    }
    return dPair
}

# define a set of identical dice (just roll the same die that many times)
def bind dupDice(die, factor) :any {
    def multiple extends abstractDiceSet(multiple) {
        to rollsInto(rolls) {
            for i in 1..factor {
                die.rollsInto(rolls)
            }
        }
        to printOn(oo) {
            oo.print(`$factor$die`)
        }
    }
    return multiple
}

def bind maxDice(toRoll) :any {
    def maxer extends abstractDice(maxer) {
        to roll() :any {
            def rolls := toRoll.rolls()
            var res := 0
            for die in rolls {
                res := res.max(die.getTotal())
            }
            return makeResult(res, rolls)
        }
        to printOn(oo) {
            oo.print(`max($toRoll)`)
        }
    }
    return maxer
}

def descending(x, y) :any { return y.getTotal().compareTo(x.getTotal()) }

def bind keepDice(keep, toRoll) :any {
    def keeper extends abstractDice(keeper) {
        to roll() :any {
            def rolls := toRoll.rolls()
            def kept := keep.min(rolls.size())
            def sorted := rolls.sort(descending)
            def run := sorted(0, kept)
            var res := 0
            for roll in run {
                res += roll.getTotal()
            }
            return makeResult(res, sorted)
        }
        to printOn(oo) {
            oo.print(`keep($keep, $toRoll)`)
        }
    }
    return keeper
}


def D8 := makeDie(8)
def D6 := makeDie(6)
def D4 := makeDie(4)

def d3D6 := D6 * 3

# dice for Seventh Sea
def x10 := makeExploding(10)
def x4k3 := (x10 * 4).keep(3)

# dice for deadlands
def x8 := D8.explode()
def m4x8 := (x8 * 4).max()

def all := d3D6 + x4k3 + m4x8 + 4

# d3D6.sample(20)
# x4k3.distribution(20)
# all.distribution(20)

x10.keep(0).sample(20)

println(all.distribution(1000))
