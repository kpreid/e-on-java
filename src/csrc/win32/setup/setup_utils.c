// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// setup_utils.c - some stuff for supporting E installation on Windows
// Visual C++ only. Not for use with Cygwin

#include <windows.h>
#include <string.h>
#include <stdio.h>
#include <shlobj.h>
#include <ctype.h>

#include "error_utils.h"
#include "file_utils.h"
#include "reg_utils.h"
#include "winfile_utils.h"
#include "winfo_utils.h"
#include "setup_utils.h"
#include "shortcut_utils.h"

#define MAX_BUF 10000

void askString(const char *question, char **strVar) {
    char buf[MAX_BUF];
    int len;
    printf("%s\n    (default: \"%s\") ? ",
           question,
           *strVar);
    fflush(stdout);
    require(fgets(buf, MAX_BUF -1, stdin) == buf,
            "Reading standard input",
            subst1("Asking $0", question));
    len = strlen(buf);
    if (len >= 1 && buf[len-1] == '\n') {
        buf[--len] = '\0';
    }
    if (0 != len) {
        *strVar = strdup(buf);
    }
}

void askBool(const char *question, BOOL *boolVar) {
    char *answer;
    while (TRUE) {
        answer = *boolVar ? "Y" : "N";
        askString(question, &answer);
        if (strieq(answer, "y") || strieq(answer, "yes")) {
            *boolVar = TRUE;
            return;
        } else if (strieq(answer, "n") || strieq(answer, "no")) {
            *boolVar = FALSE;
            return;
        } else {
            fprintf(stderr, "  unrecognized: %s\n", answer);
        }
    }
}



void askDir(const char *question, char **dirVar) {
    FileMode mode;
    BOOL create;

    while (TRUE) {
        askString(question, dirVar);
        *dirVar = normalizePath(*dirVar);
        mode = getFileMode(*dirVar);
        if (FILE_NOT_EXIST == mode) {
            create = TRUE;
            askBool(subst1("  \"$0\" doesn't exist. Create it?", *dirVar),
                    &create);
            if (create) {
                mkdirs(*dirVar);
                return;
            }
        } else if (FILE_IS_DIRECTORY == mode) {
            return;
        } else {
            fprintf(stderr, "  \"%s\" is not a directory\n", *dirVar);
        }
    }
}

void askOptDir(const char *question, OptDir *optDirVar) {
    askBool(question, &optDirVar->useFlag);
    if (optDirVar->useFlag) {
        askDir("  at?", &optDirVar->name);
    }
}

static char * NO_JAVA_CMD = "*** Could not find a java.exe on the PATH";

SetupConfig *configFromWinfo() {
    SetupConfig *config = (SetupConfig*)allocOrDie(sizeof(SetupConfig),
                                                   "SetupConfig");
    config->ehomeDir    = "C:/Program Files/erights.org";
    config->javaCmds    = whichAll(NULL, "java.exe");
    config->javaCmd     = NO_JAVA_CMD;
    if (config->javaCmds->len >= 1) {
        config->javaCmd = strdup(config->javaCmds->buf[0]);
    }

    config->launchDir   = getSpecialPath(CSIDL_DESKTOPDIRECTORY);
    config->menuDir     = normalizePair(getSpecialPath(CSIDL_PROGRAMS),
                                        "erights.org");
    config->traceDir    = normalizePair(getTempDir(), "etrace");

    config->optOnPATH.useFlag   = TRUE;
    config->optOnPATH.name      = getWindowsDir();

    config->optDesktop.useFlag  = TRUE;
    config->optDesktop.name     = getSpecialPath(CSIDL_DESKTOPDIRECTORY);

    config->grabExtensions      = TRUE;
    return config;
}

static void appendOptDir(StringBuffer *sb, OptDir *optDir, char *desc) {
    if (optDir->useFlag) {
        append(sb, subst2("\n$0$1", desc, optDir->name));
    } else {
        append(sb, subst1("\nNo $0", desc));
    }
}

