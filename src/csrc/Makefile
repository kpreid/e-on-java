# Makefile for $(TOP)/src/csrc

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

default: all

TOP=../..

include $(TOP)/src/build/makerules.mk

ifeq "$(OSDIR)" "win32"
 DIRS=utils win32 setup
else
# DIRS=utils setup
 DIRS=setup
endif

CLEANDIRS += byaccj
