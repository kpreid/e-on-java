// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// setup.c - install E on Windows
// Visual C++ only. Not for use with Cygwin

#include <stdio.h>
#include <windows.h>

#include "prop_utils.h"
#include "winerror_utils.h"
#include "setup_utils.h"

int main(int argc, char *argv[]) {
    char buf[1000];
    SetupConfig *config;
    Properties *props = newProperties();

    //XXX process command line args, including --help

#ifdef _MSC_VER
    setFatalErrorHandler(monologueBoxHandler);
#endif

    printf("If the cursor is on the end of this line, just hit Enter. ? ");
    fgets(buf, 999, stdin);
    config = configFromWinfo();
    configFromRegistry(config);

    configToProps(config, props);
    if (argc == 3 && streq(argv[1], "--debug")) {
        saveProps(props, argv[2]);
    } else {
        uninstallConfig(config);
    }
    return 0;
}
