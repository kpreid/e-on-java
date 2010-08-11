#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def traceline(str) :void { stderr.println(`$\nupdoc: $str`) }
# def traceline(str) {}
traceline("started")

def makeURL := <unsafe:java.net.makeURL>
def URL := <type:java.net.URL>
def MalformedURLException := <type:java.net.MalformedURLException>
def <tools> := <import:org.erights.e.tools.*>
def makeHashCache := <tools:updoc.makeHashCache>

#introducer onTheAir()

#def makeCapImporter := <import:org.erights.e.tools.args.makeCapImporter>
def simpleEvalServerPoolAuthor := <tools:updoc.simpleEvalServerPoolAuthor>

def optCacheFile :=
  if (interp.getProps().fetch("e.home", fn{}) =~ eHomeName :notNull) {
    <file>[eHomeName]["updoc-hash-cache.txt"]
} else {
    null
}
def hashCache := makeHashCache(optCacheFile)

def makeTraceln := <unsafe:org.erights.e.elib.debug.makeTraceln>
def ELoaderAuthor := <elang:interp.ELoaderAuthor>(makeTraceln)
def updoc := <tools:updoc.makeUpdocAuthor>(hashCache, interp.getProps(), ELoaderAuthor, stdout)

#def uriGetters := [
#    "file" => <file>,
#    "http" => <http>,
#    "ftp"  => <ftp>]

#def importCap := makeCapImporter(introducer, <file>, uriGetters)


#def interimArgParser := <import:org.erights.e.tools.args.interimArgParser>

#def optionsDesc := [
#    "getPool" => term`option("getPool", "capSource", one,
#                             "Access to the evalServerPool client facet")`
#]

#def [optionsMap, args] := interimArgParser(optionsDesc, interp getArgs())
def args := interp.getArgs()

def names := if (args == []) {
#    interp.exitAtTop("usage: updoc.e (--getPool <capSource>)? <files>+")
    interp.exitAtTop("usage: updoc.e <file>")
} else if (args.size() == 1) {
    args
} else {
    interp.exitAtTop("XXX multiple args not yet implemented")
}
def name := names[0] # XXX for now


#traceline("args parsed")

#def poolSturdy := importCap(optionsMap["getPool"][0])

#def evalServerPool := poolSturdy getRcvr()

def evalServerPool := simpleEvalServerPoolAuthor(<unsafe>)

def filedir := if (name =~ `@_:@_`) {
    try {
        makeURL(name)
    } catch problem ? (problem.leaf() =~ mux :MalformedURLException) {
        if (name =~ `resource:@body`) {
            # XXX Perhaps we should instead sugar java.net.URL to understand
            #     "resource:"?
            <resource>[body]
        } else {
            # return a file object
            <file>[name]
        }
    }
} else {
    <file>[name]
}

#traceline("args connected")

def envExtras := if (filedir =~ _ :URL) {
    [].asMap()
} else {
    def thisLoader := ELoaderAuthor(<file>[filedir.getParent()], [ "this__uriGetter" => thisLoader ], "updoc$")
    thisLoader.getEnvExtras()
}

when (updoc(filedir, evalServerPool, envExtras)) -> done(failures) :void {
    stdout.println()
    if (failures > 0) {
        interp.exitAtTop(`Failed tests: $failures`)
    } else {
        stdout.println("All tests passed.")
        interp.exitAtTop()
    }
} catch problem {
    stdout.println()
    interp.exitAtTop(problem)
} finally {
    hashCache.checkpoint()
}

interp.blockAtTop()
