# Makefile for $(TOP)/src.

# Copyright 2002 Combex, Inc. under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# Note that we now depend on Java JDK >= 1.4.

default: draft explain

# Prefix tagging this release's attributes
PREFIX=E
DOTVER=0.9.3b
TAGVER=0_9_3b
RELEASE=working

TOP=..
DIRS=jsrc csrc


include $(TOP)/src/build/makerules.mk

announce:
	@echo ""
	@echo "***************************************"
	@echo "** Making $(PREFIX) $(DOTVER) for $(PLATDIR) **"
	@echo "***************************************"
	@echo ""

clobber: targdir-clobber

targdir-clobber:
	-rm -rf $(TOP)/classes $(TOP)/export $(TOP)/tarballs $(TOP)/dist

setup:
	-rm -rf $(TOP)/dist
	mkdir -p $(TOP)/dist/bin
	mkdir -p $(TOP)/dist/0install
	cp -rf $(TOP)/src/bin/icons $(TOP)/dist/bin
ifneq "$(OSDIR)" "posix"
	cp -rf $(TOP)/src/bin/$(OSDIR) $(TOP)/dist/bin
endif
	@echo ""
	@echo "------------------"
ifeq "$(MAKE_PARSERS)" "true"
	@echo "Attempting to make byaccj in order to remake the parsers."
	@echo "If the following C compiles don't work, then unset"
	@echo "MAKE_PARSERS, or set it to 'false', and start again."
	@echo "This should be fine so long as you don't need to change"
	@echo "any *.y files."
	(cd csrc/byaccj; $(MAKE) all install)
else
	@echo "Not making byaccj or remaking the parsers."
	@echo "If you want to make these, set the environment variable"
	@echo "'MAKE_PARSERS' to 'true' and start again."
