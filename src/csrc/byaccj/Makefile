#################################################
## Makefile for Byacc/Java
## Date: 01 Oct 97
## Author: Bob Jamison
## Modified by Mark S. Miller
#################################################
# Makefile for $(TOP)/src/csrc/byaccj/

default: all

TOP=../../..

TARGETS=byaccj$(EXE)

OBJECTS=closure.o \
	error.o \
	lalr.o \
	lr0.o \
	main.o \
	mkpar.o \
	output.o \
	reader.o \
	skeleton.o \
	symtab.o \
	verbose.o \
	warshall.o

include $(TOP)/src/build/makerules.mk


all: $(TARGETS)

byaccj$(EXE): $(OBJECTS)
	$(ECC) -o byaccj$(EXE) $(OBJECTS)
	@echo "done"

closure.o: closure.c defs.h
error.o: error.c defs.h
lalr.o: lalr.c defs.h
lr0.o: lr0.c defs.h
main.o: main.c defs.h
mkpar.o: mkpar.c defs.h
output.o: output.c defs.h
reader.o: reader.c defs.h
skeleton.o: skeleton.c defs.h
symtab.o: symtab.c defs.h
verbose.o: verbose.c defs.h
warshall.o: warshall.c defs.h


install:
	mkdir -p $(TOP)/dist/bin/$(PLATDIR)
	cp byaccj$(EXE) $(TOP)/dist/bin/$(PLATDIR)
