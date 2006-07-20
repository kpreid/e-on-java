/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* shortcut_utils.h - manipulating Windows shortcuts (*.lnk files) */

#ifndef __SHORTCUT_UTILS
#define __SHORTCUT_UTILS

#include "type_utils.h"

/**
 * Create Shortcuts with IShellLink and IPersistFile. argv[0] must be
 * the path of the existing executable.
 */
extern void makeShortcut(
    const char *linkPath,            /* shortcut to make (MUST END IN .lnk) */
    const char *cwd OPTIONAL,        /* working dir when launching shortcut  */
    const char *desc OPTIONAL,       /* description (how is this used?) */
    int         showCmd OPTIONAL,    /* how should the window show? */
    const char *iconFile OPTIONAL,   /* file with icon resource */
    int         iconIndex OPTIONAL,  /* index of icon in file */
    const char *targetPath,          /* executable of command to run */
    int         argc,
    char       *argv[]               /* args of command */
);

#endif /* __SHORTCUT_UTILS */
