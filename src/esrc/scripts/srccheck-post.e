#!/usr/bin/env rune

# Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# Change Schema:
# change ::= change(.String.$relpath,
#                   .int.$lineNo,
#                   .int.$start,
#                   .int.$bound,
#                   .String.$newText)

? def intersects(term`change(@{rp1 :String},
>                            @{ln1 :int}, @{s1 :int}, @{b1 :int},
>                            @_)`,
>                term`change(@{rp2 :String},
>                            @{ln2 :int}, @{s2 :int}, @{b2 :int},
>                            @_)`) :boolean {
>     return rp1 == rp2 && ln1 == ln2 && s1.max(s2) < b1.min(b2)
> }
# value: <intersects>

? intersects(term`change("foo", 3, 22, 44, "")`,
>            term`change("foo", 3, 45,66, "")`)
# value: false

? intersects(term`change("foo", 3, 22, 44, "")`,
>            term`change("foo", 3, 44,66, "")`)
# value: false

? intersects(term`change("foo", 3, 22, 44, "")`,
>            term`change("foo", 3, 43,66, "")`)
# value: true

? intersects(term`change("foo", 3, 22, 44, "")`,
>            term`change("foo", 4, 43,66, "")`)
# value: false

/**
 * Note: If func is Functional, then the following cache would be observably
 * equivalent to a DeepFrozen object.
 * <p>
 * We hope to be able to demonstrate this someday (10 years?) to a DeepFrozen
 * Auditor which uses a PCC proof checker.
 */
def makeStringCache(func, size :int) :near {
    def keys := ([null] * size).diverge()
    def values := ([null] * size).diverge()
    def cache(key :String) :any {
        # Using cryptohash is overkill to get random replacement.
        # Thanks to Alan Karp for the random replacement idea.
        def index := key.getBareCryptoHash() %% size
        if (keys[index] == key) {
            return values[index]
        } else {
            keys[index] := key
            return values[index] := func(key)
        }
    }
    return cache
}


/**
 * Maps from path Strings to lists of line Strings, or null if the file doesn't
 * exist.
 * <p>
 * getLines is certainly not Functional, and therefore linesCache (defined
 * below) is not DeepFrozen.
 */
def getLines(path :String) :nullOk[List[String]] {
    def file := <file>[path]
    if (file.exists()) {
        return file.getText().split("\n")
    } else {
        return null
    }
}

def linesCache := makeStringCache(getLines, 1000)

def getLine(path :String, lineNo :int) :nullOk[String] {
    if (linesCache(path) =~ lines :notNull) {
        return lines[lineNo -1] #line numbers are 1 index origin
    } else {
        return null
    }
}

def changes := [].diverge()

def eatReports(reportFile, oldPrefix, newPrefix) :void {
    for line in reportFile {
#        throw.breakpoint([oldPrefix, line])
        if (line =~ `@_# warning: @problem: $\
<file:$oldPrefix@relpath#:span::@sln:@sc::@eln:@ec>@_`) {
            try {
                if (sln != eln) { continue }
                def lineNo := __makeInt(sln)
                def optLine := getLine(`$newPrefix$relpath`, lineNo)
                def startCol := __makeInt(sc)
                def endCol := __makeInt(ec)
                if (null == optLine) { continue }
                switch (problem) {
                    match `@_ e.enable.no-dot-call @_` {
                        if (startCol <= 1) { continue }
                        def oldText := optLine(startCol -2, endCol +1)
                        if (oldText =~ `@left @right`) {
                            if (left.size() != 1 || left =~ ` `) { continue }
                            if (right.size() <= 0 || right =~ `@_ @_`) {
                                continue
                            }
                            def newText := `$left.$right`
                            changes.push(term`change(.String.$relpath,
                                                     .int.$lineNo,
                                                     .int.${startCol -2},
                                                     .int.${endCol +1},
                                                     .String.$newText)`)
                        }
                    }
                    match `@_ e.enable.explicit-result-guard @_` {
                        if (startCol <= 1) { continue }
                        def oldText := optLine(startCol -2, endCol +1)
                        if (oldText =~ `) {`) {
                            def newText := `) :void {`
                            changes.push(term`change(.String.$relpath,
                                                     .int.$lineNo,
                                                     .int.${startCol -2},
                                                     .int.${endCol +1},
                                                     .String.$newText)`)
                        }
                    }
                    match _ {}
                }
            } catch problem {
                stderr.println(`## oops: $relpath:$problem`)
            }
        }
    }
}


eatReports(<file:~/e/src/report-esrc.txt>,
           "C:/Documents and Settings/MILLERM1/e/",
           "~/e/")

def answer := term`changes(${changes.sort()}*)`
println(answer)

def doChanges(term`changes(@changes*)`, prefix :String) :void {
    var lastPath := null
    var lastFile := null
    var newLines := null
    var lastLines := null
    var lastLineNo := -1
    var lastBound := -1

    def advanceTill(lineBound :int) :boolean {
        if (lastLineNo >= lineBound) {
            require(lastLineNo == lineBound)
            return false
        }
        def lastLine := lastLines[lastLineNo-1]
        newLines[lastLineNo-1] += lastLine(lastBound, lastLine.size())
stderr.println(`$lastPath:$lastLineNo:$lastBound:${newLines[lastLineNo-1]}`)
        lastLineNo += 1
        while (lastLineNo < lineBound) {
            newLines[lastLineNo-1] := lastLines[lastLineNo-1]
            lastLineNo += 1
        }
        lastBound := 0
        return true
    }

    for term`change(@{relpath :String},
                    @{lineNo :int}, @{start :int}, @{bound :int},
                    @{newText :String})` in changes {
        def path := `$prefix$relpath`
        if (lastPath != path) {
            if (null != lastPath) {
                advanceTill(lastLines.size())
                lastFile.setText("\n".rjoin(newLines.snapshot()))
            }
            lastPath := path
            lastFile := <file>[path]
            newLines := [""].diverge(String)
            lastLines := linesCache(path)
            lastLineNo := 1
            lastBound := 0
        }
        if (advanceTill(lineNo)) {
            newLines[lineNo-1] := ""
        }
        if (start < lastBound) {
            # conflicting change, ignore
            continue
        }
        def line := lastLines[lineNo-1]
        newLines[lineNo-1] += line(lastBound, start)
        newLines[lineNo-1] += newText
        lastBound := bound
    }
    if (null != lastPath) {
        advanceTill(lastLines.size())
        lastFile.setText("\n".rjoin(newLines.snapshot()))
    }
}

doChanges(answer, "~/e/")

