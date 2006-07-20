/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* reg_utils.h - for handling the Win32 Registry */

#ifndef __REG_UTILS
#define __REG_UTILS

#include <windows.h>

#define HKCR HKEY_CLASSES_ROOT
#define HKCU HKEY_CURRENT_USER
#define HKLM HKEY_LOCAL_MACHINE
#define HKU  HKEY_USERS
#define HKCC HKEY_CURRENT_CONFIG
#define HKDD HKEY_DYN_DATA

/*********************** subkeys *****************************/


/** Open or create a subkey of a key */
extern HKEY CreateKey(HKEY root,
                      const char *subKey);

/** Open a subKey, or return NULL if it isn't there. */
extern HKEY OpenKey(HKEY root,
                    const char *subKey);

/**
 * Return the only pointer to a newly allocated NULL terminated array
 * of the only pointers to newly allocated subKey names
 */
extern char ** EnumSubKeys(HKEY root);

/** Closes an open key */
extern void CloseKey(HKEY root);

/** Deletes a key, even if it has subKeys. */
extern void DeleteKey(HKEY root,
                      const char *subKey);


/*********************** values *****************************/


/** Set a string value on a key */
extern void SetStringValue(HKEY root,
                           const char *optSubKey,
                           const char *valueName,
                           const char *value);

/** Set a DWORD value on a key */
extern void SetDWORDValue(HKEY root,
                          const char *optSubKey,
                          const char *valueName,
                          DWORD value);

#define REG_ABSENT (-1)

/**
 * One of REG_* (defined in winnt.h) or REG_ABSENT if missing.
 */
extern DWORD GetValueType(HKEY root,
                          const char *optSubKey,
                          const char *valueName);

/**
 * Retrieve a string from the registry. Returns the only pointer to a
 * newly allocated string.
 */
extern char * GetStringValue(HKEY root,
                             const char *optSubKey,
                             const char *valueName);

/** Retrieve a DWORD from the registry */
extern DWORD GetDWORDValue(HKEY root,
                           const char *optSubKey,
                           const char *valueName);

/**
 * Return the only pointer to a newly allocated NULL terminated array
 * of the only pointers to newly allocated value names
 */
extern char ** EnumValueNames(HKEY root);

/** Deletes a value */
extern void DeleteValue(HKEY root,
                        const char *optSubKey,
                        const char *valueName);


/*********************** file extension associations **********************/


/**
 * Makes the extension launchable and visible with icon.
 * iconFile or quickView can be NULL.
 */
extern void FileAssoc(const char *ext,                  /* .e */
                      const char *type,                 /* e-script */
                      const char *desc,                 /* E Script */
                      const char *mime,                 /* text/x-escript */
                      const char *iconFile OPTIONAL,    /* c:/.../e-doc.ico */
                      int iconIndex,                    /* 0 */
                      const char * quickView OPTIONAL); /* * */

/**
 * Makes extensions of this type launchable with this command.
 * <p>
 * Note that argv[0] is modified in place.
 */
extern void AddCommand(const char *type,      /* e-script */
                       const char *menuName,  /* run */
                       BOOL isDefault,        /* TRUE */
                       int argc,
                       char *argv[]);         /* ["c:/../java.exe", ..,"%1"] */

#endif /* __REG_UTILS */
