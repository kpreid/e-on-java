// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// setuputil.c - some stuff for supporting E installation on Windows

#include <malloc.h>
#include <string.h>
#include <stdio.h>

#include "error_utils.h"
#include "string_utils.h"
#include "type_utils.h"


char *strndup(const char *strSource, size_t n) {
    int len = strlen(strSource);
    char * result = NULL;
    if (n > len) {
        n = len;
    }
    result = allocOrDie(n+1, "in strndup");
    strncpy(result, strSource, n);
    result[n] = '\0';
    return result;
}

BOOL streq(const char *a, const char *b) {
    return strcmp(a, b) == 0;
}

BOOL strieq(const char *a, const char *b) {
    return strcasecmp(a, b) == 0;
}

char *printeger(int value) {
    char buf[20]; // more than big enough for any integer
    int len = sprintf(buf, "%d", value);
    require(len <= 19, "Memory overflow", "unexpectedly wide integer");
    return strdup(buf);
}

StringBuffer * newStringBuffer() {
    StringBuffer *result = allocOrDie(sizeof(StringBuffer), "new sb");
    // if realloc worked, we'd be asking for 1 byte instead
    result->allocLen = 8;
    result->buf = allocOrDie(result->allocLen, "sb init");

    result->len = 0;
    result->buf[0] = '\0';
    return result;
}

char * freeStringBuffer(StringBuffer *self) {
    char *result = self->buf;
    free(self);
    return result;
}

//exists only because realloc is broken
static void growStringBuffer(StringBuffer *self, int newLen) {
    char * newBuf = NULL;
    if (newLen <= self->allocLen) {
        return;
    }
    newLen *= 2;
    newBuf = allocOrDie(newLen, "growing sb");
    strcpy(newBuf, self->buf);
    free(self->buf);
    self->buf = newBuf;
    self->allocLen = newLen;
}


void appendSubstr(StringBuffer *self, const char *str, int n) {
    require(n <= strlen(str),
            "Internal: String Handling",
            "Past the end of the string");
    growStringBuffer(self, self->len + n + 1);
    strncpy(&self->buf[self->len], str, n);
    self->len += n;
    self->buf[self->len] = '\0';
}


void append(StringBuffer *self, const char *str) {
    if (NULL != str) {
        //NULL is treated like ""
        appendSubstr(self, str, strlen(str));
    }
}

void appendChar(StringBuffer *self, char c) {
    char buf[2];
    buf[0] = c;
    buf[1] = '\0';
    appendSubstr(self, buf, 1);
}

//XXX escaping logic incomplete
void appendQuoted(StringBuffer *self, const char *str) {
    int i;
    appendChar(self, '\"');
    for (i = 0; str[i] != '\0'; i++) {
        switch (str[i]) {
            case '\"': {
                append(self, "\\\"");
                break;
            }
            case '\\': {
                append(self, "\\\\");
                break;
            }
            default: {
                appendChar(self, str[i]);
                break;
            }
        }
    }
    appendChar(self, '\"');
}

void appendQuotedArgs(StringBuffer *self, int argc, char *argv[]) {
    int i;
    if (argc <= 0) {
        return;
    }
    appendQuoted(self, argv[0]);
    for (i = 1; i < argc; i++) {
        appendChar(self, ' ');
        appendQuoted(self, argv[i]);
    }
}
                
char * quotedArgs(int argc, char *argv[]) {
    StringBuffer *result = newStringBuffer();
    appendQuotedArgs(result, argc, argv);
    return freeStringBuffer(result);
}



ListBuffer * newListBuffer() {
    ListBuffer *result = allocOrDie(sizeof(ListBuffer), "new lb");
    // if realloc worked, we'd be asking for 1 pointer instead
    result->allocLen = 8;
    result->buf = allocOrDie(sizeof(char*) * result->allocLen, "lb init");

    result->len = 0;
    result->buf[0] = NULL;
    return result;
}

char ** freeListBuffer(ListBuffer *self) {
    char **result = self->buf;
    free(self);
    return result;
}

