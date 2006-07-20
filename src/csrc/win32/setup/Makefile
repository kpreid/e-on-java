# Makefile for $(TOP)/src/csrc/win32/setup

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

default: all

TOP=../../../..

include $(TOP)/src/build/makerules.mk

all: libs setup$(EXE) uninstall$(EXE)

INC=-I.. -I../../utils

ifeq "$(OSDIR)" "win32"
 UTILS_O=setup_utils.o \
	../winerror_utils.o ../winfile_utils.o ../reg_utils.o \
	../shortcut_utils.o ../winfo_utils.o \
	../../utils/error_utils.o ../../utils/string_utils.o \
	../../utils/file_utils.o ../../utils/prop_utils.o \
	-lole32 -luuid
else
 UTILS_O=setup_utils.o \
	../winerror_utils.o ../winfile_utils.o ../reg_utils.o \
	../shortcut_utils.o ../winfo_utils.o \
	../../utils/error_utils.o ../../utils/string_utils.o \
	../../utils/file_utils.o ../../utils/prop_utils.o
endif

# OPTS = -I .. -I ../../utils

libs: setup_utils.o

setup$(EXE): setup.o libs
	gcc -o setup$(EXE) setup.o $(UTILS_O) $(LOPT)

uninstall$(EXE): uninstall.o libs
	gcc -o uninstall$(EXE) uninstall.o $(UTILS_O) $(LOPT)


install:
	cp e.template $(TOP)/dist/e.template
	cp setup$(EXE) $(TOP)/dist/setup$(EXE)
ifeq "$(OSDIR)" "win32"
	mkdir -p $(TOP)/dist/bin/$(PLATDIR)
	cp "`type -p cygwin1.dll`"  $(TOP)/dist/bin/$(PLATDIR)
endif
	#
	# uncomment only once it works.
	# cp uninstall$(EXE) $(TOP)/dist/uninstall$(EXE)
	#
	# old linux way
	# cp eprops.txt eprops-template.txt $(TOP)/dist/
	# cp personal-install.sh root-install.sh $(TOP)/dist
	# $(STLE) $(TOP)/src/esrc/scripts/replace.e \
	#	$(TOP)/dist/eprops.txt e.version \
	#	"e.version=`cat $(TOP)/dist/eVersion.txt`"