char * configToString(SetupConfig *config) {
    StringBuffer *sb = newStringBuffer();
    append(sb, "\nE Install Dir      ");  append(sb, config->ehomeDir);
    append(sb, "\nJava Command       ");  append(sb, config->javaCmd);
    append(sb, "\nLaunch Dir         ");  append(sb, config->launchDir);
    append(sb, "\nMenu Dir           ");  append(sb, config->menuDir);
    append(sb, "\nDir for trace data ");  append(sb, config->traceDir);

    appendOptDir(sb, &config->optOnPATH,      "Dir on PATH        ");
    appendOptDir(sb, &config->optDesktop,     "Desktop Dir        ");

    append(sb, "\nGrab extensions?   ");  append(sb,
                                        (config->grabExtensions ? "Y" : "N"));
    return freeStringBuffer(sb);
}

void configToProps(SetupConfig *config, Properties *props) {
    putProp(props, "e.version",         getFileText("eVersion.txt"));
    putProp(props, "e.home",            config->ehomeDir);
    putProp(props, "e.javacmd",         config->javaCmd);
    putProp(props, "e.launch.dir",      config->launchDir);
    putProp(props, "TraceLog_dir",      config->traceDir);

    putProp(props, "e.put.exe.path", "XXX");
    //appendMore(sb, &config->optOnPATH);

    putProp(props, "e.put.shortcut.path", "XXX");
    //append(sb, config->menuDir);
    //appendMore(sb, &config->optDesktop);

    if (config->grabExtensions) {
        putProp(props, "e.GrabExtensions", "true");
    } else {
        putProp(props, "e.GrabExtensions", "false");
    }
    putProp(props, "e.vendor", "ERights.org");
    putProp(props, "e.vendor.url", "http://www.erights.org/");
}


void configFromRegistry(SetupConfig *config) {
    HKEY e = OpenKey(HKLM, "Software\\erights.org\\e");
    if (NULL == e) {
        return;
    }
    if (REG_SZ == GetValueType(e, NULL, "InstallDir")) {
        config->ehomeDir = GetStringValue(e, NULL, "InstallDir");
        config->ehomeDir = normalizePath(config->ehomeDir);
        printf("previous installation at %s\n", config->ehomeDir);
    }
    CloseKey(e);
}

void configToRegistry(SetupConfig *config) {
    SetStringValue(HKLM,
                   "Software\\erights.org\\e",
                   "InstallDir",
                   config->ehomeDir);
    SetStringValue(HKLM,
              "Software\\Microsoft\\Windows\\CurrentVersion\\App Paths\\e.jar",
                   "",
                   subst1("$0/bin/jars/e.jar", config->ehomeDir));
}

static BOOL isInteger(const char *str) {
    int i;
    if (! isdigit(str[0])) {
        // make sure the null string isn't an integer
        return FALSE;
    }
    for (i = 1; str[i] != '\0'; i++) {
        if (! isdigit(str[0])) {
            return FALSE;
        }
    }
    return TRUE;
}



void configFromUser(SetupConfig *config) {
    int i;
    askDir(
"\nWhere do you wish to install E?"
"\n  A typical answer would be \"C:/Program Files/erights.org\"",
           &config->ehomeDir);
    if (config->javaCmds->len >= 1) {
        printf(
"\nSome plausible Java VMs on your machine are:");
        for (i = 0; i < config->javaCmds->len; i++) {
            printf("\n    %d %s", i+1, config->javaCmds->buf[i]);
        }
    }
    askString(
"\nWhich Java VM should I use?  It must be a Java >= 1.2,"
"\nthough we strongly recommend a Java >= 1.3.",
              &config->javaCmd);
    if (isInteger(config->javaCmd)) {
        int index = atoi(config->javaCmd);
        if (index >= 1 && index <= config->javaCmds->len) {
            config->javaCmd = config->javaCmds->buf[index-1];
        }
        /* XXX The above code interprets an out of range integer as a
           file name. This is probably stupid. */
    }

    askDir(
"\nWhat should be the current directory for the E and Elmer shortcut-icons?",
           &config->launchDir);
    askDir(
"\nWhere in the Start menu would you like these shortcut-icons?",
           &config->menuDir);
    askDir(
"\nWhere would you like internal trace data (for debugging purposes) put?",
           &config->traceDir);

    askOptDir(
"\nAlso copy e.exe onto the PATH?"
"\n  This would enable E to be run from an MSDOS shell by just saying \"e\".",
              &config->optOnPATH);
    askOptDir(
"\nAlso put shortcut-icons on Desktop?",
              &config->optDesktop);

    askBool(
"\nMay I register .e, .emaker, .updoc, & .cap?"
"\n  This would determine the action on double-click, and the entries"
"\n  seen in their right-button menu.",
            &config->grabExtensions);
}

