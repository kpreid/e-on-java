#!/usr/bin/env rune

# Copyright 2004-2009 Kevin Reid, under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................
# 
# NAME
#     safejTemplate -- generate safej from Java classes
#
# SYNOPSIS
#     safejTemplate.e [-classpath <classpath>] <Java class name>
# 
# DESCRIPTION
#    This is a useful hack for getting started in writing a safej file. It extracts the protocol of a class using 'javap', then writes a corresponding safej term with everything suppressed, for you to edit.
# 
# OPTIONS
#     -classpath
#         Controls where the class to be examined will be searched for.
#
# BUGS
#     Java reflection should perhaps be used instead of parsing the textual output of javap. There is no guarantee that the parser understands everything javap might output. (Note however that using reflection implies loading the classes into the same jvm as the host E.)
#     
#     There ought to be an option to write the safej into a file into a directory according to the FQN.
#     
#     It doesn't know when to use reject instead of suppress.
#     
#     The regular expressions may be too lenient in some cases.

pragma.syntax("0.9")
pragma.enable("accumulator")

def javap := makeCommand("javap")

# --- Argument parsing ---

var args := interp.getArgs()
var optClassPath := null
switch (args) {
  match [`-classpath`, cp] + rest {
    args := rest
    optClassPath := cp
  }
  match _ {}
}
def [className] := args

# --- Fetch javap result ---

def [p, _] := if (optClassPath != null) {
  javap("-classpath", optClassPath, "-public", className)
} else {
  javap("-public", className)
}

# --- javap parsing ---

var statics := []
var methodz := []

/** Remove package qualification from a type name. */
def shortenType(rx`(?:.*\.)?(@x[][A-Za-z_$$]+)`) { return x }

/** Remove package qualification from a comma-separated list of type names and insert spaces after the commas, thus converting javap argument lists to safej argument lists. */
def shortenArgs(argStr) {
  if (argStr == "") {
    return ""
  }
  return ", ".rjoin(accum [] for x in argStr.split(", ") { 
    _.with(shortenType(x))
  })
}

# Names which SafeJ automatically suppresses, so we don't need to mention.
# I'd like to import instead of copying this list, but SafeJ doesn't make it public.
def ALWAYS_REMOVE := [
        "clone()",
        "equals(Object)",
        "finalize()",
        "getClass()",
        "hashCode()",
        "notify()",
        "notifyAll()",
        "toString()",
        "wait()",
        "wait(long)",
        "wait(long, int)"
    ].asSet()


for line in p.split("\n") {
  stderr.println(line)
  switch (line) {
    # Public method
    match rx`    public(?: abstract)?(@stat static)?(?: final)?(?: synchronized)? [][A-Za-z0-9_$$.]+ (@verb[A-Za-z_]+)\((@argStr.*)\)(?:\s+throws [A-Za-z_$$., ]+?)?;` {
      def name := `$verb(${shortenArgs(argStr)})`
      if (ALWAYS_REMOVE.contains(name)) {
        continue
      }
      def mterm := term`method(suppress, .String.$name)`
      if (stat != null) {
        statics with= mterm
      } else {
        methodz with= mterm
      }
    }

    # Public constructor
    match rx`    public $className\((@argStr.*)\);` {
      def mterm := term`method(suppress, .String.${`run(${shortenArgs(argStr)})`})`
      statics with= mterm
    }

    # Public field
    match rx`    public(@stat static)?(@fin final)? (@type[][A-Za-z0-9_$$.]+) (@noun[A-Za-z0-9_]+);` {
      def [initial] + rest := noun
      def capped := __makeTwine.fromChars([initial], null).toUpperCase() + rest
      def mtermGet := term`method(suppress, .String.${`get$capped()`})`
      def mtermSet := term`method(suppress, .String.${`set$capped(${shortenType(type)})`})`
      {
        def &list := (stat != null).pick(&statics, &methodz)
        list with= mtermGet
        if (fin == null) {
          list with= mtermSet
        }
      }
    }
    
    # Uninteresting
    match `Compiled from @_` {}
    match `interface @_{` {}
    match rx`public(?: abstract)?(?: final)? class [A-Za-z_$$.]+( extends [A-Za-z_$$.]+)?( implements [A-Za-z_$$., ]+)?{` {}
    match `       throws @_` {}
    match `` {}
    match `}` {}
  }
}

# --- Generate final term ---

println(term`class(.String.$className,
                   statics($statics*),
                   methods($methodz*))`.asText())