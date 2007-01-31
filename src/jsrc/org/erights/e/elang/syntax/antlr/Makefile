# Copyright 2004-2005 Dean Tribble under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html


default: all

TOP=../../../../../../../..

JAVATARGS=EParser.java ETokenTypes.java \
	EALexer.java EALexerTokenTypes.java \
	QuasiLexer.java QuasiLexerTokenTypes.java \

CLEANLIST=$(JAVATARGS) ETokenTypes.txt \
	EALexerTokenTypes.txt QuasiLexerTokenTypes.txt

include $(TOP)/src/build/makerules.mk

_parse:
	$(STLJ) antlr.Tool e.g
	$(STLJ) antlr.Tool elex.g
	$(STLJ) antlr.Tool quasi.g

report: _parse
	$(JCOMPILE) $(JAVATARGS) eMain.java
	$(STLJ) org.erights.e.elang.syntax.antlr.eMain $(TOP)/src/esrc
	$(STLJ) org.erights.e.elang.syntax.antlr.eMain $(TOP)/../CL-E

all: _parse

fix:
	$(STLE) $(TOP)/src/esrc/scripts/newlines.e $(CLEANLIST)
