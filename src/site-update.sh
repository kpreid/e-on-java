#!/usr/bin/env sh

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

set -e

# XXX This file is completely stale.  Do not use.

# site-update.sh
#
# This script gets run on the hosted site whenever a new copy of E is
# released.  The general pattern of usage is:
#
#        1. FTP the E tar and zip files to the hosting site.
#        2. Log in to the hosting site and run this script as
#
#              site-update -r release-number
#
#           where ``release-number'' is the release number of the 
#           current E release.
#
# The usual usage is driven from the makefile by the 'make publish' target

USAGE="Usage: site-update -r RELEASE-NUMBER"

while getopts r: c
do
    case $c in
    r)  RELEASE=${OPTARG};;
    \?) echo ${USAGE}; exit 2;;
    esac
done

shift `expr ${OPTIND} - 1`

if [ -z "${RELEASE}" ]
then
    echo ${USAGE}
    exit 2
fi

echo "Installing release of version ${RELEASE}"


PKGS="tl-daffE-dist-${RELEASE}.zip \
    tl-daffE-dist-${RELEASE}.tar.gz \
    tl-daffE-doc-${RELEASE}.tar.gz \
    tl-daffE-src-${RELEASE}.zip \
    tl-daffE-src-${RELEASE}.tar.gz"

for p in ${PKGS}
do
    if [ ! -r ${p} ]
    then
        echo "${p} is missing"
        exit 1
    fi
done

gzip -d -c tl-daffE-doc-${RELEASE}.tar.gz > tl-daffE-doc-${RELEASE}.tar

mkdir doc
mv tl-daffE-doc-${RELEASE}.tar doc
(cd doc; tar xf tl-daffE-doc-${RELEASE}.tar)
rm xf tl-daffE-doc-${RELEASE}.tar

if [ -d website ]
then
    mkdir -p oldweb
    mv website/* oldweb
fi

mv doc/* website
rm -rf doc

if [ -d oldweb ]
then
    rm -rf oldweb
fi

if [ ! -d website/tarballs ]
then
    mkdir website/tarballs
fi

mv ${PKGS} website/tarballs/

ln -s tl-daffE-dist-${RELEASE}.zip \
    website/tarballs/tl-daffE-dist-current.zip
ln -s tl-daffE-dist-${RELEASE}.tar.gz \
    website/tarballs/tl-daffE-dist-current.tar.gz
ln -s tl-daffE-doc-${RELEASE}.tar.gz \
    website/tarballs/tl-daffE-doc-current.tar.gz
ln -s tl-daffE-src-${RELEASE}.zip \
    website/tarballs/tl-daffE-src-current.zip
ln -s tl-daffE-src-${RELEASE}.tar.gz \
    website/tarballs/tl-daffE-src-current.tar.gz
