/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* winfo_utils.h - information about this Windows system */

#ifndef __WINFO_UTILS
#define __WINFO_UTILS

#include <stdio.h>

#include "string_utils.h"

/**
 * e.g., MarkM
 */
extern char * getUserName();

/**
 * e.g., c:/Windows, c:/WinNt
 */
extern char * getWindowsDir();

/**
 * e.g. c:/Windows/System, c:/WinNT/System32
 */
extern char * getSystemDir();

/**
 * Could be anything
 */
extern char * getCurrentDir();

/**
 * e.g., c:/Windows/Temp, ???
 */
extern char * getTempDir();

/**
 * A NamedValue table associating the name of the CSIDL enum values
 * with these values. The names are the enum names without the
 * "CSIDL_" prefix. These enum values are used to ask about special
 * folders.
 */
extern NamedVal CSIDLs[];

/*
 * These were missing from the corresponding Cygwin include file, but
 * are now present in more recent versions of Cygwin (1.3.12). If
 * these are missing for you, we suggest you upgrade your Cygwin, but
 * you can chose to uncomment-out this section as a temporary measure.
 *
 * #ifndef _MSC_VER
 * # define CSIDL_ALTSTARTUP       0x1d
 * # define CSIDL_APPDATA          0x1a
 * # define CSIDL_COOKIES          0x21
 * # define CSIDL_HISTORY          0x22
 * # define CSIDL_INTERNET         0x01
 * # define CSIDL_INTERNET_CACHE   0x20
 * # define CSIDL_PRINTHOOD        0x1b
 * #endif
 */


/**
 * On success, returns the only pointer to a newly allocated string
 * with the normalized path to the special folder we asked for with
 * nFolder. nFolder is a CSIDL enum value as held in the CSIDLs table.
 * NOTE: On failure, return NULL rather than calling fatalError().
 */
extern char * getSpecialPath(int nFolder);

/**
 * Prints system info to the file in a format accepted by Java's
 * Properties.load(), for use with installation, and for a user to
 * send back when they have a problem
 */
extern void dumpWinfo(FILE *f);


#endif /* __WINFO_UTILS */
