# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html

default: all

TOP=../../../../..

include $(TOP)/src/build/makerules.mk

BYACCJ = $(TOP)/dist/bin/$(PLATDIR)/byaccj$(EXE)


# The BYACCJ is assumed to be BYACC/Java, and could originally be
# obtained from "http://www.lincom-asg.com/~rjamison/byacc/". This is
# standard Berkeley yacc enhanced by Bob Jamison so that it can also
# output Java code. Both Berkeley yacc and BYACC/Java are covered by
# the standard Berkeley open source license. This website has since
# disappeared, so the byaccj sources are now included in the E
# sources.


all:
	# The chmod is a horrible kludge to deal with the loss of the
	# execute bit when tar-ing on Windows.
	chmod a+x $(BYACCJ)
	$(BYACCJ) -jv -f TermParser -s Object term.y
	$(BYACCJ) -jv -f GrammarParser -s Object grammar.y

# For interactive grammar development and testing. Not for batch remaking.
_compile: all
	$(JCOMPILE) TermParser.java
	$(JCOMPILE) GrammarParser.java
