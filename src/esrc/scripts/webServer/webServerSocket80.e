#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def argParser := <import:org.erights.e.tools.args.argParser>

/**
 * set up tracing; stub out all the printing for operational version
 */
def traceline(str) :void { stderr.println(str) }
traceline("started")

/**
 * return the object represented by the URI
 */
def getObjectFromURI(uri) :any {
    introducer.sturdyFromURI(uri).getRcvr()
}
def makeURIFromObject(obj) :any {
    introducer.sturdyToURI(makeSturdyRef(obj))
}



def forwarderMaker(target) :near {
    def forwarder  {
        match [verb,args] {E.call(target,verb,args)}
    }
}

############## Web Server Specific Code ################

var portNum := 80

def [props, options, fname, args] := argParser(["--help", "--version"].asSet(),
                                               interp.getArgs())
switch (fname) {
    match `--help` {
        println(`
Usage: webServerSocket80.e [--port=<portNum>]
`)
        interp.exitAtTop()
    }
    match `--version` {
        println("webServerSocket80.e")
        rune(["--version"])
        interp.exitAtTop()
    }
    match _ {
        for k => v in options {
            switch (k) {
                match `--port` {
                    portNum := __makeInt(v)
                }
            }
        }
    }
}


def header1 := "HTTP/1.0 200 OK
Content-Type: "
def htmlMime := "text/html"
def contentLengthTag := "Content-Length: "
def crlf := "\r\n"

def composeHeader(mimeType,length) :pbc {
    traceline("composing header")
    var header := header1 + mimeType
    traceline("made first header part: " + header)
    if (length != null) {
        header += `$crlf$contentLengthTag$length`
    }
    header := header + crlf + crlf
    traceline("header: " + header)
    header
}

def serverSocket := <unsafe:java.net.makeServerSocket>(portNum)

def terminateServer(socket,serverSocket) :void {
    traceline("serviced request")
    socket.close()
    serverSocket.close()
    interp.continueAtTop()
}

/**
 *
 * @author Marc Stiegler
 */
def webServer() :void {
    traceline("ready to accept requests")
    def socket := serverSocket.accept()
    def outstream := socket.getOutputStream()
    var instream := socket.getInputStream()
    instream := <unsafe:java.io.makeInputStreamReader>(instream)
    instream := <unsafe:java.io.makeBufferedReader>(instream)
    def action := instream.readLine()
    println("action: " + action)
    #while (instream ready) {
    #    println("discard: " + instream readLine)
    #}
    println("finished discards")
    var filePath := ""
    if (action =~ `GET /@{fileName} HTTP/1.@{trailer}`) {
        filePath := fileName
    }
    if (filePath == "") {filePath := "index.html"}
    traceline("file to serve: " + filePath)
    if (filePath =~ `@{mainName}${".caplet"}`) {
        def theDoc := <file>[filePath].getText()
        traceline(`got the caplet text length: ${theDoc.size()}`)
        outstream.write(composeHeader("text/plain", theDoc.size()).getBytes())
        outstream.write(theDoc.getBytes())
        #terminateServer(socket,serverSocket)
        socket.close()
        webServer()
    } else if (filePath =~ `@{toSuffix}${".cap"}@{query}`) {
        traceline("distributing web service")
        def forwardStream := forwarderMaker(outstream)
        traceline("made forwarder")
        def service := getObjectFromURI(<file>[toSuffix + ".cap"].getText())
        when (service <- replyToQuery(query,forwardStream)) ->
          done(result) :void {

            #outstream write(resultBytes)
            #terminateServer(socket,serverSocket)
            socket.close()
            webServer()
        } catch e {traceline("capability broken: " + e)}
    } else {
        println("into get file to return")
        try {
            def theDoc := <file>[filePath].getText()
            def header := composeHeader(htmlMime, theDoc.size())
            println("header: " + header)
            outstream.write(header.getBytes())
            println("sent header")
            outstream.write(theDoc.getBytes())
            println("sent text")
        } catch prob {traceline(`no text: $prob`)}
        #terminateServer(socket,serverSocket)
        socket.close()
        webServer()
    }
}


introducer.onTheAir()
traceline("introducer on the air")
webServer()
interp.blockAtTop()
