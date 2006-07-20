# Makefile for $(TOP)/src/csrc/utils

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

default: all

TOP=../../..

TARGETS=eutils.a \
	subst_test$(EXE) file_test$(EXE) prop_test$(EXE) showPath$(EXE)

OBJECTS=error_utils.o string_utils.o file_utils.o prop_utils.o


include $(TOP)/src/build/makerules.mk


all: $(TARGETS)

eutils.a: $(OBJECTS)
	$(AR) crv eutils.a $(OBJECTS)
	$(RANLIB) $@

LIBS=eutils.a

TAIL=$(LIBS) $(LOPT)

subst_test$(EXE): subst_test.o $(LIBS)
	$(ECC) -o subst_test$(EXE) subst_test.o $(TAIL)

file_test$(EXE): file_test.o $(LIBS)
	$(ECC) -o file_test$(EXE) file_test.o $(TAIL)

prop_test$(EXE): prop_test.o $(LIBS)
	$(ECC) -o prop_test$(EXE) prop_test.o $(TAIL)

showPath$(EXE): showPath.o $(LIBS)
	$(ECC) -o showPath$(EXE) showPath.o $(TAIL)
