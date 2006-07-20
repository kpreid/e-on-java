// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// setup_utils.h - some stuff for supporting E installation on Windows
// Visual C++ only. Not for use with Cygwin

#ifndef __SETUP_UTILS
#define __SETUP_UTILS

#include "prop_utils.h"
#include "type_utils.h"
#include "string_utils.h"

// For all ask*() functions, the var starts with the default value,
// shown to the user, which the user can accept easily.

/**
 * Asks for a string
 */
extern void askStr(const char *question, char **strVar);

/**
 * Asks for a boolean
 */
extern void askBool(const char *question, BOOL *boolVar);

/**
 * Asks for an actual directory, making one if needed.
 */
extern void askDir(const char *question, char **dirVar);

/**
 * An optional directory
 */
typedef struct _OptDir {
    BOOL useFlag;            // is this directory used?
    char *name;
} OptDir;

/**
 * Asks for an optional actual directory, making one if needed.
 */
extern void askOptDir(const char *question, OptDir *optDirVar);

/**
 *
 */
typedef struct _SetupConfig {   //For example:
    char  *ehomeDir;            // c:/Program Files/erights.org/
    char  *javaCmd;             // c:/Windows/java.exe
    ListBuffer *javaCmds;       // all the choices
    char  *launchDir;           // c:/Windows/Desktop
    char  *menuDir;             // c:/Windows/Start Menu/Programs/erights.org
    char  *traceDir;            // c:/Windows/Temp/etrace
    OptDir optOnPATH;           // c:/Windows/
    OptDir optDesktop;          // c:/Windows/Desktop
    BOOL   grabExtensions;      // Y
} SetupConfig;

/**
 * Initializes to defaults using winfo_utils but not the registry (and
 * therefore not preceeding installations).
 */
extern SetupConfig *configFromWinfo();

/**
 * Shows the state of config as a human readable text string.
 */
extern char * configToString(SetupConfig *config);

/**
 * As a bunch of e.name=value properties
 */
extern void configToProps(SetupConfig *config, Properties *props);

/**
 * Adjusts config to correspond to any previous installations,
 * according to info stored in the registry.
 */
extern void configFromRegistry(SetupConfig *config);

/**
 * Change the registry to reflect config
 */
extern void configToRegistry(SetupConfig *config);

/**
 * Allows the user to adjust the setting in config
 */
extern void configFromUser(SetupConfig *config);

/**
 * Installs E according to config, including registry changes
 */
extern void installConfig(SetupConfig *config);

/**
 * Uninstalls E according to config
 */
extern void uninstallConfig(SetupConfig *config);

#endif __SETUP_UTILS
