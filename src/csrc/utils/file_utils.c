// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// file_utils.c - portable file manip utilities

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <errno.h>
#include <limits.h>
#ifdef WIN32
# include <windows.h>
# include <process.h>
# include <sys/cygwin.h>
#else
# include <sys/wait.h>
# include <unistd.h>
#endif

#include "error_utils.h"
#include "string_utils.h"
#include "file_utils.h"

//XXX should be provided by an include file, but are sometimes missing
#ifndef MAX_PATH
# define MAX_PATH 260
#endif
#ifndef _S_IFDIR
# define _S_IFDIR 0x4000 /* Directory */
#endif
#ifndef _S_IFREG
# define _S_IFREG 0x8000 /* Regular */
#endif


#define BUF_SIZE 4096

char * getFileText(const char *filename) {
    StringBuffer *sb = newStringBuffer();
    char buf[BUF_SIZE];
    FILE *file = fopen(filename, "rt");

    require(NULL != file, "File not found", filename);

    /*
     * I would have liked to use fread, but with the possibility of
     * newline conversion, I can't find out how many characters in the
     * resulting buffer are valid. Is the definition of fread really
     * that stupid?
     *XXX According to the MS docs, by fopening the file in "t" mode,
     * fgets should convert from platform newlines to standard '\n's.
     * We need to chack that it does.
     */
    while (NULL != fgets(buf, BUF_SIZE, file)) {
        append(sb, buf);
    }
    require(0 == ferror(file), "File read error", filename);
    require(0 == fclose(file), "File close error", filename);
    return freeStringBuffer(sb);
}


void setFileText(const char *filename, const char *text) {
    int len = strlen(text);
    FILE *file = fopen(filename, "wt");

    require(NULL != file, "Can't write file", filename);
    require(fwrite(text, 1, len, file) == len,
            "File write error",
            filename);
    require(0 == fclose(file), "File close error", filename);
}

static char * noExtraSlash(const char *path) {
    char * result = strdup(path);
    char *lastSlash = strrchr(result, '/');
    int i;
    if (NULL == lastSlash || lastSlash[1] != '\0') {
        /* if there aren't any, or it's not at the end, we're cool. */
        return result;
    }
    i = lastSlash - result;
    if (2 == i && ':' == result[1]) {
        /* "X:/" is cool. */
        return result;
    }
    if (0 == i) {
        /* "/" is cool */
        return result;
    }
    /* It's not cool. Kill it. */
    result[i] = '\0';
    return result;
}

static char * filePair(const char *dir, const char *name) {
    StringBuffer *sb = newStringBuffer();
    int dirLen = strlen(dir);
    append(sb, dir);
    if (dirLen >= 1 && '/' != dir[dirLen-1]) {
        append(sb, "/");
    }
    append(sb, name);
    return freeStringBuffer(sb);
}


char * normalizePath(const char *path) {
#ifdef WIN32
    char buf[MAX_PATH];
    cygwin_conv_to_full_win32_path(path, buf);
    return noExtraSlash(replaceAll(buf, "\\", "/"));
#else
    char buf[PATH_MAX];
    require(realpath(path, buf) != NULL,
            strerror(errno),
            path);
    return noExtraSlash(buf);
#endif
}


char * normalizePair(const char *dir, const char *name) {
    char * path = filePair(dir, name);
    char * result = normalizePath(path);
    free(path);
    return result;
}

char * parentPath(const char *path) {
    int len = strlen(path = normalizePath(path));
    const char *lastSlash = strrchr(path, '/');
    int parentlen;
    char *result;
    if (NULL == lastSlash || &path[len-1] == lastSlash) {
        /* since this is post normalization, the test is valid */
        return NULL;
    }
    parentlen = lastSlash - path;
    result = strndup(path, parentlen);
    return noExtraSlash(result);
}

char ** getPATH() {
    ListBuffer *lb = newListBuffer();

    /*
     * Note: on MSWindows, Cygwin will return a posix-path, ie, one
     * with ':' separators. We normalize after using the colons to parse.
     */
    char *pathList = getenv("PATH");
    int len;
    char *p = NULL;
    char *q = NULL;
    if (NULL == pathList) {
        return freeListBuffer(lb);
    }
    /* a private copy we can overwrite */
    pathList = strdup(pathList);
    len = strlen(pathList);
    for (p = pathList; p < &pathList[len]; p = q+1) {
        q = strchr(p, ':');
        if (NULL == q) {
            q = &pathList[len];
        } else {
            *q = '\0';
        }
        if (strlen(p) >= 1) {
            pushString(lb, normalizePath(p));
        }
    }
    free(pathList);
    return freeListBuffer(lb);
}

ListBuffer * whichAll(ListBuffer *opt, const char *command) {
    char ** pathPath = getPATH();
    char *path;
    int i;

    if (NULL == opt) {
        opt = newListBuffer();
    }
    for (i = 0; NULL != pathPath[i]; i++) {
        path = normalizePair(pathPath[i], command);
        if (FILE_IS_REGULAR == getFileMode(path)) {
            pushUnique(opt, normalizePath(path));
        }
    }
    return opt;
}


