// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// error_utils.h - for handling errors

#ifndef __ERROR_UTILS
#define __ERROR_UTILS

#ifdef WIN32
# include <windows.h>
  typedef HRESULT ErrnumType;
#else
  typedef int ErrnumType;
#endif

/** The form of error handling routines */
typedef void (*ErrorHandler)(const char *what, const char *why);

/** Set the fatal error handler. This handler function should not return */
extern void setFatalErrorHandler(ErrorHandler handler);

/** Calls the current fatal error handler */
extern void fatalError(const char *what, const char *why);

/**
 * If cond is FALSE, fatalError(what,why).
 * (cond if really a BOOL.)
 */
extern void require(int cond, const char *what, const char *why);

/** malloc()s successfully, or calls fatalError() */
extern void *allocOrDie(int size, const char *why);


/**
 * If errnum doesn't indicate success, die with a 'what' generated
 * from errnum. On Unix/Linux, errnum is like errno, except that an
 * errnum of 0 indicates success.
 */
extern void mustSucceed(ErrnumType errnum, const char *why);

#ifdef WIN32

/**
 * If you want fatal errors to pop up a monologue box, then
 * setFatalErrorHander(monologueBoxHandler) in your main.
 */
extern void monologueBoxHandler(const char *what, const char *why);

#endif

#endif /*__ERROR_UTILS*/
