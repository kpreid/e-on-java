# Makefile for $(TOP)/src/jsrc

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

# Note that we now depend on Java JDK >= 1.3

default: all

TOP=../..
# DIRS=org/erights/e/elang/syntax

include $(TOP)/src/build/makerules.mk

ifeq "$(MAKE_PARSERS)" "true"
all: all_parse
else
all: all_noparse
endif



ER=org/erights/e

setup:
	-rm -rf $(TOP)/classes/*
	mkdir -p $(TOP)/classes
	cp -rf $(TOP)/src/esrc/* $(TOP)/classes/
	-rm -rf $(TOP)/classes/caplets
	-rm -rf $(TOP)/classes/scripts/webServer
	cp -rf $(TOP)/src/safej/* $(TOP)/classes/
	cp -rf $(TOP)/src/bin/resources/* $(TOP)/classes/
	-rm -rf `find $(TOP)/classes -name CVS`
	-rm -f `find $(TOP)/classes -name '*~'` \
	# (cd $(TOP)/classes; jar xf ../src/bin/jars/hydro.jar)
	-rm -rf $(TOP)/classes/META-INF

# Targets with _ in them don't check dependencies. They're really
# just "procedures" to be used in other rules.

# Check for a compiler bug.
comp_test:
	find test                       -name '*.java' > files.tmp
	$(JCOMPILE) @files.tmp

# low level support code.
_develop:
	find $(ER)/develop              -name '*.java' > files.tmp
	$(JCOMPILE) @files.tmp

# optional: remake the term parser from yacc, or just use the generated
# one, which is included.
term_parser:
	(cd $(TOP)/src/jsrc/org/quasiliteral/term; $(MAKE) all)

# space-time-local elib & quasiliterals up to 'term'
# depends on develop
stl_elib:
	find $(ER)/elib                 -name '*.java' > files.tmp
	find $(ER)/meta/java/io         -name '*.java' >> files.tmp
	find $(ER)/meta/java/lang       -name '*.java' >> files.tmp
	find $(ER)/meta/java/math       -name '*.java' >> files.tmp
	find $(ER)/meta/java/net        -name '*.java' >> files.tmp
	find $(ER)/meta/java/security   -name '*.java' >> files.tmp
	find $(ER)/meta/java/util       -name '*.java' >> files.tmp
	find $(ER)/meta/$(ER)/elib      -name '*.java' >> files.tmp
	find org/quasiliteral/astro     -name '*.java' >> files.tmp
	find org/quasiliteral/syntax    -name '*.java' >> files.tmp
	find org/quasiliteral/term      -name '*.java' >> files.tmp
	$(JCOMPILE) @files.tmp

# depends on stl_elib
quasi_1:
	find org/quasiliteral/base      -name '*.java' > files.tmp
	find org/quasiliteral/text      -name '*.java' >> files.tmp
	find org/quasiliteral/quasiterm -name '*.java' >> files.tmp
	$(JCOMPILE) @files.tmp

# Third party parsing and regex tools
third_party:
	find antlr                      -name '*.java' > files.tmp
	find org/quasiliteral/antlr     -name '*.java' >> files.tmp
	find org/apache/oro             -name '*.java' >> files.tmp
	$(JCOMPILE) @files.tmp

# optional: remake the E parser from yacc, or just use the generated
# one, which is included.
elang_parser:
	(cd $(ER)/elang/syntax; $(MAKE) all)
	(cd $(ER)/elang/syntax/antlr; $(MAKE) all)

# space-time-local elang
# depends on stl_elib, quasi_1, third_party
stl_elang:
	find $(ER)/elang                -name '*.java' > files.tmp
	$(JCOMPILE) @files.tmp

# external file and timer services
# depends on stl_elib
_extern:
	find $(ER)/extern/file         	-name '*.java' > files.tmp
	find $(ER)/extern/timer         -name '*.java' >> files.tmp
	$(JCOMPILE) @files.tmp

# distributed persistent E
# depends on stl_elib & _extern
_vattp:
	find net/vattp                  -name '*.java' > files.tmp
	find org/waterken               -name '*.java' >> files.tmp
	$(JCOMPILE) @files.tmp

# distributed E with identity-persistence
# depends on _vattp
_captp:
	find net/captp                  -name '*.java' > files.tmp
	$(JCOMPILE) @files.tmp

# persistent E, built on identity-persistence
# depends on _captp & _extern
_persist:
	find $(ER)/extern/persist      	-name '*.java' > files.tmp
	$(JCOMPILE) @files.tmp

# ui support
# depends on stl_elang
_ui:
	@echo ""
	@echo "------------------"
	@echo "About to compile the ui support of ELib, including the SWT"
	@echo "support classes. This compiles against the swt.jar for the"
	@echo "win32 OS regardless of what platform you're actually on."
	find $(ER)/ui                   -name '*.java' > files.tmp
	find $(ER)/meta/java/awt        -name '*.java' >> files.tmp
	find $(ER)/meta/javax           -name '*.java' >> files.tmp
	find $(ER)/meta/org/eclipse/swt -name '*.java' >> files.tmp
	find org/quasiliteral/html      -name '*.java' >> files.tmp
	$(JCOMPILE) @files.tmp
	@echo "------------------"
	@echo ""


# Should probably be broken up
_skylib:
	find com/skyhunter              -name '*.java' > files.tmp
	find com/zooko              	-name '*.java' >> files.tmp
	$(JCOMPILE) @files.tmp

# Electronic Rights Transfer Protocol
# depends on _captp (just for SturdyRef)
_ertp:
	find net/ertp                   -name '*.java' > files.tmp
	$(JCOMPILE) @files.tmp

# depends on third_party, quasi_1
quasi_2:
	find org/capml                  -name '*.java' > files.tmp
	$(JCOMPILE) @files.tmp




# pre_parsers: setup comp_test _develop

pre_parsers: setup _develop

twixt_parsers: stl_elib quasi_1 third_party 

post_parsers: stl_elang _extern _vattp _captp _persist _ui _skylib

_optional: _ertp quasi_2


all_parse: pre_parsers term_parser twixt_parsers elang_parser \
	post_parsers _optional

all_noparse: pre_parsers twixt_parsers post_parsers _optional

install:
	-rm -f $(TOP)/dist/e.jar
	(cd $(TOP)/classes; \
	jar cfm ../dist/e.jar ../src/jsrc/ForManifest.txt .)
	# (cd $(TOP)/dist; jar -i e.jar)
