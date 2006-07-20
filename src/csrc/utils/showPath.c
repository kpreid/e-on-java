// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// showPath.c - portable display of the PATH variable

//XXX prints garbage under cygwin. This probably means that there's
//an error in normalizePath() in the cygwin case.

#include <stdio.h>

#include "type_utils.h"
#include "string_utils.h"
#include "file_utils.h"

int main(int argc, char *argv[]) {
    char ** pathPath = NULL;
    int i;

    if (argc == 1) {
        pathPath = getPATH();
    } else if (argc == 2) {
        if (streq(argv[1], "--help")) {
            printf("Usage: %s [command]\n"
                    "\tshow the PATH,\n"
                    "\tor the places on PATH where command is found\n",
                    argv[0]);
            return 0;
        } else {
            pathPath = freeListBuffer(whichAll(NULL, argv[1]));
        }
    }

    for (i = 0; NULL != pathPath[i]; i++) {
        printf("%s\n", pathPath[i]);
    }
    return 0;
}