FileMode getFileMode(const char *path) {
    struct stat statBuf;

    int mode;
    if (stat(normalizePath(path), &statBuf) != 0) {
        /* XXX is this an adequate test? */
        return FILE_NOT_EXIST;
    }
    mode = statBuf.st_mode;
    if ((mode & _S_IFDIR) != 0) {
        return FILE_IS_DIRECTORY;
    } else if ((mode & _S_IFREG) != 0) {
        return FILE_IS_REGULAR;
    } else {
        /* is this an adequate test? */
        return FILE_IS_DEVICE;
    }
}

static void makeDir(const char *path) {
    int result = mkdir(path, 0777);
    require(0 == result,
            subst1("Directory not created: $0",
                   strerror(errno)),
            path);
}

void mkdirs(const char *path) {
    FileMode mode = getFileMode(path = normalizePath(path));
    char *parent;

    if (FILE_IS_DIRECTORY == mode) {
        /* declare victory */
        return;
    }
    require(FILE_NOT_EXIST == mode,
            "Not a directory",
            path);
    parent = parentPath(path);
    if (NULL != parent) {
        mkdirs(parent);
    }
    makeDir(path);
}


int mySpawnVP(const char *cmdname, const char *argv[]) {
    int exitCode;
#ifdef WIN32
    exitCode = spawnvp(_P_WAIT, cmdname, argv);
#else

    pid = fork();
    if (pid == 0) {
        execvp(cmdname, (char *const*)argv);
        fprintf(stderr, "*** Failed to execvp %s\n", cmdname);
        exitCode = 666;
    } else {
        if (waitpid(pid, &status, 0) == pid && WIFEXITED(status)) {
            exitCode = WEXITSTATUS(status);
        } else {
            fprintf(stderr, "*** Failed to waitpid for %s\n", cmdname);
            exitCode = 777;
        }
    }
#endif

    if (0 == exitCode) {
        return 0;
    }
    cmdname = replaceAll(cmdname, "\\", "/");
    if (strchr(cmdname, '/') != NULL) {
        /* If there are slashes, see if the file exists */
        int mode = getFileMode(cmdname);
        if (mode == FILE_NOT_EXIST) {
            fprintf(stderr, "*** %s doesn't exist\n", cmdname);
        } else if (mode != FILE_IS_REGULAR) {
            fprintf(stderr, "*** %s isn't a normal file\n", cmdname);
        }
    } else {
        /* If there are no slashes, check the PATH */
        ListBuffer *lb = whichAll(NULL, cmdname);
        if (lb->len == 0) {
            fprintf(stderr, "*** %s isn't on the PATH\n", cmdname);
        }
    }
    return exitCode;
}


/**
 * Like pushString, but doesn't pushd "." or ".." onto the list
 */
static void pushFile(ListBuffer *lb, const char *name) {
    if (streq(name, ".") || streq(name, "..")) {
        return;
    }
    pushString(lb, name);
}

#ifdef WIN32

char **listNames(const char *path) {
    char * prefix = normalizePath(path);
    int len = strlen(prefix);
    ListBuffer *lb = newListBuffer();
    WIN32_FIND_DATA fData;
    HANDLE searchHandle;
    DWORD lastError;

    if (getFileMode(path) != FILE_IS_DIRECTORY) {
        return freeListBuffer(lb);
    }

    if (len >= 1 && prefix[len-1] == '/') {
        len--;
        prefix[len] = '\0';
    }
    searchHandle = FindFirstFile(subst1("$0/*.*", prefix), &fData);

    if (INVALID_HANDLE_VALUE == searchHandle) {
        lastError = GetLastError();
        if (ERROR_NO_MORE_FILES == lastError) {
            return freeListBuffer(lb);
        } else if (ERROR_SUCCESS == lastError) {
            fatalError("Internal: FindFirstFile Error Confusion",
                       prefix);
        } else {
            mustSucceed(lastError,
                        subst1("Finding children on $0", prefix));
        }
    }

    pushFile(lb, fData.cFileName);
    while (FindNextFile(searchHandle, &fData)) {
        pushFile(lb, fData.cFileName);
    }
    lastError = GetLastError();
    if (ERROR_NO_MORE_FILES == lastError) {
        if (FindClose(searchHandle)) {
            return freeListBuffer(lb);
        } else {
            lastError = GetLastError();
        }
    }
    if (ERROR_SUCCESS == lastError) {
        fatalError("Internal: FindNextFile Error Confusion",
                   prefix);
    } else {
        mustSucceed(lastError,
                    subst1("Finding children on $0", prefix));
    }
    return NULL; /* keep compiler happy */
}

void copyTree(const char *fromPath, const char *toPath) {
    char **list;
    DWORD lastError;
    int i;

    if (getFileMode(fromPath) == FILE_IS_DIRECTORY) {
        mkdirs(toPath);
        list = listNames(fromPath);
        for (i = 0; NULL != list[i]; i++) {
            copyTree(normalizePair(fromPath, list[i]),
                     normalizePair(toPath,   list[i]));
        }
        free(list);
    } else {
        if (CopyFile(fromPath, toPath, FALSE)) {
            return;
        }
        lastError = GetLastError();
        if (ERROR_SUCCESS == lastError) {
            fatalError("Internal: CopyFile Error Confusion",
                       subst2("$0 to $1", fromPath, toPath));
        } else {
            mustSucceed(lastError,
                        subst2("Copying $0 to $1", fromPath, toPath));
        }
    }
}

#endif
