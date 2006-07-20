#!/usr/bin/env bash

# Copyright 2004 Bryce "Zooko" Wilcox-O'Hearn
# distributed under the terms of the MIT X license found at
# http://www.opensource.org/licenses/mit-license.html
# and included at the end of this file.

# This script is named "inst-e.bash". This is version 0.9 of this
# script. I used this script on Debian circa 2004-01-29 with j2sdk
# 1.4.1 and "E-otc 0.8.25n for linux-motif/x86". This script requires
# bash, and has not been tested with any version of bash older than
# bash 2.05. This script uses a command named "uname" to determine
# the operating system and machine type.

# HOW TO USE THIS SCRIPT.
# 1. Edit the variables below if desired.
# 2. Build E. In E-otc 0.8.25n, this is accomplished by unpacking the source
#    distribution into a directory, cd'ing into "e/src" and executing "make".
# 3. Cd to the top-level "e" directory. (If you are in e/src, do "cd ..".)
# 4. Invoke inst-e.bash.

### VARIABLES THAT YOU MIGHT WANT TO CHANGE

INSTDIR=/usr/local/e
TRACELOGDIR=/tmp/etrace

### VARIABLES THAT YOU PROBABLY NEEDN'T CHANGE (unless you use GNU stow)

FINALINSTDIR=${INSTDIR}

# If you use GNU stow (and you should!), uncomment the following two
# lines. You can invoke stow after this script completes.
#
# INSTDIR=/usr/local/stow/e
# FINALINSTDIR=/usr/local/

### VARIABLES THAT YOU PROBABLY NEEDN'T CHANGE
JCMD=$(type -p java)
OSDIR=$(uname)
MACHDIR=$(uname -m)

### code that you hopefully needn't change

# internal-use variables (the user really really needn't edit these)
EHOME=${INSTDIR}/e
if [ "X${OSDIR}" = "XLinux" ]; then OSDIR="linux"; fi
if [ "X${MACHDIR}" = "Xi386" ]; then MACHDIR="x86"; fi
if [ "X${MACHDIR}" = "Xi586" ]; then MACHDIR="x86"; fi
if [ "X${MACHDIR}" = "Xi486" ]; then MACHDIR="x86"; fi
if [ "X${MACHDIR}" = "Xi686" ]; then MACHDIR="x86"; fi
if [ "X${MACHDIR}" = "Xx86_64" ]; then MACHDIR="x86"; fi # Does this work?

# create dirs
echo mkdir -p ${TRACELOGDIR}
mkdir -p ${TRACELOGDIR} || exit -1
echo mkdir -p ${INSTDIR}
mkdir -p ${INSTDIR} || exit -1

# move the whole "dist" tree to ${EHOME}
echo copying E files to ${EHOME}
cp -rf ./dist ${EHOME} || ( echo "I don't see ./dist. Maybe I'm not in the top-level \"e\" directory, or maybe E has not been built." ; exit -1 ) || exit -1

# fill in the variables in the three config files
echo filling in variables in the E files: ${EHOME}/devrune, ${EHOME}/eprops.txt, ${EHOME}/rune
cd ${EHOME} || exit -1
DATA=$(<devrune-template.txt) || exit -1
DATA=${DATA/\$\{\{e.dev.home\}\}/${EHOME}}
DATA=${DATA/\$\{\{e.javacmd\}\}/${JCMD}}
DATA=${DATA/\$\{\{e.osdir\}\}/${OSDIR}}
DATA=${DATA/\$\{\{e.machdir\}\}/${MACHDIR}}
echo "${DATA}" > devrune || exit -1
chmod ugo+x devrune

DATA=$(<eprops-template.txt) || exit -1
DATA=${DATA/\$\{\{e.home\}\}/${EHOME}}
DATA=${DATA/\$\{\{e.javacmd\}\}/${JCMD}}
DATA=${DATA/\$\{\{e.launch.dir\}\}//dev/null}
DATA=${DATA/\$\{\{TraceLog_dir\}\}/${TRACELOGDIR}}
DATA=${DATA/\$\{\{e.put.bash.pathlist\}\}/${FINALINSTDIR}/bin}
DATA=${DATA/\$\{\{e.put.shortcut.pathlist\}\}//dev/null}
DATA=${DATA/\$\{\{e.osdir\}\}/${OSDIR}}
DATA=${DATA/\$\{\{e.machdir\}\}/${MACHDIR}}
echo "${DATA}" > eprops.txt || exit -1
chmod ugo+x eprops.txt

DATA=$(<rune-template.txt) || exit -1
DATA=${DATA/\$\{\{e.home\}\}/${EHOME}}
DATA=${DATA/\$\{\{e.javacmd\}\}/${JCMD}}
DATA=${DATA/\$\{\{e.osdir\}\}/${OSDIR}}
DATA=${DATA/\$\{\{e.machdir\}\}/${MACHDIR}}
echo "${DATA}" > rune || exit -1
chmod ugo+x rune

# change the directory layout to be unixish, with "bin", "share", and
# "lib" subdirectories.
echo unixifying directory layout
cd ${INSTDIR} || exit -1
mkdir -p ${INSTDIR}/bin || exit -1

cd ${INSTDIR}/bin || exit -1
ln -snf ${EHOME}/rune . || exit -1

mkdir -p ${INSTDIR}/share/emacs/site-lisp || exit -1
cd ${INSTDIR}/share/emacs/site-lisp || exit -1
ln -snf ${EHOME}/scripts/e-mode.el . || exit -1

mkdir -p ${INSTDIR}/lib || exit -1
cd ${INSTDIR}/lib || exit -1
ln -snf ${EHOME}/bin/${OSDIR}-motif/${MACHDIR}/libXm.so.2 . || exit -1
ln -snf libXm.so.2 libXm.so || exit -1
ln -snf ${EHOME}/bin/${OSDIR}-motif/${MACHDIR}/libswt-motif-2135.so . || exit -1


# distributed under the terms of the MIT X license found at
# http://www.opensource.org/licenses/mit-license.html
# and included below:

# Copyright (c) 2004, Bryce "Zooko" Wilcox-O'Hearn
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
