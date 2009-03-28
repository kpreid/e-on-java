#!/usr/bin/env rune

# XXX Need copyright notice

pragma.syntax("0.8")

def makeToggle(self, var state) :any {
    def toggle {
        to value() :any    { state }
        to activate() :any { state := !state; self }
    }
}

def makeNthToggle(state, maxCount) :any {
    var counter := 0
    def nthToggle extends makeToggle(nthToggle, state) {
        to activate() :any {
            counter += 1
            if (counter >= maxCount) {
                super.activate()
                counter := 0
            }
            nthToggle
        }
    }
}

def n := __makeInt(interp.getArgs()[0])

def toggle := makeToggle(toggle, true)
for i in 0..!n { toggle.activate().value() }
println(toggle.value())

def ntoggle := makeNthToggle(true, 3)
for i in 0..!n { ntoggle.activate().value() }
println(ntoggle.value())
