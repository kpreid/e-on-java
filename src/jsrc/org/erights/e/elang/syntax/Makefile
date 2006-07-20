#
# The contents of this file are subject to the Electric Communities E
# Open Source Code License Version 1.0 (the "License"); you may not use
# this file except in compliance with the License. You may obtain a copy
# of the License at http://www.communities.com/EL/.
#
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
#
# The Original Code is the Distributed E Language Implementation,
# released July 20, 1998.
#
# The Initial Developer of the Original Code is Electric Communities.
# Copyright (C) 1998 Electric Communities. All Rights Reserved.
#
# Contributor(s): ______________________________________.
#

default: all

TOP=../../../../../../..

include $(TOP)/src/build/makerules.mk

BYACCJ = $(TOP)/dist/bin/$(PLATDIR)/byaccj$(EXE)

RPATH=org/erights/e/elang/syntax/ParserTables.data


# The BYACCJ is assumed to be BYACC/Java, and could originally be
# obtained from "http://www.lincom-asg.com/~rjamison/byacc/". This is
# standard Berkeley yacc enhanced by Bob Jamison so that it can also
# output Java code. Both Berkeley yacc and BYACC/Java are covered by
# the standard Berkeley open source license. This website has since
# disappeared, so the byaccj sources are now included in the E
# sources.
#
# On e.y, the byacc/java output exceeds jvm length limitations, so we
# call our own EYaccFixer to break up the output


_parse:
	# The chmod is a horrible kludge to deal with the loss of the
	# execute bit when tar-ing on Windows.
	chmod a+x $(BYACCJ)
	$(BYACCJ) -jv -f EParser -s Object e.y
	$(STLJ) org.quasiliteral.text.EYaccFixer \
		EParser.java $(TOP)/src/bin/resources $(RPATH)
	cp $(TOP)/src/bin/resources/$(RPATH) $(TOP)/classes/$(RPATH)

# e-test.y is for testing which grammars are unambigous, without
# needing to work with the rest of the E build.
_test:
	$(BYACCJ) -jv -f EParser -s Object e-test.y

all: e.y _parse

# For interactive grammar development and testing. Not for batch remaking.
_compile: all
	$(JCOMPILE) EParser.java
