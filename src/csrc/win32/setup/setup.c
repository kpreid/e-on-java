// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// setup.c - install E on Windows
// Visual C++ only. Not for use with Cygwin

#include <stdio.h>
#include <windows.h>
#include <process.h>

#include "file_utils.h"
#include "prop_utils.h"
#include "winerror_utils.h"
#include "setup_utils.h"

int main(int argc, char *argv[]) {
    char buf[1000];
    SetupConfig *config;
    BOOL okFlag;
    Properties *props = newProperties();
    int exitCode;
    char *args[4];

    args[0] = NULL; // will be the "rune" command
    args[1] = "--noerrorwin";
    args[2] = NULL; // will be the eConfig.e script
    args[3] = NULL; // needs a null terminator

    //XXX process command line args, including --help

#ifdef _MSC_VER
    setFatalErrorHandler(monologueBoxHandler);
#endif

    printf("If the cursor is on the end of this line, just hit Enter. ? ");
    fflush(stdout);
    //printf(".");
    fgets(buf, 999, stdin);
    //printf(".");
    config = configFromWinfo();
    //printf(".");
    configFromRegistry(config);
    //printf(".");

    while (TRUE) {
        while (TRUE) {
            printf("%s\n\n", configToString(config));
            okFlag = TRUE;
            askBool("ok?", &okFlag);
            if (okFlag) {
                break;
            }
            configFromUser(config);
        }

        configToProps(config, props);
        if (argc == 3 && streq(argv[1], "--debug")) {
            saveProps(props, argv[2]);
            return 0;
        }
        configToRegistry(config);
        installConfig(config);
        fillInProps(props,
                    normalizePair(config->ehomeDir, "eprops-template.txt"),
                    normalizePair(config->ehomeDir, "eprops.txt"));

        //args[0] = normalizePair(config->ehomeDir, "bin/win32/e.exe");
        args[0] = "e.exe";
        args[2] = normalizePair(config->ehomeDir, "scripts/eConfig.e");
        exitCode = mySpawnVP(args[0], (const char **)args);
        if (0 == exitCode) {
            return 0;
        } else {
            printf("eConfig failed. Let's try again.\n");
            okFlag = FALSE;
        }
    }
}
