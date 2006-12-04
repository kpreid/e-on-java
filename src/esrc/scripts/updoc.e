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
def SyntaxException := <type:org.quasiliteral.syntax.SyntaxException>

def <tools> := <import:org.erights.e.tools.*>
def makeHashCache := <tools:updoc.makeHashCache>
def oneAtATimeVow := <tools:collect.oneAtATimeVow>
def html2updoc := <tools:html.html2updoc>
def makeOldUpdocParser := <tools:updoc.makeOldUpdocParser>
def makeScriptPlayer := <tools:updoc.makeScriptPlayer>

def optCacheFile :=
  if (interp.getProps().fetch("e.home", fn{}) =~ eHomeName :notNull) {
    <file>[eHomeName]["updoc-hash-cache.txt"]
} else {
    null
}
def hashCache := makeHashCache(optCacheFile)


def withoutSuffix := <tools:text.withoutSuffix>

/**
 * <tt>printBlock</tt>
 *
 * @param keyword
 * @param str
 * @param out
 */
def printBlock(keyword, str, out) :void {
    # "+ 2" for the colon and space
    def prefix := " " * (keyword.size() + 2)
    def shortStr := withoutSuffix(str, "\n")
    out.indent(prefix).print(`$keyword: $shortStr`)
    # blank line for removed newline
    out.println()
}

/**
 * <tt>parseAndPlay</tt> parses the <tt>Updoc</tt> source then runs
 * the script and reports the results.
 *
 * @param source The text or twine of the <tt>Updoc</tt> source file
 * @param hash The crypto hash of the <tt>Updoc</tt> source file
 * @param evalServerPool A ref to an <tt>evalServerPool</tt> object
 * @param out An output object for reporting results or errors
 * @return A <tt>vow</tt> that becomes <tt>null</tt> on success or
 *         becomes broken with a problem
 */
def parseAndPlay(source :Twine, hash :int, evalServerPool :rcvr, out) :vow {
    try {
        def oldUpdocParser := makeOldUpdocParser(source)
        def script := oldUpdocParser.readScript()
        if (script.isEmpty()) {
            hashCache.put(hash)
            null
        } else {
            def player := makeScriptPlayer(script)
            player.replay(evalServerPool, [], interp.getProps(), out)
        }
    } catch problem {
        if (problem.leaf() =~ sex :SyntaxException) {
            printBlock("***script stopped by", `$sex`,
                       out)
        } else {
            printBlock("***script stopped by", `$problem
${problem.eStack()}

${problem.javaStack()}`,
                       out)
        }
        Ref.broken(problem)
    }
}

def endsWithAny(name, suffixList) :boolean {
    for suffix in suffixList {
        if (name.endsWith(suffix)) {
            return true
        }
    }
    false
}

/**
 * <tt>updocOne</tt> runs <tt>Updoc</tt> on the source of a single
 * <tt>Updoc</tt> file or on the results of an <tt>html2updoc</tt> conversion.
 * <p>
 * <tt>Updoc</tt> files must end with <tt>.updoc</tt>, <tt>.e</tt>,
 * <tt>.e-awt</tt>, <tt>.e-swt</tt>,
 * <tt>.emaker</tt>, <tt>.caplet</tt>, or <tt>.txt</tt>. <tt>HTML</tt> files
 * must end with <tt>.html</tt> or <tt>.htm</tt>. All other files are ignored.
 *
 * @param file A <tt>file</tt> object
 * @param path The path of the file
 * @param evalServerPool A ref to an <tt>evalServerPool</tt> object
 * @param out An output object for reporting results or errors
 * @return A <tt>vow</tt> that becomes <tt>null</tt> on success or
 *         becomes broken with a problem
 */
def updocOne(file, path, evalServerPool, out) :vow {
    def hash := file.getCryptoHash()
    if (hashCache.has(hash)) {
        if (__makeMap.testProp(interp.getProps(), "updoc.verbose")) {
            out.lnPrint(`skipping $path`)
        }
        null
    } else if (endsWithAny(path, [".updoc",
                                  ".e", ".e-awt", ".e-swt",
                                  ".emaker",
                                  ".caplet",
                                  ".txt"])) {
        out.lnPrint(`$path:`)
        # XXX Once E is faster, and the simple__quasiParser is fixed
        # to pass source info through (preserving twine-ness), then
        # switch from getText() to getTwine()
        def source := file.getTwine()
        parseAndPlay(source, hash, evalServerPool, out)
    } else if (endsWithAny(path, [".html", ".htm"])) {
        out.lnPrint(`$path:`)
        def html := file.getTwine()
        def source := try {
            html2updoc(html)
        } catch problem {
            out.lnPrint(`can't parse $path: `)
            out.indent("#                  ").print(problem)
            return null
        }
        parseAndPlay(source, hash, evalServerPool, out)
    } else {
        if (__makeMap.testProp(interp.getProps(), "updoc.verbose")) {
            out.lnPrint(`ignoring $path`)
        }
        null
    }
}

/**
 * <tt>updoc</tt> runs <tt>Updoc</tt> on a <tt>url</tt>, <tt>file</tt>, or
 * <tt>directory</tt>.
 *
 * @param filedir A <tt>url</tt> or <tt>file</tt> or <tt>directory</tt> object
 * @param evalServerPool A ref to an <tt>evalServerPool</tt> object
 * @return A <tt>vow</tt> that becomes <tt>null</tt> on success or
 *         becomes broken with a problem
 * @author Terry Stanley
 * @author Mark S. Miller
 */
def updoc(filedir, evalServerPool :rcvr) :vow {
    if (filedir =~ url :URL) {
        updocOne(url,
                 url.toExternalForm(),
                 evalServerPool,
                 stdout)
    } else if (filedir.isDirectory()) {
        def resultVow := oneAtATimeVow(filedir.list(), def _(_, name) :any {
            updoc(filedir[name], evalServerPool)
        })
        Ref.whenResolved(resultVow, def _(_) :void {
            hashCache.checkpoint()
        })
        resultVow
    } else {
        updocOne(filedir,
                 filedir.getPath(),
                 evalServerPool,
                 stdout)
    }
}

#introducer onTheAir()

#def makeCapImporter := <import:org.erights.e.tools.args.makeCapImporter>
def simpleEvalServerPoolAuthor := <tools:updoc.simpleEvalServerPoolAuthor>


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

when (updoc(filedir, evalServerPool)) -> done(_) :void {
    stdout.println()
    interp.exitAtTop()
} catch problem {
    stdout.println()
    interp.exitAtTop(problem)
} finally {
    hashCache.checkpoint()
}

interp.blockAtTop()
