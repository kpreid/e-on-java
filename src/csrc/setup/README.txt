Help on installing and running E


                   Preparing to Install


Before you can install E, you must install a Java executable
compatible with Sun's JDK >= 1.5. Into a shell prompt, type "java
-version" to see your situation. Below, we will refer to the name of
your java executable as if it is bound to the shell variable JAVACMD.

On MSWindows, we assume either the MSDOS Shell (available in Windows2K
or WindowsXP under
"Start Menu >> Programs >> Accessories >> Command Prompt") or Cygwin
bash shell. (The bash shell is preferred.) On all other platforms, we
assume a regular bash shell in a Unix-like environment. If a bash
shell is used, it must be a bash >= 2.01.

For example, in an MSDOS shell:
    C:\>set JAVACMD=java
    C:\>"%JAVACMD%" -version
    java version "1.6.0_23"
    Java(TM) SE Runtime Environment (build 1.6.0_23-b05)
    Java HotSpot(TM) 64-Bit Server VM (build 19.0-b09, mixed mode)
    C:\>

For example, in a bash or Cygwin shell:
    $ JAVACMD=java
    $ "$JAVACMD" -version
    java version "1.6.0_23"
    Java(TM) SE Runtime Environment (build 1.6.0_23-b05)
    Java HotSpot(TM) 64-Bit Server VM (build 19.0-b09, mixed mode)
    $ echo $BASH_VERSION
    3.2.51(24)-release
    $

Unpack the E distribution into the directory you'd like it to live.
On MSWindows, we suggest "C:/Program Files/erights.org/". On *nix, if
you are installing for the system as a whole, we suggest
"/usr/local/e". For a personal install, we suggest "~/ehome". Below,
we will refer to this directory as if it is bound to the shell
variable EHOME.

Below, you should also replace
* <download-dir> with the directory into which you downloaded the E
  distribution.
* <platform> with the name for the platform you're installing on, as
  encoded into the name of the distribution file you're installing
  from. This is either "purej" for the pure Java install (which
  therefore doesn't contain SWT), or <osdir>-<machdir> for the
  names encoding your operating system and instruction set,
  respectively. For example, the platform name "linux-motif-x86"
  consists of the osdir "linux-motif" and the machdir "x86".
* <version> with the version of E you're installing, as encoded into
  the name of the distribution file you're installing from. For
  example, "0.8.22c".


For example, in an MSDOS Shell
    C:\>set EHOME=c:\Program Files\erights.org
    C:\>mkdir "%EHOME%"
    C:\>cd "%EHOME%"
    C:\Program Files\erights.org>
... unpack "<download-dir>\E-win32-x86-<version>.zip" into "%EHOME%" ...
    C:\Program Files\erights.org>

For example, in a Cygwin shell:
    $ EHOME=/cygdrive/c/Program\ Files/erights.org/
    $ mkdir "$EHOME"
    $ cd "$EHOME"
    $ unzip -q "<download-dir>/E-win32-x86-<version>.zip"
    $

For example, in a *nix bash shell for a personal install:
    $ EHOME=~/ehome
    $ mkdir "$EHOME"
    $ cd "$EHOME"
    $ tar xzf <download-dir>/E-<platform>-<version>.tar.gz
    $

For example, in a *nix bash shell for a system install:
    # EHOME=/usr/local/e
    # mkdir "$EHOME"
    # cd "$EHOME"
    # tar xzf <download-dir>/E-<platform>-<version>.tar.gz
    #



                     Installing E


Manually copy "eprops-template.txt" to "eprops.txt", copy
"rune-template.txt" to "rune", optionally copy "devrune-template.txt"
to "devrune", optionally copy "rune-bat-template.txt" to "rune.bat", and
then replace all text of the form "${{<property name>}}" in these new
files according to the comments at those places.

To ease manual installation, some of the entries in the these files
list default values as their value rather than a "${{..}}" form. After
copying these files, you don't need to edit these, but you may.

E scripts by convention begin with "#!/usr/bin/env rune". So if you wish
to run E scripts directly as executables, you will need to have
"/usr/bin/env" installed on your system. This looks on your PATH for its
argument, so you will need to copy or symlink the "rune" driver script
(made in the previous step) into your path. For a system install under
*nix, we recommend "/usr/local/bin/rune". For a personal install under
*nix, we recommend "~/bin/rune". For installing under Windows2K or
WindowsXP, we recommend "c:/Windows/rune".

"rune" is a bash script, and requires a bash >= 2.01 to be installed
as "bash" on your PATH so "#!/usr/bin/env bash" can find it. To check
your bash version, type

    $ echo $BASH_VERSION

into a bash shell. If you can't find an adequate bash for your system,
you can still run E scripts the old fashioned way, as explained below
under "Running E".

Finally, each user of E, if they may be interested in running capDesk,
should make a copy of the <EHOME>/caplets directory at ~/caplets.


SWT on Linux:

We are currently experiencing a strange bug on one installation of Red
Hat Linux ("X Error of failed request: BadFont (invalid font
parameter)"), but we don't have the problem on another Red Hat
installation, and we haven't had the opportunity to try it on any
other Linuxes. If you have any information about this bug, please let
us know. Thanks.

If you get an error message about a file named libXm.so.2, then see
http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/platform-swt-home/faq.html#missinglibXm
for more on this issue.




                         Running E


Once E in installed you can run E scripts without using bash and
without SWT support by saying

    $ cd "$EHOME"
    $ java -jar e.jar <script>.<ext> <args...>

or without bash and with SWT support by saying

    $ cd "$EHOME"
    $ LD_LIBRARY_PATH="bin/$OSDIR/$MACHDIR/"
    $ export LD_LIBRARY_PATH
    $ java -cp "e.jar:bin/$OSDIR/swt.jar" \
    > "-Djava.library.path=bin/$OSDIR/$MACHDIR/" \
    > org.erights.e.elang.interp.Rune <script>.<ext> <args...>

where $OSDIR is, for example, "win32" or "linux-motif", $MACHDIR is,
for example "x86", and "<script>.<ext>" is the file name of the E
script. Any args following the script name are provided as arguments
to the script by the E expression "interp.getArgs()".

On MSWindows, the colon (":") shown above after the "e.jar" should
instead be a semicolon (";").

If you succeed at an automatic install, you should also be able to run
E scripts by double clicking on them. The right button menu should
give you more choices as well.

If you have a bash >= 2.01 installed on your system you can use it
to run scripts simply by:

    % rune <script>.<ext> <args...>

or, if the script begins with

    #!/usr/bin/env rune

and is executable (as E scripts should be), and you have
"/usr/bin/env" installed on your system (as all *nix systems seem to
these days), then you can simply say

    % <script>.<ext> <args...>

On some versions of Cygwin, putting the "#!/usr/bin/env rune" at the
top of the file makes it executable.

For more on running the "rune" driver bash script, type

    $ rune --help

or see the file "rune-help.txt".


                   Reporting Bugs


Please report bugs or seek help on the e-lang mailing list, after
subscribing at http://www.eros-os.org/mailman/listinfo/e-lang

Please include any diagnostic output that appears.

Thanks, and have fun using E.
