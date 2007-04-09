#!/usr/bin/env rune

pragma.syntax("0.9")

def makeURIFromObject(obj) :String {
    def sr := makeSturdyRef.temp(obj)

    def bracketed := introducer.sturdyToURI(sr)
    if (bracketed =~ `<@uri>`) { return uri }
    return bracketed
}

def pbcthing := <import:de.flowdev.emonkey.makeTestEPBC>()

def connector {
    to connect() :any { return pbcthing }
}

introducer.onTheAir()

def uri := makeURIFromObject(connector)
<file:mycap.txt>.setText(uri)
println(`server.e: listening on $uri`)
interp.blockAtTop()
