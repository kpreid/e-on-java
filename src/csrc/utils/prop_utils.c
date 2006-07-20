// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// prop_utils.c - portable Property manip utilities

#include <stdlib.h>
#include <string.h>

#include "error_utils.h"
#include "file_utils.h"
#include "prop_utils.h"

Properties * newProperties() {
    Properties *self = allocOrDie(sizeof(Properties), "newProperties");
    self->keys = newListBuffer();
    self->values = newListBuffer();
    return self;
}

char * getProp(Properties *self, const char *key) {
    int i;
    for (i = 0; i < self->keys->len; i++) {
        if (streq(self->keys->buf[i], key)) {
            return strdup(self->values->buf[i]);
        }
    }
    return NULL;
}

char * getProp3(Properties *self, const char *key, char *instead) {
    char *result = getProp(self, key);
    if (NULL == result) {
        return instead;
    } else {
        return result;
    }
}

/**
 * Like getProp, but also removes the association if found
 * Could be made extern if needed.
 */
static char * takeProp(Properties *self, const char *key) {
    int i;
    for (i = 0; i < self->keys->len; i++) {
        if (streq(self->keys->buf[i], key)) {
            takeString(self->keys, i);
            return takeString(self->values, i);
        }
    }
    return NULL;
}

void putProp(Properties *self, const char *key, const char *value) {
    int i;
    for (i = 0; i < self->keys->len; i++) {
        if (streq(self->keys->buf[i], key)) {
            free(self->values->buf[i]);
            self->values->buf[i] = strdup(value);
            return;
        }
    }
    pushString(self->keys, strdup(key));
    pushString(self->values, strdup(value));
}


void loadProps(Properties *self, const char *filename) {
    char *text = getFileText(filename);
    int len = strlen(text);
    char *key = text;
    char *newline;
    char *equals;

    while (key < &text[len]) {
        newline = strchr(key, '\n');
        if (NULL == newline) {
            newline = &text[len];
        } else {
            newline[0] = '\0';
            //XXX getFileText should do newline conversion, so the
            // following shouldn't be necessary
            if (newline > key && '\r' == newline[-1]) {
                newline--;
                newline[0] = '\0';
            }
            if (newline > key && '\\' == newline[-1]) {
                fatalError(subst1("Loading $0", filename),
                           "Line continuation not yet implemented");
            }
        }
        while (' ' == key[0] || '\t' == key[0]) {
            key++;
        }
        if ('#' != key[0] && '\0' != key[0]) {
            equals = strchr(key, '=');
            require(NULL != equals, 
                    subst1("Loading $0", filename),
                    subst1("'=' not found: $0", key));
            equals[0] = '\0';
            putProp(self, key, &equals[1]);
        }
        key = &newline[1];
    }        
}


void saveProps(Properties *self, const char *filename) {
    StringBuffer *sb = newStringBuffer();
    int i;
    for (i = 0; i < self->keys->len; i++) {
        append(sb, subst2("$0=$1\n",
                          self->keys->buf[i],
                          self->values->buf[i]));
    }
    setFileText(filename, freeStringBuffer(sb));
}

/**
 * Could be made extern if needed.
 * Could be made vastly more efficient, but why bother?
 */
static Properties *copyProps(Properties *self) {
    Properties *result = newProperties();
    int i;
    for (i = 0; i < self->keys->len; i++) {
        putProp(result, self->keys->buf[i], self->values->buf[i]);
    }
    return result;
}

void fillInProps(Properties *self,
                 const char *template,
                 const char *filename)
{
    Properties *scratch = copyProps(self);
    const char *before = getFileText(template);
    StringBuffer *after = newStringBuffer();
    const char *rest = before;
    const char *left;
    const char *right;
    const char *name;
    const char *value;
    while (TRUE) {
        left = strstr(rest, "${");
        if (NULL == left) {
            append(after, rest);
            setFileText(filename, freeStringBuffer(after));
            require(0 == scratch->keys->len,
                    subst2("Instantiating $0 into $1",
                           template,
                           filename),
                    subst1("Unconsumed properties: $0",
                           scratch->keys->buf[0]));
            return;
        }
        appendSubstr(after, rest, left - rest);
        left += 2; //strlen("${");
        right = strchr(left, '}');
        require(NULL != right,
                subst2("Instantiating $0 into $1",
                       template,
                       filename),
                "'}' expected");
        name = strndup(left, right - left);
        value = takeProp(scratch, name);
        require(NULL != value,
                subst2("Instantiating $0 into $1",
                       template,
                       filename),
                subst1("Unexpected name: $0",
                       name));
        append(after, name);
        appendChar(after, '=');
        append(after, value);
        rest = right +1; //strlen("}");
    }
}