//exists only because realloc is broken
static void growListBuffer(ListBuffer *self, int newLen) {
    char ** newBuf = NULL;
    int i;
    if (newLen <= self->allocLen) {
        return;
    }
    newLen *= 2;
    newBuf = allocOrDie(sizeof(char*) * newLen, "growing lb");
    for (i = 0; i <= self->len; i++) {
        // '<=' above so we'll copy the NULL at self->buf[self->len] as well
        newBuf[i] = self->buf[i];
    }
    free(self->buf);
    self->buf = newBuf;
    self->allocLen = newLen;
}


void pushString(ListBuffer *self, const char *str) {
    // + 2 to include both the new string and the NULL
    growListBuffer(self, self->len + 2);
    self->buf[self->len] = strdup(str);
    self->len++;
    self->buf[self->len] = NULL;
}

void pushArgs(ListBuffer *self, int argc, char *argv[]) {
    int i;
    for (i = 0; i < argc; i++) {
        pushString(self, argv[i]);
    }
}

void pushUnique(ListBuffer *self, const char *str) {
    int i;
    for (i = 0; i < self->len; i++) {
        if (strieq(self->buf[i], str)) {
            return;
        }
    }
    pushString(self, str);
}

char * takeString(ListBuffer *self, int index) {
    char *result;
    require(0 <= index && index < self->len,
            "Internal: ListBuffer",
            subst2("$0 must be less than $1",
                   printeger(index),
                   printeger(self->len)));

    result = self->buf[index];
    self->len--; //len now indexes the old last position
    //works whether index is last or not
    self->buf[index] = self->buf[self->len];
    self->buf[self->len] = NULL;
    return result;
}

char * substitute(const char *template, int argc, const char *argv[]) {
    StringBuffer *sb = newStringBuffer();
    const char *rest = template;
    const char *next;
    char c;
    int argi;

    while (TRUE) {
        next = strchr(rest, '$');
        if (NULL == next) {
            append(sb, rest);
            return freeStringBuffer(sb);
        }
        appendSubstr(sb, rest, next - rest);
        c = next[1];
        rest = next + 2;
        if ('$' == c) {
            appendChar(sb, '$');
        } else if ('0' <= c && c <= '9') {
            argi = c - '0';
            require(argi < argc,
                    "Internal: String Substitution",
                    "Index too large");
            append(sb, argv[argi]);
        } else if ('\0' == c) {
            fatalError("Internal: String Substitution",
                       "EOS in midst of interpolating");
        } else {
            fatalError("Internal: String Substitution",
                       "Invalid interpolation");
        }
    }
}

char * subst1(const char *template,
              const char *arg0)
{
    const char *argv[1];
    argv[0] = arg0;
    return substitute(template, 1, argv);
}

char * subst2(const char *template,
              const char *arg0,
              const char *arg1)
{
    const char *argv[2];
    argv[0] = arg0;
    argv[1] = arg1;
    return substitute(template, 2, argv);
}

char * subst3(const char *template,
              const char *arg0,
              const char *arg1,
              const char *arg2)
{
    const char *argv[3];
    argv[0] = arg0;
    argv[1] = arg1;
    argv[2] = arg2;
    return substitute(template, 3, argv);
}


char *replaceAll(const char *template,
                 const char *from,
                 const char *to)
{
    StringBuffer *sb = newStringBuffer();
    const char *rest = template;
    int fromlen = strlen(from);
    const char *next;
    while (TRUE) {
        next = strstr(rest, from);
        if (NULL == next) {
            append(sb, rest);
            return freeStringBuffer(sb);
        }
        appendSubstr(sb, rest, next - rest);
        append(sb, to);
        rest = next + fromlen;
    }
}



int valOfName(char *name, NamedVal *table) {
    for (; NULL != table->name; table++) {
        if (streq(table->name, name)) {
            return table->val;
        }
    }
    fatalError("Unrecognized name", name);
    return -1; //make compiler happy
}

void showNamedValTable(NamedVal *table) {
    for (; NULL != table->name; table++) {
        printf("%s: %d\n", table->name, table->val);
    }
}