void installConfig(SetupConfig *config) {
    // e.exe path
    char *e_exe = normalizePair(config->ehomeDir, "bin/win32/e.exe");
    // path to elmer.e script
    char *elmer_e = normalizePair(config->ehomeDir, "scripts/elmer.e");
    // path to eBrowser.e script
    char *eBrowser_e = normalizePair(config->ehomeDir, "scripts/eBrowser.e");

    mkdirs(config->traceDir);
    copyTree(".", config->ehomeDir);
    if (config->optOnPATH.useFlag) {
        copyTree(e_exe, normalizePair(config->optOnPATH.name, "e.exe"));
    }
    mkdirs(config->menuDir);
    makeShortcut(e_exe,
                 normalizePair(config->menuDir, "e.lnk"),
                 NULL,
                 config->launchDir,
                 NULL,
                 SW_SHOW,
                 normalizePair(config->ehomeDir, "bin/icons/e-lambda.ico"),
                 0);
    makeShortcut(e_exe,
                 normalizePair(config->menuDir, "elmer.lnk"),
                 subst1("\"$0\"", elmer_e),
                 config->launchDir,
                 NULL,
                 SW_HIDE,
                 normalizePair(config->ehomeDir, "bin/icons/carrot2.ico"),
                 0);

    if (config->optDesktop.useFlag) {
        copyTree(config->menuDir, config->optDesktop.name);
    }

    if (! config->grabExtensions) {
        return;
    }

    FileAssoc(".e",                             // extension
              "e-script",                       // type
              "text/x-escript",                 // mime type
              "E Script",                       // description
              normalizePair(config->ehomeDir,   // icon file
                            "bin/icons/e-doc.ico"),
              0,                                // icon index
              "*");                             // quick view

    AddCommand("e-script",
               "Launch",
               subst1("\"$0\" \"%1\"", e_exe),
               TRUE);

    AddCommand("e-script",
               "eBrowse",
               subst2("\"$0\" \"$1\" \"%1\"",
                      e_exe,
                      eBrowser_e),
               FALSE);

    FileAssoc(".emaker",                        // extension
              "e-maker",                        // type
              "text/x-emaker",                  // mime type
              "E Maker File",                   // description
              normalizePair(config->ehomeDir,   // icon file
                            "bin/icons/emaker-doc.ico"),
              0,                                // icon index
              "*");                             // quick view

    AddCommand("e-maker",
               "eBrowse",
               subst2("\"$0\" \"$1\" \"%1\"",
                      e_exe,
                      eBrowser_e),
               TRUE);

    FileAssoc(".updoc",                         // extension
              "e-updoc",                        // type
              "text/x-updoc",                   // mime type
              "E Updoc Script",                 // description
              normalizePair(config->ehomeDir,   // icon file
                            "bin/icons/carrot-doc.ico"),
              0,                                // icon index
              "*");                             // quick view

    AddCommand("e-updoc",
               "eBrowse",
               subst2("\"$0\" \"$1\" \"%1\"",
                      e_exe,
                      eBrowser_e),
               FALSE);

    AddCommand("e-updoc",
               "Elmer",
               subst2("\"$0\" \"$1\" \"%1\"",
                      e_exe,
                      elmer_e),
               TRUE);

    FileAssoc(".cap",                           // extension
              "e-cap",                          // type
              "text/x-cap",                     // mime type
              "E Capability",                   // description
              normalizePair(config->ehomeDir,   // icon file
                            "bin/icons/e-cap.ico"),
              0,                                // icon index
              "*");                             // quick view
}

static char *HKCRKeys[] = {
    ".e",       "e-scripts"
    ".emaker",  "e-maker",
    ".updoc",   "e-updoc",
    ".cap",     "e-cap",
    // "MIME\\Database\\Content Type\\text/x-cap"

    NULL
};

// for HKLM:
//      "Software\\Microsoft\\Windows\\CurrentVersion\\App PAths\\e.exe"
//      "Software\\erights.org\e"


void uninstallConfig(SetupConfig *config) {
    int i;
    // XXX mostly unimplemented

    if (! config->grabExtensions) {
        return;
    }
    for (i = 0; NULL != HKCRKeys[i]; i++) {
        DeleteKey(HKCR, HKCRKeys[i]);
    }
}
