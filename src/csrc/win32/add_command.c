/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* add_command.c -- add a command to the right button menu of this type */


#include <windows.h>
#include <stdlib.h>

#include "error_utils.h"
#include "string_utils.h"
#include "reg_utils.h"


int main(int argc, char *argv[]) {
    char *usageStr = subst1(
"\n"
"usage: $0 [-options] type menuName command args...\n"
"\n"
"  type      : Regsitry type for this extension (eg, 'e-script')\n"
"  menuName  : Name to appear in right button menu\n"
"  command   : Executable of command to run\n"
"  args...   : Args of command\n"
"\n"
"options:\n"
"  -default  : Makes this be the command launched on double left click\n",
                            argv[0]);

    BOOL isDefault = FALSE;
    char *type = NULL;
    char *menuName = NULL;

    int i;

    /* setFatalErrorHandler(monologueBoxHandler); */
    require(argc >= 3,
            "Usage", 
            usageStr);

    for (i = 1; i < argc; i++) {
        if (streq(argv[i], "-default")) {
            isDefault = TRUE;
        } else {
            require(argv[i][0] != '-',
                    "Unrecognized option",
                    usageStr);
            break;
        }
    }

    require(i + 3 <= argc,
            "Usage",
            usageStr);

    type = argv[i];
    menuName = argv[i+1];
    i += 2;

    AddCommand(type,
               menuName,
               isDefault,
               argc-i, &argv[i]);
    return 0;
}

