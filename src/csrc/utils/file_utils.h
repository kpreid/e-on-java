// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// file_utils.h - portable file manip utilities

#ifndef __FILE_UTILS
#define __FILE_UTILS

#include "string_utils.h"

/**
 * Reads the file as a text file, converting platform newlines to
 * '\n's, returns the only pointer to the resulting string, and closes
 * the file. If anything fails, it's a fatal error.
 */
extern char * getFileText(const char *filename);

/**
 * Overwrites the file, creating it if necessary, with text converted
 * to the platform's newline convention.
 */
extern void setFileText(const char *filename, const char *text);

/**
 * Return the only pointer to the newly allocated normalized path. A
 * normalized path is absolute, uses "/" as a separator, and ends in a
 * "/" only if it is a top-level directory. On MSWindows, the drive
 * is handled using the Win32 conventions rather Cygwin/Posix
 * conventions -- a drive letter followed by a colon. This is like
 * the E-normal form for a file name, except that in the E normal
 * form, all directories end in "/".
 */
extern char * normalizePath(const char *path);

/**
 * Return the only pointer to the newly allocated normalized path
 * whose directory is <dir> and whose name is <name>. It's like
 * "<dir>/<name>", but if <dir> already ends in a slash we don't add
 * an extra slash.
 */
extern char * normalizePair(const char *dir, const char *name);

/**
 * If path is not a top-level directory, returns the only pointer to
 * the newly allocated normalizedPath of its parent directory. If it
 * is a top level directory, return NULL.
 */
extern char * parentPath(const char *path);

/**
 * A null terminated array of normalized directory paths, originally
 * separated by ";" or ":" (on Windows) or ":" (on Unix) in the PATH
 * environment variable.
 *
 * Returns the only pointer to a newly allocated array of the only
 * pointers to newly allocated strings.
 */
extern char ** getPATH();

/**
 * 'command' should be a local file name (i.e., one with no
 * directory information) naming a sought after command like
 * "java.exe". This returns a ListBuffer holding the normalized paths
 * whose directory is from getPATH() and ending in this name, where
 * these paths are FILE_IS_REGULAR. Each path appears only once.
 *
 * If opt is NULL, a new one is allocated. Otherwise, the paths are
 * added to opt. opt is returned.
 */
extern ListBuffer * whichAll(ListBuffer *opt, const char *command);

/**
 *
 */
typedef enum {
    FILE_NOT_EXIST,
    FILE_IS_REGULAR,
    FILE_IS_DIRECTORY,
    FILE_IS_DEVICE
} FileMode;

/**
 * What kind of file, if any, is named 'path'?
 */
extern FileMode getFileMode(const char *path);

/**
 * Like _mkdir(path), but will also make parent directories if
 * needed, and fatalError()s on error rather than returning an error
 * code.
 */
extern void mkdirs(const char *path);


/**
 * Acts like the standard spawnvp, as implemented by cygwin, except
 * that it will quote the argv array (under Windows)
 */
extern int mySpawnVP(const char *cmdname, const char *argv[]);

/**
 * Returns a list of the names (not the paths) of all the immediate
 * children of path
 */
extern char **listNames(const char *path);

/**
 * Makes a copy of the directory tree rooted in fromPath at toPath
 */
extern void copyTree(const char *fromPath, const char *toPath);


#endif /* __FILE_UTILS */