endif
	@echo "------------------"
	@echo ""
	echo $(DOTVER) > $(TOP)/dist/eVersion.txt
	cp -rf $(TOP)/src/esrc/scripts $(TOP)/dist/scripts
	cp -rf $(TOP)/src/elisp/* $(TOP)/dist/scripts
	cp -rf $(TOP)/src/esrc/caplets $(TOP)/dist/caplets
	-rm -rf `find $(TOP)/dist -name CVS`

export:
	-rm -rf $(TOP)/export
	mkdir -p $(TOP)/export/e
	cp -rf $(TOP)/dist $(TOP)/export/dist
	cp -rf $(TOP)/src $(TOP)/export/e/src
	-rm -rf $(TOP)/export/e/src/ePrograms
	# remove edoc because it's SCSL, not open source
	-rm -rf $(TOP)/export/e/src/jsrc15/com/combex/e/edoc
	# remove jsrc15 because it's not yet ready
	-rm -rf $(TOP)/export/e/src/jsrc15/
	-rm -rf `find $(TOP)/export -name CVS`
	-rm -rf `find $(TOP)/export -name .svn`
	-rm -rf `find $(TOP)/export -name makeout`
	-rm -rf `find $(TOP)/export -name cvsout`
	-rm -rf `find $(TOP)/export -name y`
	-rm -rf `find $(TOP)/export -name y.tab.out`
	-rm -rf `find $(TOP)/export -name '*.cdb'`
	-rm -rf `find $(TOP)/export -name '*.obj'`
	-rm -rf `find $(TOP)/export -name '*.o'`
	-rm -rf `find $(TOP)/export -name '*.tmp'`
	-rm -rf `find $(TOP)/export -name '*.core'`
	-rm -rf `find $(TOP)/export -name '#*#'`
	-rm -rf `find $(TOP)/export -name '*~'`
	-rm -rf `find $(TOP)/export -name '*.lnk'`
	-rm -rf `find $(TOP)/export -name '_.#*#.0'`
	-rm -rf `find $(TOP)/export -name _notes`
	-rm -rf `find $(TOP)/export -name Thumbs.db`
	-rm -rf `find $(TOP)/export -name '*.ipr'`
	-rm -rf `find $(TOP)/export -name '*.iws'`
	-rm -rf `find $(TOP)/export -name '*.stackdump'`
	-rm -rf `find $(TOP)/export -name '*.DS_Store'`
	-rm -f $(TOP)/export/dist/bin/icons/*.png
	-rm -f $(TOP)/export/dist/bin/jars/hydro.jar
	-rm -rf $(TOP)/export/dist/bin/resources
	-rm -rf `find $(TOP)/export/e -name '*.out'`
	-rm -rf `find $(TOP)/export/e -name '*.exe'`
	-rm -rf `find $(TOP)/export/e -name '*.ilk'`
	-rm -rf `find $(TOP)/export/e -name '*.pdb'`
	-rm -rf `find $(TOP)/export/e/src/csrc -name '*.a'`
	cp -rf $(TOP)/export/dist $(TOP)/export/puredist
	rm -rf $(TOP)/export/puredist/bin/$(OSDIR)
	$(STLE) $(TOP)/src/esrc/scripts/substProps.e \
		$(TOP)/export/dist/eprops-template.txt \
		e.version=$(DOTVER) \
		e.osdir=$(OSDIR) \
		e.machdir=$(MACHDIR)
	$(STLE) $(TOP)/src/esrc/scripts/substProps.e \
		$(TOP)/export/dist/rune-template.txt \
		e.osdir=$(OSDIR) \
		e.machdir=$(MACHDIR)
	$(STLE) $(TOP)/src/esrc/scripts/substProps.e \
		$(TOP)/export/dist/devrune-template.txt \
		e.osdir=$(OSDIR) \
		e.machdir=$(MACHDIR)
	$(STLE) $(TOP)/src/esrc/scripts/substProps.e \
		$(TOP)/export/puredist/eprops-template.txt \
		e.version=$(DOTVER)


# addressed in the tarballs directory from the export directory
UPFIX1=../tarballs/$(PREFIX)
# addressed in the tarballs directory from the export/dist directory
UPFIX2=../../tarballs/$(PREFIX)
# The suffix for the platform specific version
SUFFIX=$(OSDIR)-$(MACHDIR)-$(DOTVER)

tarballs:
	-rm -rf $(TOP)/tarballs
	mkdir -p $(TOP)/tarballs
	# We now just normalize all newlines, regardless of target
	# platform. 
	$(STLE) $(TOP)/src/esrc/scripts/newlines.e \
		--unix --onlyKnown $(TOP)/export
	# There is now just one source distribution
	(cd $(TOP)/export; tar czf $(UPFIX1)-src-$(DOTVER).tar.gz e)
ifneq "$(OSDIR)" "posix"
 ifneq "$(MACHTYPE)" "unknown"
	# If we're making on an unrecognized OS or machine, then we
	# don't make any platform-specific binary distribution.
	# Otherwise, we make the platform-specific binary distribution
	# for our current platform. We do *not* do any cross-building
	# between platforms.
  ifeq "$(OSDIR)" "win32"
	# If the target platform is win32, then package the binary
	# distribution as a zip file, else as a tarball.
	(cd $(TOP)/export/dist; zip -rqD $(UPFIX2)-$(SUFFIX).zip .)
  else
	(cd $(TOP)/export/dist; tar czf $(UPFIX2)-$(SUFFIX).tar.gz .)
  endif
 endif
endif
	# Make the Pure-Java binary distribution under the assumption
	# of a Posix-like target.  We build this distribution
	# regardless of what platform we're building on.
	(cd $(TOP)/export/puredist; tar czf $(UPFIX2)-purej-$(DOTVER).tar.gz .)


ckversion:
ifeq "$(RELEASE)" "release"
	@echo "Releasing version $(DOTVER) of $(PREFIX)"
else
	@echo "**** The RELEASE variable is currently set to $(RELEASE)'"
	@echo "**** To do a release, edit this makefile to update the"
	@echo "**** values of the DOTVER and TAGVER variables and say"
	@echo "****"
	@echo "****       make RELEASE=release publish"
	@echo "****"
	@exit 1
endif


release:
	cvs rtag -R -F $(RELEASE)_$(TAGVER) e/src
ifneq "$(RELEASE)" "working"
	cvs rtag -R -F current_release e/src
endif



reports: srccheck updoc_reports
visit_reports:
	$(TOP)/src/esrc/scripts/maint/emacsify.e *report*.txt

srccheck: srccheck-esrc srccheck-jsrc srccheck-doc

srccheck-esrc:
	($(STLEBIGT) $(TOP)/src/esrc/scripts/maint/srccheck.e \
		$(TOP)/src/esrc  $(TOP)/src/esrc 2>&1 | tee report-esrc.txt)

srccheck-jsrc:
	($(STLEBIGT) $(TOP)/src/esrc/scripts/maint/srccheck.e \
		$(TOP)/src/jsrc  $(TOP)/src/esrc 2>&1 | tee report-jsrc.txt)

srccheck-doc:
	($(STLEBIGT) $(TOP)/src/esrc/scripts/maint/srccheck.e \
		$(TOP)/doc/data  $(TOP)/src/esrc 2>&1 | tee report-doc1.txt)
	($(STLEBIGT) $(TOP)/src/esrc/scripts/maint/srccheck.e \
		$(TOP)/doc/elib  $(TOP)/src/esrc 2>&1 | tee report-doc2.txt)
	($(STLEBIGT) $(TOP)/src/esrc/scripts/maint/srccheck.e \
		$(TOP)/doc/elang $(TOP)/src/esrc 2>&1 | tee report-doc3.txt)

updoc_reports: updoc_e_reports updoc_doc_reports

updoc_e_reports:
	($(STLEBIGT) $(TOP)/src/esrc/scripts/updoc.e \
		jsrc 2>&1 | tee report-updoc-jsrc.txt)
	cp "/cygdrive/c/Program Files/erights.org/updoc-hash-cache.txt" \
		$(TOP)/src/csrc/setup/updoc-hash-cache.txt
	($(STLEBIGT) $(TOP)/src/esrc/scripts/updoc.e \
		esrc 2>&1 | tee report-updoc-esrc.txt)
	cp "/cygdrive/c/Program Files/erights.org/updoc-hash-cache.txt" \
		$(TOP)/src/csrc/setup/updoc-hash-cache.txt

updoc_doc_reports:
	($(STLEBIGT) $(TOP)/src/esrc/scripts/updoc.e \
		$(TOP)/doc/data 2>&1 | tee report-updoc-doc1.txt)
	cp "/cygdrive/c/Program Files/erights.org/updoc-hash-cache.txt" \
		$(TOP)/src/csrc/setup/updoc-hash-cache.txt
	($(STLEBIGT) $(TOP)/src/esrc/scripts/updoc.e \
		$(TOP)/doc/elib 2>&1 | tee report-updoc-doc2.txt)
	cp "/cygdrive/c/Program Files/erights.org/updoc-hash-cache.txt" \
		$(TOP)/src/csrc/setup/updoc-hash-cache.txt
	($(STLEBIGT) $(TOP)/src/esrc/scripts/updoc.e \
		$(TOP)/doc/elang 2>&1 | tee report-updoc-doc3.txt)
	cp "/cygdrive/c/Program Files/erights.org/updoc-hash-cache.txt" \
		$(TOP)/src/csrc/setup/updoc-hash-cache.txt


# Just for experimenting with the STLE environment
__interact:
	$(STLE)

#
# All of publish except tarballing and tagging/releasing 
#
draft: announce clobber setup all fix install export

#
# Run after "draft" if the user uses the default target
#
explain:
	@echo ""
	@echo "An unpacked E binary distribution is now in $(TOP)/export/dist"
	@echo "and a source distribution is now in $(TOP)/export/e/src."
	@echo "To make tarballs, you can \"make tarballs\"."
	@echo "Or to actually install E, you can copy $(TOP)/export/dist" 
	@echo "to where you want E installed, cd there, and run"
	@echo ""
	@echo "    % java -jar e.jar"
	@echo ""

#
# do as explain explains, but in place
#
_setup:
	(cd $(TOP)/export/dist; $(JAVACMD) -jar e.jar)

#
# Actually make a full blown release
#
publish: ckversion draft tarballs release

fix:
	(cd jsrc/com/hp/orc; make fix)
	(cd jsrc/org/erights/e/elang/syntax; make fix)
	(cd jsrc/org/erights/e/elang/syntax/antlr; make fix)
	(cd jsrc/org/quasiliteral/term; make fix)
