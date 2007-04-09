#!/usr/bin/env rune

pragma.syntax("0.9")

def getObjectFromURI(uri) :any {
    return introducer.sturdyFromURI(uri).getRcvr()
}

introducer.onTheAir()
def uri := <file:mycap.txt>.getText()
println("Client.e: Connecting to "+uri)
def connectorRcvr := getObjectFromURI(uri)

when(connectorRcvr) -> {
    def pbcthingy := connectorRcvr <- connect()
    when (pbcthingy) -> {
        println(`Client.e: Connected to $pbcthingy`)
    } catch err {
        println(`Client.e: Could not connect to server: $err`)
    }
}
interp.blockAtTop()
