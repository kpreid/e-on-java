/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

#include <windows.h>
#include <shlobj.h>
#include <lmcons.h>

#include "file_utils.h"
#include "winfo_utils.h"
#include "error_utils.h"

char * getUserName() {
    char buf[UNLEN+1];
    DWORD len = UNLEN+1;

    require(GetUserName(buf, &len) != 0,
            "Internal: GetUserName failed",
            "returned a zero");
    return strdup(buf);
}

char * getWindowsDir() {
    char buf[MAX_PATH];

    require(GetWindowsDirectory(buf, MAX_PATH) <= MAX_PATH,
            "Internal: Path too large",
            "Getting Windows Directory");
    return normalizePath(buf);
}

char * getSystemDir() {
    char buf[MAX_PATH];

    require(GetSystemDirectory(buf, MAX_PATH) <= MAX_PATH,
            "Internal: Path too large",
            "Getting System Directory");
    return normalizePath(buf);
}

char * getCurrentDir() {
    char buf[MAX_PATH];

    /* yes, GetCurrentDirectory & GetTempPath (below) really does have */
    /* the opposite param order from GetWindowsDirectory and */
    /* GetSystemDirectory */
    require(GetCurrentDirectory(MAX_PATH, buf) <= MAX_PATH,
            "Internal: Path too large",
            "Getting Current Directory");
    return normalizePath(buf);
}

char * getTempDir() {
    char buf[MAX_PATH+1];

    require(GetTempPath(MAX_PATH+1, buf) <= MAX_PATH+1,
            "Internal: Path too large",
            "Getting Temp Directory");
    return normalizePath(buf);
}

NamedVal CSIDLs[] = {
    { "ALTSTARTUP", CSIDL_ALTSTARTUP },
    { "APPDATA", CSIDL_APPDATA },
    { "BITBUCKET", CSIDL_BITBUCKET },
    { "COMMON_ALTSTARTUP", CSIDL_COMMON_ALTSTARTUP },
    { "COMMON_DESKTOPDIRECTORY", CSIDL_COMMON_DESKTOPDIRECTORY },
    { "COMMON_FAVORITES", CSIDL_COMMON_FAVORITES },
    { "COMMON_PROGRAMS", CSIDL_COMMON_PROGRAMS },
    { "COMMON_STARTMENU", CSIDL_COMMON_STARTMENU },
    { "COMMON_STARTUP", CSIDL_COMMON_STARTUP },
    { "CONTROLS", CSIDL_CONTROLS },
    { "COOKIES", CSIDL_COOKIES },
    { "DESKTOP", CSIDL_DESKTOP },
    { "DESKTOPDIRECTORY", CSIDL_DESKTOPDIRECTORY },
    { "DRIVES", CSIDL_DRIVES },
    { "FAVORITES", CSIDL_FAVORITES },
    { "FONTS", CSIDL_FONTS },
    { "HISTORY", CSIDL_HISTORY },
    { "INTERNET", CSIDL_INTERNET },
    { "INTERNET_CACHE", CSIDL_INTERNET_CACHE },
    { "NETHOOD", CSIDL_NETHOOD },
    { "NETWORK", CSIDL_NETWORK },
    { "PERSONAL", CSIDL_PERSONAL },
    { "PRINTERS", CSIDL_PRINTERS },
    { "PRINTHOOD", CSIDL_PRINTHOOD },
    { "PROGRAMS", CSIDL_PROGRAMS },
    { "RECENT", CSIDL_RECENT },
    { "SENDTO", CSIDL_SENDTO },
    { "STARTMENU", CSIDL_STARTMENU },
    { "STARTUP", CSIDL_STARTUP },
    { "TEMPLATES", CSIDL_TEMPLATES },
    { NULL,                     -1 },
};

char * getSpecialPath(int nFolder) {
    char buf[MAX_PATH];
    HRESULT hres;
    LPITEMIDLIST pidl;

    /* we're using SHGetSpecialFolderLocation instead of the simpler */
    /* SHGetSpecialFolderPath since the latter seems buggy. */
    hres = SHGetSpecialFolderLocation(NULL,     /* owner window */
                                      nFolder,  /* which dir */
                                      &pidl);   /* get itemid list */

    if (ERROR_SUCCESS == hres && SHGetPathFromIDList(pidl, buf)) {
        return normalizePath(buf);
    } else {
        return NULL;
    }
}

void dumpWinfo(FILE *f) {
    int i;
    char **pathPath;
    char *path;
    OSVERSIONINFO vInfo;

    fprintf(f, "e.winfo.user=%s\n",             getUserName());
    fprintf(f, "e.winfo.windowsdir=%s\n",       getWindowsDir());
    fprintf(f, "e.winfo.systemdir=%s\n",        getSystemDir());
    fprintf(f, "e.winfo.currentdir=%s\n",       getCurrentDir());
    fprintf(f, "e.winfo.tempdir=%s\n",          getTempDir());
    pathPath = getPATH();
    fprintf(f, "e.winfo.pathlist=");
    if (NULL != pathPath[0]) {
        fprintf(f, "%s", pathPath[0]);
        for (i = 1; NULL != pathPath[i]; i++) {
            fprintf(f, ";%s", pathPath[i]);
        }
    }
    fprintf(f, "\n\n");

    vInfo.dwOSVersionInfoSize = sizeof(vInfo);

    require(GetVersionEx(&vInfo),
            "GetVerionEx failed",
            printeger(GetLastError()));
    /*This is weird. In the cygwin library, DWORD is defined as a */
    /*unsigned long. The MS win32 docs define it as a 32 bit */
    /*unsigned. Perhaps a cygwin long is 32 bits? */
    fprintf(f, "e.winfo.MajorVersion=%lu\n",    vInfo.dwMajorVersion);
    fprintf(f, "e.winfo.MinorVersion=%lu\n",    vInfo.dwMinorVersion);
    fprintf(f, "e.winfo.BuildNumber=%lu\n",     vInfo.dwBuildNumber);
    fprintf(f, "e.winfo.PlatformId=%lu\n",      vInfo.dwPlatformId);
    fprintf(f, "e.winfo.CSDVersion=%s\n",       vInfo.szCSDVersion);

    fprintf(f, "\n");
    for (i = 0; NULL != CSIDLs[i].name; i++) {
        path = getSpecialPath(CSIDLs[i].val);
        if (NULL != path) {
            fprintf(f, "e.winfo.%s=%s\n", CSIDLs[i].name, path);
        }
        free(path);
    }
    fprintf(f, "\n");
}

