#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2004 Mark S. Miller & Sandeep Ranade under the terms of the 
# MIT X license found at http://www.opensource.org/licenses/mit-license.html ..

introducer.onTheAir()

# Reads the uri from a file to obtain an initial reference to the pingServer.
def uri := <file:~/Desktop/ping-server.cap>.getText()
def sr := introducer.sturdyFromURI(uri)
def pingServer := sr.getRcvr()

# A forward reference to the pongClient, so that the pinger and the
# pongClient can each be initialized to point at each other.
def pongClient

(
    def tcr := <unsafe:org.erights.e.develop.trace.TraceController>
    tcr.setProperty("TraceLog_causality", "debug")
    
    def pinger := pingServer <- handshake(pongClient)
    
    var count := 0
    
    bind pongClient {
        to pong() :void {
            println(`pong: $count`)
            count += 1
            if (count <= 10) {
                pinger <- ping(count)
            } else {
                tcr.setProperty("TraceLog_causality", "warning")
                interp.exitAtTop()
            }
        }
    }
    
    pinger <- ping(count)
    
    interp.blockAtTop()
)
