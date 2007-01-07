#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def <qq> := <unsafe:org.quasiliteral.*>

def traceline(str) :void {
    stderr.println(str)
}

def safejSchema := <qq:astro.BaseSchema>("safej", [
    ".char.",
    ".int.",
    ".float64.",
    ".String.",

    "class",
    "name",
    "method",
    "static",
    "signature",
    "suppress",
    "comment",
    "byproxy",
    "selfless",
    "byconstruction",
    "persistent",
    "safe"])

def safejBuilder := <qq:term.TermBuilder>(safejSchema)

def qsml2term(qsmlSrc) :any {
    def dom :=
      sml__quasiParser.valueMaker(qsmlSrc.trim()).substitute([]).minimize()
    dom.build(safejBuilder)
}

def optArgs(tree, tagName) :any {
    if (tree.getTag().getTagName() <=> tagName) {
        tree.getArgs()
    } else {
        null
    }
}

def fqn(tree) :any {
    def [x ? (optArgs(x, "name") =~ [result])] + _ := optArgs(tree, "class")
    result.getOptString()
}

def path(fqname) :any {
    def parts := fqname.split(".")
    var sep := "/"
    var result := ""
    for part in parts {
        result += sep + part
        if (! (part <=> part.toLowerCase())) {
            # If it has any upper case in it, assume it's a class
            # so all further steps are nested classes
            sep := "$"
        }
    }
    # get rid of initial "$"
    result(1, result.size()) + ".safej"
}

def openForWriting(dir, path) :any {
    def i := path.lastStartOf("/")
    def parentPath := path(0, i+1)
    dir[parentPath].mkdirs(null)
    dir[path].textWriter()
}

def simpMethod(term`method(static(@oldStatic),
                           signature(@sig),
                           suppress(@oldSuppress),
                           comment(@oldMethComment?))`) :any {
    def newStatic := switch (oldStatic) {
        match term`"true"`  { [term`static`] }
        match term`"false"` { [] }
    }
    def newSuppress := switch (oldSuppress) {
        match term`"true"`  { [term`suppress`] }
        match term`"false"` { [] }
    }
    def newMethComment := switch (oldMethComment) {
        match [comment] { [term`comment($comment)`] }
        match []        { [] }
    }
    term`method($newStatic?, $newSuppress?, $sig, $newMethComment?)`
}

def simpClass(term`class(name(@name),
                         method@oldMeths*,
                         byproxy(@oldByproxy?),
                         selfless(@oldSelfless?),
                         byconstruction(@oldByconstruction?),
                         persistent(@oldPersistent?),
                         safe(@oldSafe),
                         comment(@oldClassComment?))`) :any {
    def staticMeths := [].diverge()
    def instMeths := [].diverge()
    for oldMeth in oldMeths {
        def newMeth := simpMethod(oldMeth)
        if (newMeth =~ term`method(static, @rest*)`) {
            staticMeths.push(term`method($rest*)`)
        } else {
            instMeths.push(newMeth)
        }
    }
    def honored := [].diverge()
    switch (oldByproxy) {
        match [term`"true"`]  { honored.push(term`PassByProxy`) }
        match [term`"false"`] {}
        match []              {}
    }
    switch (oldSelfless) {
        match [term`"true"`]  { honored.push(term`Selfless`) }
        match [term`"false"`] {}
        match []              {}
    }
    switch (oldByconstruction) {
        match [term`"true"`]  { honored.push(term`PassByConstruction`) }
        match [term`"false"`] {}
        match []              {}
    }
    switch (oldPersistent) {
        match [term`"true"`]  { honored.push(term`Persistent`) }
        match [term`"false"`] {}
        match []              {}
    }
    def honorary := if (honored.size() >= 1) {
        [term`honorary($honored*)`]
    } else {
        []
    }
    def newSafe := switch (oldSafe) {
        match term`"true"`  { [term`safe`] }
        match term`"false"` { [] }
    }
    def newClassComment := switch (oldClassComment) {
        match [comment] { [term`comment($comment)`] }
        match []        { [] }
    }
    term`class($newSafe?,
               $name,
               $honorary?,
               statics($staticMeths*),
               methods($instMeths*),
               $newClassComment?)`
}

def convert(smlFiledir, safejRootDir) :void {
    if (smlFiledir.isDirectory()) {
        for sub in smlFiledir {
            convert(sub, safejRootDir)
        }
    } else if (smlFiledir.getName() =~ `@_.txt`) {
        def tree := qsml2term(smlFiledir.getText())
        def simp := simpClass(tree)
        def fqname := fqn(tree)
        def pathName := path(fqname)
        def out := openForWriting(safejRootDir, pathName)
        try {
            simp.prettyPrintOn(out, false)
        } finally {
            out.close()
        }
        traceline(pathName)
    }
}

def [smlDirName, safejDirName] := interp.getArgs()
convert(<file>[smlDirName], <file>[safejDirName])
