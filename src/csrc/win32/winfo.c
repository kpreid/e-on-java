/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* winfo.c - what are the special folders? */

#include <stdio.h>
#include "winfo_utils.h"

int main(int argc, char *argv[]) {
    dumpWinfo(stdout);
    return 0;
}

/***
 * on my win98 thinkpad laptop:

             ALTSTARTUP - not
                APPDATA   C:\WINDOWS\Application Data
              BITBUCKET - not
      COMMON_ALTSTARTUP - not
COMMON_DESKTOPDIRECTORY - not
       COMMON_FAVORITES - not
        COMMON_PROGRAMS - not
       COMMON_STARTMENU - not
         COMMON_STARTUP - not
               CONTROLS - not
                COOKIES   C:\WINDOWS\Cookies
                DESKTOP   C:\WINDOWS\Desktop
       DESKTOPDIRECTORY   C:\WINDOWS\Desktop
                 DRIVES - not
              FAVORITES   C:\WINDOWS\Favorites
                  FONTS   C:\WINDOWS\FONTS
                HISTORY   C:\WINDOWS\History
               INTERNET - not
         INTERNET_CACHE   C:\WINDOWS\Temporary Internet Files
                NETHOOD   C:\WINDOWS\NetHood
                NETWORK - not
               PERSONAL   C:\My Documents
               PRINTERS - not
              PRINTHOOD   C:\WINDOWS\PrintHood
               PROGRAMS   C:\WINDOWS\Start Menu\Programs
                 RECENT   C:\WINDOWS\Recent
                 SENDTO   C:\WINDOWS\SendTo
              STARTMENU   C:\WINDOWS\Start Menu
                STARTUP   C:\WINDOWS\Start Menu\Programs\StartUp
              TEMPLATES   C:\WINDOWS\ShellNew
***/
