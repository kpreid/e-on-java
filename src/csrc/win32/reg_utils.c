/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* reg_utils.c - for handling the Win32 Registry */

#include <stdio.h>
#include <windows.h>

#include "error_utils.h"
#include "string_utils.h"
#include "file_utils.h"
#include "reg_utils.h"


/* todo: make this adaptive */
#define MAX_BUFSIZE 10000

HKEY CreateKey(HKEY root,
               const char *subKey)
{
    HRESULT hres;
    HKEY hKey;
    DWORD disposition;

    /* create or open the specified key */
    hres = RegCreateKeyEx(root,                 /* already open key */
                          subKey,               /* name of subkey */
                          0,                    /* reserved */
                          NULL,                 /* class name */
                          REG_OPTION_NON_VOLATILE, /* options */
                          KEY_ALL_ACCESS,       /* desired access */
                          NULL,                 /* security attributes */
                          &hKey,                /* result goes here */
                          &disposition);        /* was it created or not? */

    if (ERROR_SUCCESS != hres) {
        mustSucceed(hres, subst1("Couldn't open/create $0", subKey));
    }
    return hKey;
}

HKEY OpenKey(HKEY root,
             const char *subKey)
{
    HKEY hKey = 0;
    HRESULT hres = 0;

    hres = RegOpenKeyEx(root,           /* already open key */
                        subKey,         /* name of subkey to open */
                        0,              /* reserved */
                        KEY_ALL_ACCESS, /* access mask */
                        &hKey);         /* result goes here */

    if (ERROR_SUCCESS == hres) {
        return hKey;
    } else if (ERROR_FILE_NOT_FOUND == hres) {
        return NULL;
    } else {
        mustSucceed(hres, subst1("Opening $0 for reading", subKey));
        return NULL; /*keep compiler happy */
    }
}

char ** EnumSubKeys(HKEY root) {
    char ** result;
    DWORD numKeys;
    HRESULT hres;
    int i;
    char buf[MAX_BUFSIZE];
    DWORD bufSizeNeeded = MAX_BUFSIZE;

    hres = RegQueryInfoKey(root,                /* open key to enumerate */
                           NULL,                /* class name */
                           NULL,                /* class name size */
                           NULL,                /* reserved */
                           &numKeys,            /* how many subKeys? */
                           NULL,                /* max subKey length */
                           NULL,                /* max class length */
                           NULL,                /* how many values? */
                           NULL,                /* max value name length */
                           NULL,                /* max value data length */
                           NULL,                /* security description */
                           NULL);               /* last write time */
    mustSucceed(hres, "Querying key for subKeys");
    result = (char **)allocOrDie((1 + numKeys) * sizeof(char *),
                                 "Enumerating keys");
    for (i = 0; i < numKeys; i++) {
        hres = RegEnumKeyEx(root,               /* open key */
                            i,                  /* subKey index */
                            buf,                /* subKey name */
                            &bufSizeNeeded,     /* size of subKey name */
                            NULL,               /* reserved */
                            NULL,               /* class name */
                            NULL,               /* class name size */
                            NULL);              /* last write time */
        mustSucceed(hres, "Enumerating a subkey");
        result[i] = strdup(buf);
    }
    result[numKeys] = NULL;
    return result;
}
    
void CloseKey(HKEY root) {
    mustSucceed(RegCloseKey(root), "Can't close key");
}

void DeleteKey(HKEY root, const char *subKey) {
    /* On Win9x RegDeleteKey() deletes recursively, but on WinNT it */
    /* only deletes if the key has no sub keys, so I have to do the */
    /* recursive delete myself. */

    HKEY sub = OpenKey(root, subKey);
    char **subsubs;
    int i;

    if (NULL == sub) {
        return;
    }
    subsubs = EnumSubKeys(sub);
    for (i = 0; NULL != subsubs[i]; i++) {
        DeleteKey(sub, subsubs[i]);
    }
    CloseKey(sub);
    mustSucceed(RegDeleteKey(root, subKey),
                subst1("Can't delete key $0", subKey));
}




