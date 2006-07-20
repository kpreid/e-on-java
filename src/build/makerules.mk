# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# This file is derived with permission from the EROS makerules.mk.  It
# has been heavily modified since then.

# If the following `which bash` doesn't work, then manually set the SHELL
# variable to the location of bash on your system, perhaps by uncommenting
# out one of the other SHELL definitions below.
SHELL := $(shell which bash)
# SHELL=/bin/bash
# SHELL=/usr/local/bin/bash


include $(TOP)/src/build/platform.mk


# NON-STANDARD MAKE VARIABLES AND THEIR USAGE:

# TOP         the top of the build tree.  The directory containing this
#             file is $(TOP)/src/build/.
#
ifndef TOP
TOP=$(HOME)/e
endif

# DIRS        Subdirectories to be built, in left to right order.  All
#             subdirectories are built before the current directory.
#
# CLEANLIST   Additional files that should be removed by the 'make clean'
#             target
#
# CLEANDIRS   It is sometimes convenient to have directories that are not
#             built by default and therefore are not in DIRS -- such as
#             test case directories --  but should be cleaned if they have
#             been built by hand.  Such directories should be added to
#             CLEANDIRS.  Directories that appear in DIRS are automatically
#             in CLEANDIRS.
#
# TARGETS     output (i.e. binaries or libraries)
#
# DOCS        documents to be installed
#
# TOP         **relative** path to e
#
# OBJECTS     Names of any .o files built in the current directory.
#             Used by the dependency generator

# STRUCTURE OF TYPICAL MAKEFILE:
#
#   default: all
#
#   TOP=../..
#
#   TARGETS=foo
#   OBJECTS=foo.o bar.o bletch.o
#
#   DEF=-DSOME_DEF
#   INC=-Isome/directory/
#   OPT=-g   (or perhaps -O2, depending)
#
#   include $(TOP)/build/makerules.mk
#
#   all: $(TARGETS)
#   install: all
#       $(INSTALL) <install options> $(TARGETS) $(TOP)/directory/
#
#   foo: $(OBJECTS)
#       $(ECC) -o $@ $(OBJECTS)
#
# IF you are building a library:
#
#   foo.a: $(OBJECTS)
#       $(AR) crv foo.a $(OBJECTS)
#       $(RANLIB) $@
#
# IF you have multiple targets in a directory, it is easiest to set up
# several OBJECTS variables and then merge them all into a single
# OBJECTS variable at the end.

ifndef OPT
OPT=-O
endif

#################
#
# VARIABLES RELATED TO COMPILATION.  Generally speaking, you should control
# compilation by setting the variables CFLAGS, OPT, INC, and DEF and let
# the variable definitions below do their thing.  In unusual circumstances,
# modifications to these variables may be necessary, which is why their
# definition is conditionalized.
#
#################
#
# ECC         Compiler used to build C source files

ECC=gcc

# ECCWARN      Warning flags that will be used in C compiles. Can
#              be overridden by the makefile.
# ECCFLAGS     General compilation flags for C compilation
# ECCOPT       Optimization for C compilation

ifndef ECCFLAGS
 ECCFLAGS=$(CFLAGS) $(OPT) $(INC) $(DEF)
endif
ifndef ECCWARN
 ECCWARN=-Wall -Winline -Werror
endif

# ECPLUS       Compiler used to build C++ source files

ECPLUS=g++

# ECPLUSWARN   Warning flags that will be used for C++ compiles. Can
#              be overridden by the makefile.
# ECPLUSFLAGS  General compilation flags for C compilation
# ECPLUSOPT  Optimization for C compilation

ifndef ECPLUSWARN
ECPLUSWARN=-Wall -Winline -Werror
endif
ifndef ECPLUSFLAGS
ECPLUSFLAGS=$(OPT) $(ECPLUSOPT) $(INC) $(DEF)
endif

# WORKAROUND:
#
# gplus and egcs disable the inlining of member functions defined in headers
# by default.  This violates the spec, and the following variable tells it
# to conform:
#
# Note: GCC 3.0 conforms to C++ spec, enabling inlining member
# functions defined in class definitions (not in header files) by
# default.  --Zooko 2002-01-20

ECPLUSFLAGS += -fdefault-inline

# WORKAROUND:
#
# Template expansion is a mess of compile-flag compatibility problems.
# The following is the right thing for non-template applications.

ECPLUSFLAGS += -fno-implicit-templates

