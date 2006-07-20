// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// error_utils.c - for handling errors

#include <stdio.h>
#include <malloc.h>
#include <stdlib.h>

#include "error_utils.h"
#include "string_utils.h"

/** The fatal error handler if it's not overridden */
static void defaultFatalErrorHandler(const char *what, const char *why) {
    fprintf(stderr, "Error - %s\n    %s\n", what, why);
    exit(-1);
}

static ErrorHandler TheFatalErrorHandler = &defaultFatalErrorHandler;

void setFatalErrorHandler(ErrorHandler handler) {
    if (handler == &fatalError) {
        fatalError("Internal: Illegal Argument",
                   "Can't set handler to &fatalError");
    }
    TheFatalErrorHandler = handler;
}

/** Calls the current fatal error handler */
void fatalError(const char *what, const char *why) {
    (*TheFatalErrorHandler)(what, why);
    defaultFatalErrorHandler("Internal: Error Handling",
                             "Provided handler returned");
}

void require(int cond, const char *what, const char *why) {
    if (! cond) {
        fatalError(what, why);
    }
}

void *allocOrDie(int size, const char *why) {
    void *result = malloc(size);
    require(NULL != result,
            "Internal: Out of memory",
            why);
    return result;
}


#ifdef WIN32

void mustSucceed(ErrnumType errnum, const char *why) {
    char *what = NULL;
    if (errnum == ERROR_SUCCESS) {
        return;
    }
    if (FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER
                      | FORMAT_MESSAGE_IGNORE_INSERTS
                      | FORMAT_MESSAGE_FROM_SYSTEM
                      | FORMAT_MESSAGE_MAX_WIDTH_MASK,
                      NULL,             // source
                      (DWORD)errnum,    // message ID
                      0,                // language ID
                      (char*)&what,     // must free with LocalFree()
                      0,                // max size
                      NULL)             // args
        && NULL != what)
    {
        fatalError(subst1("Windows: $0", what), why);
    } else {
        fatalError("Unrecognized HRESULT", why);
    }
}

void monologueBoxHandler(const char *what, const char *why) {
    int result = MessageBox(// handle of owner window
                            NULL,
                            // text in message box
                            subst1("$0\n(Hit cancel to segfault)", why),
                            // title of message box
                            subst1("Error - $0", what),
                            // style of message box
                            MB_OKCANCEL | MB_ICONERROR);
    char *p = NULL;
    if (IDCANCEL == result) {
        //cause a segfault or core dump or something that enters a
        //debugger or leaves behind something that can be analyzed
        *p = *p;
    }
    exit(-1);
}

#else

void mustSucceed(ErrnumType errnum, const char *why) {
    if (0 != errnum) {
        fatalError(strerror(errnum), why);
    }
}

#endif
