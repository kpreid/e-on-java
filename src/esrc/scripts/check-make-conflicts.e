#!/usr/bin/env rune

pragma.disable("explicit-result-guard")
pragma.enable("easy-return")
pragma.enable("easy-when")
pragma.enable("anon-lambda")
pragma.enable("lambda-args")

def table extends [].asMap().diverge() {
    to get(index) {
        return super.fetch(index, fn{[]})
    }
}

def addAll(filedir, prefix) {
    for fName => sub in filedir {
        if (sub.isDirectory()) {
            addAll(sub, `$prefix.$fName`)
        } else if (fName =~ `@name.java`) {
            table[name] with= prefix
        } else if (fName =~ `@{var name}.emaker`) {
            if (name =~ `make@rest`) {
                name := rest
            }
            table[name] with= prefix
        }
    }
}

addAll(<file:~/e/src/jsrc>, "")
addAll(<file:~/e/src/esrc>, "")

for name => prefixes in table {
    if (prefixes.size() >= 2) {
        if (prefixes.size() > prefixes.asSet().size()) {
            stderr.print("*")
        }
        stderr.println(`conflicts: $name`)
        for prefix in prefixes {
            stderr.println(`$\t$prefix`)
        }
        stderr.println()
    }
}

interp.blockAtTop()
