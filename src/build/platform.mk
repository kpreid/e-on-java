# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# @author Mark S. Miller

# Sets OSDIR to a name to be used for subdirectories containing files
# specific to the current OS (including window system variations),
# such as "win32" or "linux-motif".  For example, an swt.jar file is
# specific to a given OSDIR. 

# Sets MACHDIR to a name to be used for subdirectories containing
# files specific to a given machine architecture (ie, instruction
# set), such as "x86" or "sparc".  For example, the result of
# compiling a C program is specific at least to a given MACHDIR.

# Sets PLATDIR to "$(OSDIR)/$(MACHDIR)".  A C program itself may be
# specific to a given OSDIR (as the swt native libraries are), so a
# compiled C program may be specific to a given PLATDIR

ifndef UNAME_SYSNAME
 UNAME_SYSNAME := $(shell uname -s)
endif
# In alphabetical order by the OSDIR value
ifndef OSDIR
 ifeq "$(findstring AIX,$(UNAME_SYSNAME))" "AIX"
  OSDIR=aix-motif
 endif
endif
ifndef OSDIR
 ifeq "$(findstring FreeBSD,$(UNAME_SYSNAME))" "FreeBSD"
  # FreeBSD not yet specially supported.
  # Treating this as a generic 'posix' platform.
  # OSDIR=freebsd
  OSDIR=posix
 endif
endif
ifndef OSDIR
 ifeq "$(findstring Linux,$(UNAME_SYSNAME))" "Linux"
  # Linux defaults to the OSDIR "linux-motif", but you can try
  # "linux-gtk" if you wish.
  OSDIR=linux-motif
 endif
endif
ifndef OSDIR
 ifeq "$(findstring Mac OS X,$(UNAME_SYSNAME))" "Mac OS X"
  OSDIR=mac
 endif
endif
ifndef OSDIR
 ifeq "$(findstring Darwin,$(UNAME_SYSNAME))" "Darwin"
  OSDIR=mac
 endif
endif
ifndef OSDIR
 ifeq "$(findstring QNX,$(UNAME_SYSNAME))" "QNX"
  OSDIR=qnx
 endif
endif
ifndef OSDIR
 ifeq "$(findstring Solaris,$(UNAME_SYSNAME))" "Solaris"
  OSDIR=solaris-motif
 endif
endif
ifndef OSDIR
 ifeq "$(findstring SunOS,$(UNAME_SYSNAME))" "SunOS"
  OSDIR=solaris-motif
 endif
endif
ifndef OSDIR
 ifeq "$(findstring CYGWIN,$(UNAME_SYSNAME))" "CYGWIN"
  OSDIR=win32
 endif
endif
ifndef OSDIR
 ifeq "$(findstring MINGW32,$(UNAME_SYSNAME))" "MINGW32"
  OSDIR=win32
 endif
endif
ifndef OSDIR
 # Unrecognized OS: $(UNAME_SYSNAME), assuming POSIX.
 # Treating this as a generic 'posix' platform.
 OSDIR=posix
endif


ifndef UNAME_MACHINE
 UNAME_MACHINE := "$(shell uname -m):$(shell uname -p)"
endif
# In alphabetical order by the MACHDIR value
ifndef MACHDIR
 ifeq "$(findstring arm,$(UNAME_MACHINE))" "arm"
  MACHDIR=arm
 endif
endif
ifndef MACHDIR
 ifeq "$(findstring ppc,$(UNAME_MACHINE))" "ppc"
  MACHDIR=ppc
 endif
endif
ifndef MACHDIR
 ifeq "$(findstring Power Macintosh,$(UNAME_MACHINE))" "Power Macintosh"
  MACHDIR=ppc
 endif
endif
ifndef MACHDIR
 ifeq "$(findstring sparc,$(UNAME_MACHINE))" "sparc"
  MACHDIR=sparc
 endif
endif
ifndef MACHDIR
 ifeq "$(findstring 86,$(UNAME_MACHINE))" "86"
  ifeq "$(findstring _64,$(UNAME_MACHINE))" "_64"
   MACHDIR=x86_64
  else
   MACHDIR=x86
  endif
 endif
endif
ifndef MACHDIR
 # Unrecognized machine: $(UNAME_MACHINE).
 # Treating this as a generic 'unknown' machine type.
 MACHDIR=unknown
endif

ifndef PLATDIR
 PLATDIR="$(OSDIR)/$(MACHDIR)"
endif
