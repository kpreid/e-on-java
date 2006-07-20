/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* shortcut_utils.c - manipulating Windows shortcuts (*.lnk files) */

#include <windows.h>
#include <shellapi.h>
#include <shlobj.h>

#include "error_utils.h"
#include "string_utils.h"
#include "file_utils.h"
#include "shortcut_utils.h"

/**
 * Converts from ascii to unicode. Returns the only pointer to a
 * newly allocated unicode (wide) string.
 */
static WORD * ascii2wide(const char *str) {
    int numWide;
    WORD *result;

    numWide = MultiByteToWideChar(CP_ACP,       /* code page */
                                  0,            /* chat-type options */
                                  str,          /* string to convert */
                                  -1,           /* size of str */
                                  NULL,         /* gets the unicode string */
                                  0);           /* What is the unicode size? */

    result = allocOrDie(sizeof(WORD) * numWide,
                        "Converting to unicode");

    numWide = MultiByteToWideChar(CP_ACP,       /* code page */
                                  0,            /* chat-type options */
                                  str,          /* string to convert */
                                  -1,           /* size of str */
                                  result,       /* gets the unicode string */
                                  numWide);     /* size of result */
    return result;
}


void makeShortcut(const char *linkPath,
                  const char *cwd OPTIONAL,
                  const char *desc OPTIONAL,
                  int         showCmd OPTIONAL,
                  const char *iconFile OPTIONAL,
                  int         iconIndex OPTIONAL,
                  const char *targetPath,
                  int         argc,
                  char       *argv[])
{
    IShellLink *psl;
    IPersistFile *ppf;
    char *args = NULL;

    targetPath = replaceAll(normalizePath(targetPath), "/", "\\");
    if (argc >= 1) {
        args = quotedArgs(argc, argv);
    }

    mustSucceed(CoInitialize(NULL), "CoInitialize()ing");

    /* Get a pointer to the IShellLink interface. */
    mustSucceed(CoCreateInstance(&CLSID_ShellLink,      /* class id */
                                 NULL,                  /* out aggregate */
                                 CLSCTX_INPROC_SERVER,  /* exec context */
                                 &IID_IShellLink,       /* interface id */
                                 (PVOID*)&psl),         /* gets the interface */
                "Can't get IShellLink interface");

    /* Set the path to the shortcut target, and add the  */
    /* rest */

    mustSucceed(psl->lpVtbl->SetPath(psl, targetPath),
                subst1("Setting shortcut target to \"$0\"", targetPath));

    if (NULL != args) {
        mustSucceed(psl->lpVtbl->SetArguments(psl, args),
                    subst1("Setting shortcut args to $0", args));
    }
    if (NULL != cwd) {
        cwd = replaceAll(normalizePath(cwd), "/", "\\");
        mustSucceed(psl->lpVtbl->SetWorkingDirectory(psl, cwd),
                    subst1("Setting shortcut working dir to $0", cwd));
    }
    if (NULL != desc) {
        mustSucceed(psl->lpVtbl->SetDescription(psl, desc),
                    subst1("Setting shortcut description to $0", desc));
    }
    if (-1 != showCmd) {
        mustSucceed(psl->lpVtbl->SetShowCmd(psl, showCmd),
                    "Setting shortcut show cmd");
    }
    if (NULL != iconFile) {
        iconFile = replaceAll(normalizePath(iconFile), "/", "\\");
        mustSucceed(psl->lpVtbl->SetIconLocation(psl,
                                                 iconFile,
                                                 iconIndex),
                    subst1("Setting shortcut icon to $0", iconFile));
    }

    /* Query IShellLink for the IPersistFile interface for */
    /* saving the shortcut in persistent storage. */
    mustSucceed(psl->lpVtbl->QueryInterface(psl, &IID_IPersistFile,
                                            (PVOID*)&ppf),
                "Can't get IPersist interface");

    /* Save the link by calling IPersistFile::Save. */
    mustSucceed(ppf->lpVtbl->Save(ppf, ascii2wide(linkPath), TRUE),
                subst1("Saving shortcut to $0", linkPath));
    ppf->lpVtbl->Release(ppf);
    psl->lpVtbl->Release(psl);

    CoUninitialize();
}
