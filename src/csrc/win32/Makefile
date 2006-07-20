# Makefile for $(TOP)/src/csrc/win32

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

default: all

TOP=../../..

CMNDS=make_shortcut$(EXE) file_assoc$(EXE) add_command$(EXE) winfo$(EXE)

TARGETS=ewin32.a $(CMNDS) reg_test$(EXE)

OBJECTS=reg_utils.o shortcut_utils.o winfo_utils.o

INC=-I../utils
# OPTS = -I ../utils


include $(TOP)/src/build/makerules.mk


all: $(TARGETS)

ewin32.a: $(OBJECTS)
	$(AR) crv ewin32.a $(OBJECTS)
	$(RANLIB) $@


LIBS=ewin32.a ../utils/eutils.a 

TAIL=$(LIBS) -lole32 -luuid $(LOPT)


make_shortcut$(EXE): make_shortcut.o $(LIBS)
	$(ECC) -o make_shortcut$(EXE) make_shortcut.o $(TAIL)

file_assoc$(EXE): file_assoc.o $(LIBS)
	$(ECC) -o file_assoc$(EXE) file_assoc.o $(TAIL)

add_command$(EXE): add_command.o $(LIBS)
	$(ECC) -o add_command$(EXE) add_command.o $(TAIL)

reg_test$(EXE): reg_test.o $(LIBS)
	$(ECC) -o reg_test$(EXE) reg_test.o $(TAIL)

winfo$(EXE): winfo.o $(LIBS)
	$(ECC) -o winfo$(EXE) winfo.o $(TAIL)


install:
	mkdir -p $(TOP)/dist/bin/$(PLATDIR)
	cp $(CMNDS) $(TOP)/dist/bin/$(PLATDIR)
ifeq "$(OSDIR)" "win32"
	cp "`type -p cygwin1.dll`" $(TOP)/dist/bin/$(PLATDIR)
endif
