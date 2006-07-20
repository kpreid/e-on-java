/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* file_assoc.c - Associate an extension with a type, mime-type, & icon */


#include <windows.h>
#include <stdlib.h>

#include "error_utils.h"
#include "string_utils.h"
#include "reg_utils.h"


int main(int argc, char *argv[]) {
    char *usageStr = subst1(
"\n"
"usage: $0 [-options] .ext type desc mime\n"
"\n"
"  .ext            : file extension to bind (eg, '.e')\n"
"  type            : Regsitry type for this extension (eg, 'e-script')\n"
"  desc            : Short phrase describing this type (eg, 'E Script')\n"
"  mime            : Corresponding mime type (eg, 'text/x-escript')\n"
"\n"
"options:\n"
"  -icon iconFile iconIndex\n"
"    iconFile      : Path of file containing icon for these files\n"
"    iconIndex     : integer index into iconFile\n"
"  -quickView str  : It's good to say \"-quickView '*'\"\n",
                            argv[0]);

    char *iconFile = NULL;
    int   iconIndex = 0;
    char *quickView = NULL;
    char *ext = NULL;
    char *type = NULL;
    char *desc = NULL;
    char *mime = NULL;

    int i;

    /* setFatalErrorHandler(monologueBoxHandler); */
    require(argc >= 6,
            "Usage", 
            usageStr);

    for (i = 1; i < argc; i++) {
        if (streq(argv[i], "-icon")) {
            iconFile = argv[++i];
            iconIndex = strtoul(argv[++i],NULL,0);
        } else if (streq(argv[i], "-quickView")) {
            quickView = argv[++i];
        } else {
            require(argv[i][0] != '-',
                    "Unrecognized option",
                    usageStr);
            break;
        }
    }

    require(i + 4 == argc,
            "Usage",
            usageStr);

    ext = argv[i];
    type = argv[i+1];
    desc = argv[i+2];
    mime = argv[i+3];

    FileAssoc(ext,
              type,
              desc,
              mime,
              iconFile,
              iconIndex,
              quickView);              
    return 0;
}

