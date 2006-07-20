// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// prop_utils.h - portable Property manip utilities

#ifndef __PROP_UTILS
#define __PROP_UTILS

#include "string_utils.h"

/**
 * The Properties type is loosely based on Java's Properties type.
 * Like the Java one, the implementation should be a hash table, but we
 * don't bother yet.
 */
typedef struct _Properties {
    ListBuffer *keys;
    ListBuffer *values;
} Properties;

/**
 *
 */
extern Properties * newProperties();

/**
 * Returns a fresh copy
 */
extern char * getProp(Properties *self, const char *key);

/**
 * If not found, return <tt>instead</tt> itself (not a copy)
 */
extern char * getProp3(Properties *self,
                       const char *key,
                       char *instead);

/**
 * Stores a fresh copy
 */
extern void putProp(Properties *self, const char *key, const char *value);

/**
 *
 */
extern void loadProps(Properties *self, const char *filename);

/**
 *
 */
extern void saveProps(Properties *self, const char *filename);

/**
 * Like saveProps(), saves the props into filename as "<name>=<value>"
 * pairs, one per line. However, filename isn't constructed from
 * scratch. Rather, template is copied but with each "${<name>}"
 * replaced with "<name>=<value>". If the set of names in template
 * and self don't match exactly, die with an error.
 */
extern void fillInProps(Properties *self,
                        const char *template,
                        const char *filename);


#endif /* __PROP_UTILS */
