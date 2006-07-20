/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* file_test.c - test file_utils */

#include <malloc.h>
#include <stdio.h>

#include "error_utils.h"
#include "type_utils.h"
#include "string_utils.h"
#include "file_utils.h"


static void show(int nest, const char *path) {
    char **list = listNames(path);
    int i;

    printf("%d %s\n", nest, path);
    for (i = 0; NULL != list[i]; i++) {
        show(nest+1, normalizePair(path, list[i]));
    }
    free(list);
}

int main(int argc, char *argv[]) {
    require(2 == argc || 3 == argc,
            "Usage:",
            subst1("$0 path [path]", argv[0]));
    if (2 == argc) {
        show(0, argv[1]);
    } else if (3 == argc) {
        copyTree(argv[1], argv[2]);
    }
    return 0;
}