# WORKAROUND:
#
# If your compilation system is egcs, you get RTTI and exceptions support
# unless you turn it off.  If your code was written for an older generation
# of C++ compiler, this adds a lot of overhead for essentially zero benefit.
#
# Note: GCC 2.95 (the successor to egcs) and GCC 3.0 (the successor to
# GCC 2.95) both have RTTI and exceptions enabled by default.  If you
# don't use either feature and want to avoid the costs, you may want to
# adjust this to turn them off for those compilers.  --Zooko
# 2002-01-20

ifneq "" "$(findstring /usr/bin/egcs,$(wildcard /usr/bin/*))"
ECPLUSFLAGS += -fno-rtti -fno-exceptions
endif

# WORKAROUND:
#
# I don't like using .cpp as the extension for C++ source files, because
# historically I have used that for stuff that is fed through the C
# preprocessor.  Enough people seem to prefer .cpp that I caved in enough
# to make a variable of it:
CXX=cpp
.SUFFIXES: .$(CXX)

# RANLIB      Some System-V machines do not have ranlib, because
#             this functionality was integrated into ar in that
#             release.  On others RANLIB is present but does nothing.
#             If your system does not have a ranlib program, set this
#             variable to 'echo'.

RANLIB=ranlib
#RANLIB=echo

INSTALL=install
AR=ar

#
# Set up default values for these variables so that a build in an improperly
# configured environment has a fighting chance:
#


CLEANDIRS += $(DIRS)


#
# Object construction rules
#

.S.o:
	$(ECPLUS) $(ECCFLAGS) -c $< -o $@

.c.o:
	$(ECC) $(ECCFLAGS) $(ECCWARN) -c $<

.$(CXX).o:
	$(ECPLUS) $(ECPLUSFLAGS) $(ECPLUSWARN) -c $<


#
# MarkM's stuff for Windows & Java.  Note that we now depend on Java
# JDK >= 1.3
#

ifeq "$(OSDIR)" "win32"
 ifeq "$(JAVAC)" "gcj"
  SEP=:
 else
  SEP=;
 endif
 LOPT=
 DEF=-DWIN32
 EXE=.exe
else
 SEP=:
 LOPT=-lc
 DEF=
 EXE=.out
endif

COMMON_PATH=$(TOP)/classes

# When we run, we need to run against our platform's swt.jar if there
# is one. Since there may not be one available for the current
# platform, and we'd like to build at least the posix distribution of
# E anyway, we just always compile against the win32 version of
# swt.jar (the swt.jar for Microsoft Windows). The result should be
# able to run with any other swt.jar.
COMP_PATH0=$(COMMON_PATH)$(SEP)$(TOP)/src/bin/win32/swt.jar

ifdef JAVA_HOME
 # If JAVA_HOME is defined, then reach into it for the various commands
 ifndef JAVACMD
  JAVACMD=$(JAVA_HOME)/jre/bin/java
 endif
 ifndef JAVAC
  JAVAC=$(JAVA_HOME)/bin/javac
 endif
 ifndef JAVADOC
  JAVADOC=$(JAVA_HOME)/bin/javadoc
 endif
 # If JAVA_HOME is defined, then reach into it for additional jar
 # files to support compilation with jikes, and compilation of
 # doclets, respectively.
 COMP_PATH=$(JAVA_HOME)/jre/lib/rt.jar$(SEP)$(COMP_PATH0)
 TOOL_PATH=$(COMP_PATH)$(SEP)$(JAVA_HOME)/lib/tools.jar

else
 # If not JAVA_HOME, then assume various commands are on our PATH
 ifndef JAVACMD
  JAVACMD=java
 endif
 ifndef JAVAC
  JAVAC=javac
 endif
 ifndef JAVADOC
  JAVADOC=javadoc
 endif
 # "Must set JAVA_HOME to work with jikes or compile doclets."
 COMP_PATH=$(COMP_PATH0)
 TOOL_PATH=$(COMP_PATH)
endif

RUN_PATH0=$(TOP)/src/safej$(SEP)$(TOP)/src/esrc$(SEP)$(TOP)/src/bin/resources
RUN_PATH1=$(RUN_PATH0)$(SEP)$(COMMON_PATH)

ifeq "$(OSDIR)" "posix"
 # We know of no swt.jar for this platform.
 RUN_PATH=$(RUN_PATH1)
