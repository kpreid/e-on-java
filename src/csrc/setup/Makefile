# Makefile for $(TOP)/src/csrc/setup

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

default: all

TOP=../../..

include $(TOP)/src/build/makerules.mk

all:

SLFTTFT=sacrificial-long-file-name-to-test-for-truncation.txt

install:
	cp rune rune-template.txt devrune-template.txt $(TOP)/dist/
	cp rune-bat-template.txt $(TOP)/dist/
	cp eprops-template.txt $(SLFTTFT) $(TOP)/dist/
	cp 0install/e-core.xml $(TOP)/dist/0install/
	cp README.txt updoc-hash-cache.txt $(TOP)/dist/
	cp inst-e.bash $(TOP)/dist/
	$(STLE) --help > $(TOP)/dist/rune-help.txt
