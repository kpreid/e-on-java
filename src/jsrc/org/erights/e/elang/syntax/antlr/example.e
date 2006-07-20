#!/usr/bin/env rune

def t := `Canonical Path: $canonical$\nSize: $fileSize$\nLast Mod: $modDate`
makeDialogVow("Properties For " + name, textTree, null, ["OK"])

def `<$root/@{var path}>` := (""+wdir)
` @{
    oldValue} `
  0..5
x=~[`-`]
switch (interp.getArgs()) {
    match [`-`] { println(uri) }
    match [] { println(uri) }
    match [fname] { <file: fname>.setText(uri) }
}

`$zz`
`identest$id`
`}$${}`
`etest${345}a`
`$xxtest${345}a`
`hello`
```aa``4{3}``b`````
e`go`
#strings
""
"string
"
"
ending"
[a => b]
[x
=> y]
def [e => x,]
def [f
=> g] := f

for x => z in y {foo()}
for x : int in y {foo()}
[ 3
    => 4]
    def List1  {

      to coerce(var specimen, optEjector) {

        return accum [] for subSpecimen in specimen {
          _.with(elementGuard.coerce(subSpecimen, optEjector))
        }
      }

      to __printOn(out :TextWriter) {
        out.print("List[")
        out.quote(elementGuard)
        out.print("]")
      }
    }