else
 ifeq "$(findstring -motif,$(OSDIR))" "-motif"
  # All OSes whose OSDIR ends in "-motif" share the same swt.jar file,
  # which we place in the pseudo-OSDIR "motif".
  RUN_PATH=$(RUN_PATH1)$(SEP)$(TOP)/src/bin/motif/swt.jar
 else
  # Otherwise, assume the OSDIR itself contains the swt.jar for the
  # current platform
  RUN_PATH=$(RUN_PATH1)$(SEP)$(TOP)/src/bin/$(OSDIR)/swt.jar
 endif
endif



# A Time-Space-Local UI-less Java that works after "make setup all" is done.
# Includes those flags relevant to ELib
STLJ="$(JAVACMD)" -cp "$(RUN_PATH)" \
	-De.safej.bind-var-to-propName=true \
	-Djava.library.path=$(TOP)/src/bin/$(PLATDIR)


# A Time-Space-Local UI-less E that works after "make setup all" is done.
# Includes those flags relevant to the E language as well.
STLE=$(STLJ) org.erights.e.elang.interp.Rune
STLEBIG=$(STLJ) -Xmx400m org.erights.e.elang.interp.Rune



ifeq "$(JAVAC)" "gcj"
 JCOMPILE="$(JAVAC)" -C -fno-assert -g \
	-classpath "$(COMP_PATH)" -d $(TOP)/classes
 TOOLCOMPILE="$(JAVAC)" -C -fno-assert -g \
	-classpath "$(TOOL_PATH)" -d $(TOP)/classes
else
 ifeq "$(JTARGET)" "none"
  JCOMPILE="$(JAVAC)" -g \
	-classpath "$(COMP_PATH)" -d $(TOP)/classes
  TOOLCOMPILE="$(JAVAC)" -g \
	-classpath "$(TOOL_PATH)" -d $(TOP)/classes
 else
  ifndef JTARGET
   JTARGET=1.2
  endif
  JCOMPILE="$(JAVAC)" -source $(JTARGET) -target $(JTARGET) -g \
	-classpath "$(COMP_PATH)" -d $(TOP)/classes
  TOOLCOMPILE="$(JAVAC)" -source $(JTARGET) -target $(JTARGET) -g \
	-classpath "$(TOOL_PATH)" -d $(TOP)/classes
 endif
endif


#
# Here is where all of the make recursion stuff is hidden.
#

.PHONY : all recursive-all
.PHONY: install recursive-install clobber recursive-clobber walk

all: recursive-all
recursive-all:
	@set -e; if [ -n "$(DIRS)" ]; then\
		for i in `echo "$(DIRS)"`; do \
			if [ -d "$$i" ]; then\
				$(MAKE) -C $$i $(MAKERULES) all; \
			fi; \
		done; \
	fi

install: recursive-install
recursive-install:
	@set -e; if [ -n "$(DIRS)" ]; then\
		for i in `echo "$(DIRS)"`; do \
			if [ -d "$$i" ]; then\
				$(MAKE) -C $$i $(MAKERULES) install; \
			fi; \
		done; \
	fi

clean: recursive-clean
	-rm -f *.o *.m core *~ new.Makefile
	-rm -f .*.m
	-rm -f *.dvi *.blg *.aux *.log *.toc $(CLEANLIST)
	-rm -f *.obj *.exe *.pif *.lnk

recursive-clean:
	@set -e; if [ -n "$(CLEANDIRS)" ]; then\
		for i in `echo "$(CLEANDIRS)"`; do \
			if [ -d "$$i" ]; then\
				$(MAKE) -C $$i $(MAKERULES) clean; \
			fi; \
		done; \
	fi

clobber: recursive-clobber
	-rm -f *.o *.m *~ core new.Makefile
	-rm -f .*.m
	-rm -f *.dvi *.blg *.aux *.log *.toc $(CLEANLIST)
	-rm -f *.obj *.exe *.pif *.lnk
	-rm -f *.cdb *.ilk *.pdb
	-rm -f $(TARGETS) DEPEND
	-rm -f "#*#" *.tmp *.out

recursive-clobber:
	@set -e; if [ -n "$(CLEANDIRS)" ]; then\
		for i in `echo "$(CLEANDIRS)"`; do \
			if [ -d "$$i" ]; then\
				$(MAKE) -C $$i $(MAKERULES) clobber; \
			fi; \
		done; \
	fi


# This is a debugging target..
walk:
	@set -e; if [ -n "$(DIRS)" ]; then\
		for i in `echo "$(DIRS)"`; do \
			$(MAKE) -C $$i $(MAKERULES) walk; \
		done; \
	fi
