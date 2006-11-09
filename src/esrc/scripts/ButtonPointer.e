#!/usr/bin/env rune

pragma.syntax("0.8")

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def root

def complain(complaint) :void {
    # throw(complaint)
    println(complaint)
}

def trace(msg) :void {
    stderr.print(msg)
}
def traceline(msg) :void {
    stderr.println(msg)
}

def editFile(file, varName, newValue) :void {
    if (file.exists()) {
        def text := file.getText()
        if (text =~ `@left<!-- #BeginEditable "$varName" -->@{
                oldValue
              }<!-- #EndEditable -->@right` &&
              oldValue != newValue) {

            file.setText(`$left<!-- #BeginEditable "$varName" -->${
                newValue
            }<!-- #EndEditable -->$right`)
        }
    } else {
        complain(`no $file`)
    }
}

? pragma.syntax("0.8")

? def f := <file:~/e/doc/toc.txt>
# value: <file:c:/e/doc/toc.txt>

? def tr := f.textReader()

def getLine(tr) :Tuple[int, nullOk[String]] {
    while (true) {
        def line := tr.readLine()
        if (line == null) {
            return [0, null]
        } else {
            def name := line.trim()
            if (name != "" && name[0] != '#') {
                def spaces := line.size() - name.size()
                require(spaces %% 4 == 0,
                        fn{`$line not aligned`})
                return [spaces // 4, name]
            }
        }
    }
}

? getLine(tr)
# value: [1, elib/]

? getLine(tr)
# value: [2, capability/]

? getLine(tr)
# value: [3, ode/]

? tr.close()

def walkToc(wdir) :void {
    def tocReader := wdir["toc.txt"].textReader()
    var parent := wdir
    var prev := null
    var prevLevel := 1
    def nest() :void {
        require(prev != null,
                fn{`internal: Missing prev`})
        parent := prev
        prev := null # no left sibling
        prevLevel += 1
    }
    def unnest() :void {
        prev.setNextButton(null) # prev has no right sibling
        prev := parent
        parent := parent.getParentFile()
        prevLevel -= 1
    }
    while (true) {
        def [level, name] := getLine(tocReader)

        if (level > prevLevel) {
            # name is the first child of prev.
            require(level == prevLevel + 1,
                    fn{`can only nest one level at a time:
                           $name $level $prevLevel`})
            nest()
            # 'parent[name]' is redundant. Oh well.
            parent.setDownButton(parent[name])

        } else {
            while (prevLevel > level) {
                # name is the next sibling of an ancestor of prev
                unnest()
            }
        }
        if (level == 0 || name == null) {
            require(level == 0 && name == null,
                    fn{`$level vs $name`})
            break
        }
        def current := parent[name]
        current.setPrevButton(prev)
        if (prev != null) { prev.setNextButton(current) }
        current.setUpButton(parent)
        current.setDownButton(null)
        prev := current
    }
}

def splitName(filename) :any {
    var from := filename.size() -1
    if (filename.endsWith("/")) {
        from -= 1
    }
    def i := filename.lastIndexOf("/", from)
    [filename(0,i+1), filename(i+1, filename.size())]
}

? splitName("foo/bar/baz")
# value: [foo/bar/, baz]

? splitName("foo.bar")
# value: [, foo.bar]

? splitName("foo/bar/")

def relativeURL(osrc, otarget) :any {
    var src := osrc
    var target := otarget
    if (src =~ `<@tmp>`) {
        src := tmp
    }
    if (target =~ `<@tmp>`) {
        target := tmp
    }
    def buf := "".diverge(char)
    while (! (target.startsWith(src))) {
        def [parent, name] := splitName(src)
        if (name.endsWith("/")) {
            buf.append("../")
        }
        src := parent
    }
    require(src == "" || src.endsWith("/"),
            fn{`$src should be at end`})
    def `$src@rest` := target
    buf.append(rest)
    var result := `${buf.snapshot()}`
    if (result.endsWith("/")) {
        result += "index.html"
    } else {
        result
    }
    # traceline(`$result = relativeURL($osrc, $otarget)`)
    trace(".")
    result
}

? relativeURL("foo/bar", "baz/zip")
# value: ../baz/zip

? relativeURL("foo/bar", "baz/zip/")
# value: ../baz/zip/index.html

? relativeURL("foo/bar", "foo/zip")
# value: zip

? relativeURL("foo/bar", "foo/bar/zip")
# problem: required condition failed
#
#   <statics of org.erights.e.elang.interp.Thrower>(required condition failed)
#   <require0>(false, required condition failed)
#   <require0>(false)
#   <relativeURL>(foo/bar, foo/bar/zip)
#   <interp> evalPrint(e`relativeURL run("foo/bar", "foo/bar/zip")`)

? relativeURL("foo/bar/", "foo/bar/zip")
# value: zip

? relativeURL("foo/bar/", "foo/")

def FileWrapperMaker(filedir) :any {
    def file := if (filedir.isDirectory()) {
        filedir["index.html"]
    } else {
        filedir
    }
    def FileWrapper extends filedir {
        to __printOn(out :TextWriter) :void { filedir.__printOn(out) }
        to getTitle() :any {
            var result := "Title Bar Title" # pink.dwt default value
            if (file.exists() &&
                  file.getText() =~ `@_<TITLE>@title</TITLE>@_`) {

                result := title.trim()
            }
            if (file.exists() &&
                  file.getText() =~ `@_<title>@title</title>@_`) {

                result := title.trim()
            }
            if (result == "Title Bar Title") {
                complain(`$file has no title`)
                filedir.getName()
            } else {
                result
            }
        }
        to setNextButton(wfile) :void {
            def base := `$filedir`
            def text := if (wfile == null) {
                `<img src="${
                    relativeURL(base, `<$root/images/next-gray.gif>`)
                }" width="64" height="32" alt="No Next Sibling">`
            } else {
                def altText := `"On to: ${wfile.getTitle()}"`
                `<a href="${
                    relativeURL(base, `$wfile`)
                }" title=$altText><img src="${
                    relativeURL(base, `<$root/images/next.gif>`)
                }" width="64" height="32" alt=$altText border="0"></a>`
            }
            editFile(file, "NextButton", text)
            editFile(file, "NextButton2", text)
        }
        to setPrevButton(wfile) :void {
            def base := `$filedir`
            def text := if (wfile == null) {
                `<img src="${
                    relativeURL(base, `<$root/images/prev-gray.gif>`)
                }" width="64" height="32" alt="No Previous Sibling">`
            } else {
                def altText := `"Back to: ${wfile.getTitle()}"`
                `<a href="${
                    relativeURL(base, `$wfile`)
                }" title=$altText><img src="${
                    relativeURL(base, `<$root/images/prev.gif>`)
                }" width="64" height="32" alt=$altText border="0"></a>`
            }
            editFile(file, "PrevButton", text)
            editFile(file, "PrevButton2", text)
        }
        to setDownButton(wfile) :void {
            def base := `$filedir`
            def text := if (wfile == null) { "" } else {
                def altText := `"1st child: ${wfile.getTitle()}"`
                `<a href="${
                    relativeURL(base, `$wfile`)
                }" title=$altText><img src="${
                    relativeURL(base, `<$root/images/first.gif>`)
                }" width="32" height="64" alt=$altText border="0"></a>`
            }
            editFile(file, "FirstButton", text)
            editFile(file, "FirstButton2", text)
        }
        to setUpButton(wdir) :void {
            def `<$root/@{var path}>` := (`$wdir`)
            def list := [].diverge()
            while (path =~ `@dir/@rest`) {
                list.push(dir)
                path := rest
            }
            require(path == "",
                    fn{`"$path" must be empty`})
            def levels := list.size()
            def buf := "".diverge(char)
            def adjust := if (filedir.isDirectory()) { 0 } else { 1 }
            for i in 0..!levels {
                buf.append(`/&nbsp;<a href="${
                    "../" * (levels - i - adjust)
                }index.html">${list[i]}</a>&nbsp;`)
            }
            editFile(file, "Path", buf.snapshot())
            editFile(file, "Path2", buf.snapshot())
        }
        to getParentFile() :any {
            FileWrapperMaker(<file>[filedir.getParent()])
        }
        to get(name) :any { FileWrapperMaker(filedir[name]) }
    }
}

? def welib := FileWrapperMaker(<c:/e/doc/elib/>)
# value: <file:c:/e/doc/elib/>

? welib.getTitle()
# value: ELib: Local and Remote

? def winsrc := FileWrapperMaker(<c:/e/doc/download/windows-src.html>)
# value: <file:c:/e/doc/download/windows-src.html>

? welib.getDownButton() := null
? welib.getDownButton() := winsrc
# value: <file:c:/e/doc/download/windows-src.html>

?
? def unixbin := FileWrapperMaker(<c:/e/doc/download/unix-bin.html>)
# value: <file:c:/e/doc/download/unix-bin.html>

? winsrc.getNextButton() := null
? winsrc.getNextButton() := unixbin
# value: <file:c:/e/doc/download/unix-bin.html>

? unixbin.getPrevButton() := winsrc
# value: <file:c:/e/doc/download/windows-src.html>

? unixbin.getPrevButton() := null
? unixbin.getUpButton() := FileWrapperMaker(<c:/e/doc/download/>)
# value: <file:c:/e/doc/download/>

def fileName
if (interp.getArgs() =~ [bind fileName]) {
    # we're done
} else {
    # kludge around lack of args in eBrowser's Run action
    bind fileName := "c:/e/doc"
}

def wdir := <file>[fileName]


# XXX should use 'wdir getPath()' rather that ' `$wdir`'
def `<@{bind root}/>` := `$wdir`

try {
    walkToc(FileWrapperMaker(wdir))
} catch problem {
    println(problem.javaStack())
    println(problem.eStack())
}

