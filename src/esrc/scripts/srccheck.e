#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def makeELexer := <elang:syntax.makeELexer>
def makeEParser := <elang:syntax.makeEParser>
def Term := <type:org.quasiliteral.term.Term>
def <tools> := <import:org.erights.e.tools.*>

def optCacheFile :=
  if (interp.getProps().fetch("e.home", thunk{}) =~ eHomeName :notNull) {
    <file>[eHomeName]["updoc-hash-cache.txt"]
} else {
    null
}
def makeUpdocParser := <tools:updoc.makeUpdocParserAuthor>(optCacheFile)


def checkUpdoc(script :Term) :void {

    # XXX Should refactor: the following is copy-pasted from
    #     cmdMakerMaker.emaker
    # XXX Rather than one per updoc script, we really need one per vat-name
    #     within an updoc script.
    def lexer := makeELexer(null,           # optLineFeeder
                            false,          # partialFlag
                            true)           # noTabsFlag
    def parser := makeEParser.make(null,    # optProps
                                   lexer,
                                   stderr,  # warning
                                   false,   # debugFlag
                                   false)   # onlyOneExprFlag

    def term`script(@_,[inVat(@_,[test(@exprss,@_)*])*])` := script
    # exprss is a list of lists of terms of twine
    for exprs in exprss {
        for expr in exprs {
            def exprSrc :Twine := expr.getOptData()
            parser.setSource(exprSrc)
            parser.parse()
        }
    }
}

def checkWidth(src) :void {
    if (src.startOf("\t") =~ pos :(int >= 0)) {
        stderr.print(`# tab at $pos`)
        if (src(pos,pos+1).getOptSpan() =~ span :notNull) {
            stderr.print(` from: $span`)
        }
        stderr.println()
        stderr.println()
    }
    return
# XXX Make the following switchable
    for line in src.split("\n") {
        def len := line.size()
        if (len > 79) {
            stderr.print(`# warning: $len column line`)
            if (line(79,len).getOptSpan() =~ span :notNull) {
                stderr.print(` from: $span`)
            } else {
                throw.breakpoint([len, line])
            }
            stderr.println()
            stderr.println()
            return #only one width warning per compilation unit.
        }
    }
}


def stdheader := "#!/usr/bin/env rune\n"

def stdexts := ["e", "e-awt", "e-swt"].asSet()

/**
 * @param filedir A File that represents either a file to be
 *                checked, or the root of a directory tree of
 *                such files
 * @param relpath The path of the filedir relative to the
 *                importation root. "." for the root
 *                directory itself. Must begin with "."
 * @returns Whether all the files rooted here were fine
 */
def srcCheckRecurse(filedir,
                    relpath :String,
                    optImportRoot :nullOk[String]) :boolean {
    def fullpath := filedir.getPath()
    def good() :boolean {
        stderr.print(".")
        return true
    }
    def bad(msg) :boolean {
        stderr.println("\n" + fullpath)
        stderr.println(msg)
        return false
    }
    if (filedir.isDirectory()) {
        var result := true
        stderr.print("[")
        for name => sub in filedir {
            result &= srcCheckRecurse(sub, `$relpath/$name`, optImportRoot)
        }
        makeUpdocParser.checkpoint()
        stderr.print("]")
        return result
    } else if (relpath =~ `./@_.emaker`) {
        def src := filedir.getTwine()
        try {
            if (null != optImportRoot &&
                  fullpath =~ `$optImportRoot@restpath.emaker`) {

                def fqname := restpath.replaceAll("/", ".")
                <import>[fqname]
            }
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkWidth(src)
            return good()
        } catch problem {
            return bad(problem)
        }
    } else if (relpath =~ `./@_.caplet`) {
        def src := filedir.getTwine()
        try {
            e__quasiParser(src)
            checkUpdoc(makeUpdocParser.parsePlain(src))
            # checkWidth(src)
            return good()
        } catch problem {
            return bad(problem)
        }
    } else if (relpath =~ `./@_.@ext` && stdexts.contains(ext)) {
        def src := filedir.getTwine()
        if (! (src.startsWith(stdheader))) {
            stderr.println(fullpath)
            def header := stdheader.replaceAll("\n","")
            return bad(`First line should be "$header"`)
        } else {
            try {
                e__quasiParser(src)
                checkUpdoc(makeUpdocParser.parsePlain(src))
                checkWidth(src)
                return good()
            } catch problem {
                return bad(problem)
            }
        }
    } else if (relpath =~ `./@_.java` && relpath !~ `@_/antlr/@_`) {
        def src := filedir.getTwine()
        try {
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkWidth(src)
            return good()
        } catch problem {
            return bad(problem)
        }
    } else if (relpath =~ `./@_.@ext` && ["updoc", "txt"].contains(ext)) {
        def src := filedir.getTwine()
        try {
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkWidth(src)
            return good()
        } catch problem {
            return bad(problem)
        }
    } else if (relpath =~ `./@_.@ext` && ["html", "htm"].contains(ext)) {
        def src := filedir.getTwine()
        try {
            stderr.print("*")
            checkUpdoc(makeUpdocParser.parseHtml(src))
            # checkWidth(html2updoc(src))
            return good()
        } catch problem {
            return bad(problem)
        }
    } else {
        return true
    }
}

def USAGE := "usage: srccheck.e [pathname [importRoot]]"

switch (interp.getArgs()) {
    match [`--help`] {
        println(USAGE)
    }
    match [pathname, importRoot] {
        srcCheckRecurse(<file>[pathname], ".", <file>[importRoot].getPath())
    }
    match [pathname] {
        srcCheckRecurse(<file>[pathname], ".", null)
    }
    match [] {
        srcCheckRecurse(<file:.>, ".", null)
    }
    match _ {
        throw(USAGE)
    }
}