void SetStringValue(HKEY root,
                    const char *optSubKey,
                    const char *valueName,
                    const char *value)
{
    HRESULT hres;
    unsigned const char *val = (unsigned const char *)value;

    if (NULL != optSubKey) {
        root = CreateKey(root, optSubKey);
    }
    hres = RegSetValueEx(root,          /* open key to set value for   */
                         valueName,     /* name of value to set  */
                         0,             /* reserved  */
                         REG_SZ,        /* flag for value type  */
                         val,           /* address of value data  */
                         strlen(value)+1); /* size of value data  */
    if (NULL != optSubKey) {
        CloseKey(root);
    }
    mustSucceed(hres, "Setting a Registry string value");
}

void SetDWORDValue(HKEY root,
                   const char *optSubKey,
                   const char *valueName,
                   DWORD value)
{
    HRESULT hres;
    unsigned const char *val = (unsigned const char *)&value;

    if (NULL != optSubKey) {
        root = CreateKey(root, optSubKey);
    }
    hres = RegSetValueEx(root,          /* open key to set value for   */
                         valueName,     /* name of value to set  */
                         0,             /* reserved  */
                         REG_DWORD,     /* flag for value type  */
                         val,           /* address of value data  */
                         sizeof(DWORD)); /* size of value data  */
    if (NULL != optSubKey) {
        CloseKey(root);
    }
    mustSucceed(hres, "Setting a Registry DWORD value");
}

DWORD GetValueType(HKEY root,
                   const char *optSubKey,
                   const char *valueName)
{
    DWORD type;
    HRESULT hres;
    
    if (NULL != optSubKey) {
        root = OpenKey(root, optSubKey);
    }
    hres = RegQueryValueEx(root,                /* open key to get from */
                           valueName,           /* name of value to get */
                           0,                   /* reserved */
                           &type,               /* gets result type */
                           NULL,                /* gets result data */
                           NULL);               /* size of result buffer */
    if (NULL != optSubKey) {
        CloseKey(root);
    }
    if (ERROR_SUCCESS == hres) {
        return type;
    } else if (ERROR_FILE_NOT_FOUND == hres) {
        return REG_ABSENT;
    } else {
        mustSucceed(hres, subst1("Couldn't find $0 in Registry",
                                 valueName));
        return 666; /*keep compiler happy */
    }
}


char * GetStringValue(HKEY root,
                      const char *optSubKey,
                      const char *valueName)
{
    char res[MAX_BUFSIZE];
    unsigned char *resPtr = (unsigned char *)&res;
    DWORD bufSizeNeeded = MAX_BUFSIZE;
    DWORD type;
    HRESULT hres;
    char *result;

    if (NULL != optSubKey) {
        root = OpenKey(root, optSubKey);
    }
    hres = RegQueryValueEx(root,                /* open key to get from */
                           valueName,           /* name of value to get */
                           0,                   /* reserved */
                           &type,               /* gets result type */
                           resPtr,              /* gets result data */
                           &bufSizeNeeded);     /* size of result buffer */
    if (NULL != optSubKey) {
        CloseKey(root);
    }
    if (ERROR_SUCCESS != hres) {
        mustSucceed(hres, subst1("Couldn't find $0 in Registry",
                                 valueName));
    }
    require(REG_SZ == type,
            "Registry: Not a string",
            valueName);
    result = strdup(res);
    require(NULL != result,
            "Memory: out of room",
            valueName);
    return result;
}

DWORD GetDWORDValue(HKEY root,
                    const char *optSubKey,
                    const char *valueName)
{
    DWORD res;
    unsigned char *resPtr = (unsigned char *)&res;
    DWORD bufSizeNeeded = sizeof(res);
    DWORD type;
    HRESULT hres;

    if (NULL != optSubKey) {
        root = OpenKey(root, optSubKey);
    }
    hres = RegQueryValueEx(root,                /* open key to get from */
                           valueName,           /* name of value to get */
                           0,                   /* reserved  */
                           &type,               /* gets result type  */
                           resPtr,              /* gets result data */
                           &bufSizeNeeded);     /* size of result buffer */
    if (NULL != optSubKey) {
        CloseKey(root);
    }
    if (ERROR_SUCCESS != hres) {
        mustSucceed(hres, subst1("Couldn't find $0 in Registry",
                                 valueName));
    }
    require(REG_DWORD == type,
            "Registry: Not a DWORD",
            valueName);
    return hres;
}


