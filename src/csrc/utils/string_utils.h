// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// string_utils.h - portable string manip utilities

#ifndef __STRING__UTILS
#define __STRING__UTILS

#include "type_utils.h"


/**
 * Return the only ptr to a newly allocated string containing the
 * first n characters of strSource.
 */
extern char *strndup(const char *strSource, size_t n);

/**
 * Are the strings equal?
 */
extern BOOL streq(const char *a, const char *b);

/**
 * Ignoring case, are the strings equal?
 */
extern BOOL strieq(const char *a, const char *b);

/**
 * Returns the only pointer to a newly allocated string holding the
 * integer printed in signed base 10.
 */
extern char *printeger(int value);

/**
 * By loose analogy with the java StringBuffer. Grows on demand.
 */
typedef struct _StringBuffer {
    char *buf;
    int allocLen;       // shouldn't be necessary, but realloc doesn't
                        // seem to work
    int len;            // buf[len] is always '\0'
} StringBuffer;

/** Initialized to a new empty string */
extern StringBuffer * newStringBuffer();

/** Returns the accumulated string but frees the StringBuffer struct */
extern char * freeStringBuffer(StringBuffer *self);

/** Append str(0..!n) to the end, growing if necessary */
extern void appendSubstr(StringBuffer *self, const char *str, int n);

/** Append str to the end, growing if necessary */
extern void append(StringBuffer *self, const char *str);

/** Append c to the end, growing if necessary */
extern void appendChar(StringBuffer *self, char c);

/**
 * Append the quoted form of str.
 */
extern void appendQuoted(StringBuffer *self, const char *str);

/**
 * Appends the strings in argv quoted and separated by spaces.
 */
extern void appendQuotedArgs(StringBuffer *self, int argc, char *argv[]);

/**
 *
 */
extern char * quotedArgs(int argc, char *argv[]);

/**
 * For present purposes, we define a 'list' as a NULL terminated array
 * of strings. We use a ListBufer to accumulate one incrementally.
 */
typedef struct _ListBuffer {
    char **buf;
    int allocLen;       // shouldn't be necessary, but realloc doesn't
                        // seem to work
    int len;            //buf[len] is always NULL
} ListBuffer;

/** Initialized to a new empty list */
extern ListBuffer * newListBuffer();

/** Returns the accumulated list but frees the ListBuffer struct */
extern char ** freeListBuffer(ListBuffer *self);

/** Add str on the end, growing if necessary */
extern void pushString(ListBuffer *self, const char *str);

/**
 * Push all the strings in argv
 */
extern void pushArgs(ListBuffer *self, int argc, char *argv[]);

/**
 * If str is already in the list, ignoring case, then do nothing.
 * Else add it on the end as in pushString.
 */
extern void pushUnique(ListBuffer *self, const char *str);

/**
 * Removes the index'th string from the list, and returns that string.
 * If it isn't the last element, the last element is swapped into its
 * place.
 */
extern char * takeString(ListBuffer *self, int index);

/**
 * Return the only pointer to a newly allocated copy of template in
 * which every $n is replaced by argv[n]. n must be a single digit in
 * 0..9. "$$" turns into an uninterpreted "$". An interpreted "$"
 * not followed by a digit is a fatal error.
 */
extern char * substitute(const char *template, int argc, const char *argv[]);

/** substitute() with 1 argument */
extern char * subst1(const char *template,
                     const char *arg0);

/** substitute() with 2 arguments */
extern char * subst2(const char *template,
                     const char *arg0,
                     const char *arg1);

/** substitute() with 3 arguments */
extern char * subst3(const char *template,
                     const char *arg0,
                     const char *arg1,
                     const char *arg2);


/**
 * Return the only pointer to a newly allocated copy of template in
 * which every occurrence of from is replaced by to, while scanning
 * only once.
 */
extern char *replaceAll(const char *template,
                        const char *from,
                        const char *to);

/**
 * Use an array of these to associate names of enums with the enum
 * values. Terminate the array with a NamedVal whose ->name is NULL.
 */
typedef struct {
    char *name;
    int val;
} NamedVal;

/**
 * Return the value corresponding to name. It's a fatal error if
 * it's not found
 */
extern int valOfName(char *name, NamedVal *table);

/** for debugging purposes */
extern void showNamedValTable(NamedVal *table);

#endif /* __STRING__UTILS */
