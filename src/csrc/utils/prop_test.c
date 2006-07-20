// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// prop_test.c - see if it works

#include <stdio.h>

#include "prop_utils.h"

static const char *USAGE = 
    "Usage: prop_test propfile get key\n"
    "    or prop_test propfile put key value\n";

int main(int argc, char *argv[]) {
    Properties * props = newProperties();

    if (2 <= argc && streq(argv[1], "--help")) {
        printf("%s", USAGE);

    } else if (4 == argc && streq(argv[2], "get")) {
        loadProps(props, argv[1]);
        printf("%s\n", getProp(props, argv[3]));

    } else if (5 == argc && streq(argv[2], "put")) {
        loadProps(props, argv[1]);
        putProp(props, argv[3], argv[4]);
        saveProps(props, argv[1]);

    } else {
        fprintf(stderr, "%s", USAGE);
        return -1;
    }
    return 0;
}
