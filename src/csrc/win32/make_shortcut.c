/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* make_shortcut.c - create Microsoft Windows shell links (aka shortcuts). */


#include <windows.h>
#include <stdlib.h>

#include "error_utils.h"
#include "string_utils.h"
#include "shortcut_utils.h"


static NamedVal ShowCmds[] = {
    { "HIDE",           SW_HIDE },
    { "MIN",            SW_MINIMIZE },
    { "RESTORE",        SW_RESTORE },
    { "SHOW",           SW_SHOW },
    { "SHOWMAX",        SW_SHOWMAXIMIZED },
    { "SHOWMIN",        SW_SHOWMINIMIZED },
    { "SHOWMINNA",      SW_SHOWMINNOACTIVE },
    { "SHOWNA",         SW_SHOWNA },
/*    { "SHOWNOACTIVE",   SW_SHOWNOACTIVE }, // in docs but not .h files? */
    { "SHOWNORMAL",     SW_SHOWNORMAL },
    { NULL,             -1 }
};

int main(int argc, char *argv[]) {
    char *linkPath = NULL;
    char *targetPath = NULL;
    char *cwd = NULL;
    int   showCmd = -1;
    char *desc = NULL;
    char *iconFile = NULL;
    int   iconIndex = 0;

    int i;

    /* showNamedValTable(ShowCmds); */

    /* setFatalErrorHandler(monologueBoxHandler); */
    require(argc >= 3,
            "Usage",
            subst1(
"\n"
"usage: $0 [-options] linkPath command args...\n"
"\n"
"  linkPath      : path of shortcut to create (MUST END IN \".lnk\"!)\n"
"  command       : path to executable to be executed by shortcut\n"
"  args...       : args of command\n"
"\n"
"options:\n"
"  -cd   cwd     : working directory to set when launched\n"
"  -show showCmd : one of HIDE, MIN, RESTORE, SHOW\n"
"                         SHOWMAX, SHOWMIN, SHOWMINNA, SHOWNA, SHOWNORMAL\n"
"  -comment desc : A flyover description\n"
"  -icon iconFile iconIndex\n"
"    iconFile    : path of file containing Shortcut's icon\n"
"    iconIndex   : integer index into iconFile\n",
                   argv[0]));

    for (i = 1; i < argc; i++) {
        if (streq(argv[i], "-cd")) {
            cwd = argv[++i];
        } else if (streq(argv[i], "-show")) {
            showCmd = valOfName(argv[++i], ShowCmds);
        } else if (streq(argv[i], "-comment")) {
            desc = argv[++i];
        } else if (streq(argv[i], "-icon")) {
            iconFile = argv[++i];
            iconIndex = strtoul(argv[++i],NULL,0);
        } else if (argv[i][0] == '-') {
            fatalError("Unrecognized option", argv[i]);
        } else if (linkPath == NULL) {
            linkPath = argv[i];
        } else if (targetPath == NULL) {
            targetPath = argv[i];
            i++;
            break;
        }
    }

    makeShortcut(linkPath,      /* "c:/temp/elmer.lnk", */
                 cwd,           /* "c:/windows/desktop", */
                 desc,          /* "For editing Updoc scripts" */
                 showCmd,       /* SW_HIDE */
                 iconFile,      /* "c:/.../icons/e-carrot.ico", */
                 iconIndex,     /* 0 */
                 targetPath,    /* "c:/windows/java.exe" */
                 argc-i,
                 &argv[i]);     /* ["-jar", "e.jar"] */
    return 0;
}

