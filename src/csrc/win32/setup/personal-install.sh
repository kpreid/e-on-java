#!/usr/bin/env sh

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

set -e

cd `dirname $0`
ehome=`pwd`
binDir="$HOME/bin"

if [ -e $binDir ]; then
    if [ ! -d $binDir ]; then
        echo "You have a file named $binDir; usually this should be a"
        echo "directory, and this is where the installer would place some"
        echo "files. Sorry; I cannot proceed as-is."
        exit 1
    fi
else
    echo "You do not have a personal binaries directory named $binDir;"
    echo "shall I create it? (If do not say yes, then I cannot proceed.)"
    echo -n "Create (y/n)? "
    read answer
    if [ "$answer" != "y" ]; then
        echo "Sorry; I cannot proceed as-is."
        exit 1
    fi
    mkdir $binDir
    echo "Be sure to put $binDir on your PATH"
fi

binName="$binDir/e"
if [ -e $binName ]; then
    echo "You already have a binary named $binName; shall I overwrite it?"
    echo "(If you do not say yes, then I cannot proceed.)"
    echo -n "Overwrite (y/n)? "
    read answer
    if [ "$answer" != "y" ]; then
        echo "Sorry; I cannot proceed as-is."
        exit 1
    fi
fi

traceDir="$HOME/.etrace"
if [ -e $traceDir ]; then
    if [ ! -d $traceDir ]; then
        echo "You have a file named $traceDir; usually this should be a"
        echo "directory, and this is where E would place some"
        echo "files. Sorry; I cannot proceed as-is."
        exit 1
    fi
else
    echo "You do not have an etrace directory named $traceDir;"
    echo "shall I create it? (If do not say yes, then I cannot proceed.)"
    echo -n "Create (y/n)? "
    read answer
    if [ "$answer" != "y" ]; then
        echo "Sorry; I cannot proceed as-is."
        exit 1
    fi
    mkdir $traceDir
fi



echo "#!/usr/bin/env sh" > $binName
echo "set -e" >> $binName
echo "export EHOME=$ehome" >> $binName
echo "exec $ehome/bin/linux-386-glibc/e "'"$@"' >> $binName
chmod 755 $binName

$binName scripts/eConfig.e