char ** EnumValueNames(HKEY root) {
    char ** result;
    DWORD numValues;
    HRESULT hres;
    int i;
    char buf[MAX_BUFSIZE];
    DWORD bufSizeNeeded = MAX_BUFSIZE;

    hres = RegQueryInfoKey(root,                /* open key to enumerate */
                           NULL,                /* class name */
                           NULL,                /* class name size */
                           NULL,                /* reserved */
                           NULL,                /* how many subKeys? */
                           NULL,                /* max subKey length */
                           NULL,                /* max class length */
                           &numValues,          /* how many values? */
                           NULL,                /* max value name length */
                           NULL,                /* max value data length */
                           NULL,                /* security description */
                           NULL);               /* last write time */
    mustSucceed(hres, "Querying key for values");
    result = (char **)allocOrDie((1 + numValues) * sizeof(char *),
                                 "Enumerating values");
    for (i = 0; i < numValues; i++) {
        hres = RegEnumValue(root,               /* open key */
                            i,                  /* value index */
                            buf,                /* value name */
                            &bufSizeNeeded,     /* size of value name */
                            NULL,               /* reserved */
                            NULL,               /* gets type code */
                            NULL,               /* gets data */
                            NULL);              /* size of data */
        mustSucceed(hres, "Enumerating a value");
        result[i] = strdup(buf);
    }
    result[numValues] = NULL;
    return result;
}

void DeleteValue(HKEY root,
                 const char *optSubKey,
                 const char *valueName)
{
    if (NULL != optSubKey) {
        root = OpenKey(root, optSubKey);
    }
    if (NULL == root) {
        return;
    }
    mustSucceed(RegDeleteValue(root, valueName), "Can't delete value");
    if (NULL != optSubKey) {
        CloseKey(root);
    }
}

void FileAssoc(const char *ext,                 /* .e */
               const char *type,                /* e-script */
               const char *mime,                /* text/x-escript */
               const char *desc,                /* E Script */
               const char *iconFile OPTIONAL,   /* c:/.../e-doc.ico */
               int iconIndex,                   /* 0 */
               const char *quickView OPTIONAL)  /* *   */
{
    HKEY typeKey;
    char *iconName;

    SetStringValue(HKCR, ext, "", type);
    SetStringValue(HKCR, ext, "Content Type", mime);
    SetStringValue(HKCR, 
                   subst1("MIME\\Database\\Content Type\\$0", mime),
                   "Extension",
                   ext);

    typeKey = CreateKey(HKCR, type);
    SetStringValue(typeKey, NULL, "", desc);

    if (NULL != iconFile) {
        iconFile = replaceAll(normalizePath(iconFile), "/", "\\");
        iconName = subst2("\"$0\",$1",
                          iconFile,
                          printeger(iconIndex));
        SetStringValue(typeKey, "DefaultIcon", "", iconName);
    }
    if (NULL != quickView) {
        SetStringValue(typeKey, "QuickView", "", quickView);
    }
    CloseKey(typeKey);
}

void AddCommand(const char *type,       /* e-script */
                const char *menuName,   /* run */
                BOOL isDefault,         /* TRUE */
                int argc,
                char *argv[])           /* ["c:/../java.exe", "%1"] */
{
    HKEY typeKey = CreateKey(HKCR, type);

    if (isDefault) {
        SetStringValue(typeKey, "shell", "", menuName);
    }
    if (argc >= 1) {
        argv[0] = replaceAll(normalizePath(argv[0]), "/", "\\");
    }
    SetStringValue(typeKey, 
                   subst1("shell\\$0\\command", menuName),
                   "",
                   quotedArgs(argc, argv));
    CloseKey(typeKey);
}    
