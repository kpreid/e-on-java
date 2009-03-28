#!/usr/bin/env rune

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

pragma.syntax("0.9")
pragma.enable("explicit-result-guard")

def makeELexer := <elang:syntax.makeELexer>
def makeEParser := <elang:syntax.makeEParser>
def Term := <type:org.quasiliteral.term.Term>
def <tools> := <import:org.erights.e.tools.*>
def argParser := <import:org.erights.e.tools.args.argParser>


def optCacheFile :=
  if (interp.getProps().fetch("e.home", fn{}) =~ eHomeName :notNull) {
    <file>[eHomeName]["updoc-hash-cache.txt"]
} else {
    null
}
def makeUpdocParser := <tools:updoc.makeUpdocParserAuthor>(optCacheFile)

def warn {
    to run(warning, src) :void {
        warn(warning, src, null)
    }
    to run(warning :String, src :Twine, optPathname) :void {
        stderr.println()
        if (null != optPathname) {
            stderr.println(`at <file:$optPathname#:span::1:0::1:0>`)
        }
        stderr.print(`# warning: $warning`)
        if (src.getOptSpan() =~ span :notNull) {
            stderr.print(` from: $span`)
        } else if (src.getParts() =~ [firstPart] + _) {
            if (firstPart.getOptSpan() =~ span :notNull) {
                stderr.print(` from: $span`)
            }
        }
        stderr.println()
        stderr.println()
    }
}

def checkExprs(exprs :List[Twine]) :void {
    if (exprs =~ [firstExpr]+_ && firstExpr !~ `pragma.syntax(@_)@_`) {
        warn(`undeclared updoc version`, firstExpr)
    }

    # XXX Should refactor: the following is copy-pasted from
    #     cmdMakerMaker.emaker
    def lexer := makeELexer(null,           # optLineFeeder
                            false,          # partialFlag
                            true)           # noTabsFlag
    def parser := makeEParser.make(null,    # optProps
                                   lexer,
                                   stderr,  # warning
                                   false,   # debugFlag
                                   false)   # onlyOneExprFlag
    for expr in exprs {
        parser.setSource(expr)
        parser.parse()
    }
}

def checkUpdoc(script :Term) :void {
    def term`script(@inVats(@{vats :List[String]}*),
                    [inVat(@{vatNames :List[String]},
                           [test(@{exprss :List[List[Twine]]},
                                 @_)*])*])` := script
    # Since the checking we need here doesn't depend on the time sequence
    # between vats, but just on the textual sequence within each vat
    # separately, accumulate and check these separate sequences.
    def map := [].asMap().diverge()
    for vat in vats {
        map[vat] := [].diverge()
    }
    for i => vatName in vatNames {
        map[vatName].append(exprss[i])
    }
    for exprs in map {
        checkExprs(exprs.snapshot())
    }
}

def checkText(src) :void {
    if (src.startOf("\t") =~ pos :(int >= 0)) {
        warn(`tab at $pos`, src(pos,pos+1))
    }
    return
# XXX Make the following switchable
    for line in src.split("\n") {
        def len := line.size()
        if (len > 79) {
            warn(`$len column line`, line(79,len))
            return #only one width warning per compilation unit.
        }
    }
}

def STDHEADER := "#!/usr/bin/env rune"

def checkHeader(var src) :void {
    if (src =~ `$STDHEADER$\n@rest`) {
        if (rest =~ `@left$STDHEADER@_`) {
            warn("duplicate header", rest(left.size(),
                                          left.size()+STDHEADER.size()))
        }
        src := rest
    } else {
        warn(`First line should be "$STDHEADER"`, src(0,1))
    }
    if (src !~ `@{_}opyright@_`) {
        warn("Missing copyright notice", src(0,1))
    }
}

/**
 * Returns the index of the first line of lines containing something other than
 * whitespace, a "#"-style comment, or an apparent updoc case.
 * <p>
 * Return -1 if none.
 */
def firstRealLine(lines :List[Twine]) :int {
    for i => var line in lines {
        line := line.trim()
        if (line.size() >= 1 && !("#?>".contains(line[0]))) {
            return i
        }
    }
    return -1
}

def checkESrc(src) :void {
    checkHeader(src)
    checkText(src)
    if (src =~ `@left$\npragma.syntax("0.@ver")$\n@right`) {
        def lines := left.split("\n")
        if (firstRealLine(lines) =~ i :(int >= 0)) {
            def line := lines[i].trim()
            warn(`text before version: ${E.toQuote(line)}`, line)
        }
        if (right =~ `@{pre}pragma.syntax@_` && !(pre.endsWith("? "))) {
            warn("duplicate version",
                 right(pre.size(),
                       pre.size() + "pragma.syntax".size()))
        }
    } else {
        warn("undeclared version", src(0,1))
    }
}


def STDEXTS := [".e", ".e-awt", ".e-swt"].asSet()

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
    def ext := argParser.getExtension(relpath)
    def good() :boolean {
        stderr.print(".")
        return true
    }
    def bad(msg, src) :boolean {
        warn(`$msg`, src, fullpath)
        return false
    }
    if (filedir.isDirectory()) {
        if (relpath =~ `@_/.svn`) {
            return true
        }
        var result := true
        stderr.print("[")
        for name => sub in filedir {
            result &= srcCheckRecurse(sub, `$relpath/$name`, optImportRoot)
        }
        makeUpdocParser.checkpoint()
        stderr.print("]")
        return result
    } else if (ext == ".emaker") {
        def src := filedir.getTwine()
        try {
            if (null != optImportRoot &&
                  fullpath =~ `$optImportRoot@restpath.emaker`) {

                def fqname := restpath.replaceAll("/", ".")
                <import>[fqname]
            }
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkESrc(src)
            return good()
        } catch problem {
            return bad(problem, src(0,1))
        }
    } else if (ext == ".caplet") {
        def src := filedir.getTwine()
        try {
            e__quasiParser(src)
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkESrc(src)
            return good()
        } catch problem {
            return bad(problem, src(0,1))
        }
    } else if (STDEXTS.contains(ext)) {
        def src := filedir.getTwine()
        if (! (src.startsWith(STDHEADER + "\n"))) {
            stderr.println(fullpath)
            return bad(`First line should be "$STDHEADER"`, src(0,1))
        } else {
            try {
                e__quasiParser(src)
                checkUpdoc(makeUpdocParser.parsePlain(src))
                checkESrc(src)
                return good()
            } catch problem {
                return bad(problem, src(0,1))
            }
        }
    } else if (ext == ".java" && relpath !~ `@_/antlr/@_`) {
        def src := filedir.getTwine()
        try {
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkText(src)
            return good()
        } catch problem {
            return bad(problem, src(0,1))
        }
    } else if (ext == ".updoc") {
        def src := filedir.getTwine()
        try {
            checkHeader(src)
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkText(src)
            return good()
        } catch problem {
            return bad(problem, src(0,1))
        }
    } else if (ext == ".txt") {
        def src := filedir.getTwine()
        try {
            checkUpdoc(makeUpdocParser.parsePlain(src))
            checkText(src)
            return good()
        } catch problem {
            return bad(problem, src(0,1))
        }
    } else if ([".html", ".htm"].contains(ext)) {
        def src := filedir.getTwine()
        try {
            stderr.print("*")
            checkUpdoc(makeUpdocParser.parseHtml(src))
            # checkText(html2updoc(src))
            return good()
        } catch problem {
            return bad(problem, src(0,1))
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
