#!/usr/bin/env sh

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

set -e

mkdir -p /usr/local/e
cp -r * /usr/local/e
cd /usr/local/e

cp bin/linux-386-glibc/e /usr/local/bin/e
mkdir -p ~/.etrace/etrace

scripts/eConfig.e-awt
