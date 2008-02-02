#!/usr/bin/env sh


# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

set -e

# XXX This file is completely stale.  Do not use.

# javadoc-update.sh
#
# This script gets run on the hosted site whenever a new copy of E is
# released.  The general pattern of usage is:
#
#        1. FTP the E tar and zip files to the hosting site.
#        2. Log in to the hosting site and run this script as
#
#              javadoc-update

PKG_TZ="javadoc.tar.gz"
PKG_T="javadoc.tar"

if [ ! -r ${PKG_TZ} ]
    then
        echo "${PKG} is missing"
        exit 1
fi

gzip -d -c ${PKG_TZ} > ${PKG_T}

mkdir newweb
mv ${PKG_T} newweb
(cd newweb; tar xf ${PKG_T})
# rm newweb/${PKG_T}

if [ -d website ]
then
    mkdir -p oldweb
    mv website/* oldweb
fi

mv newweb/* website
rm -rf newweb

if [ -d oldweb ]
then
    rm -rf oldweb
fi

