// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// TextWriter.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "TextWriter.h"
#include "VTable.h"
#include "PrimString.h"
#include <stdio.h>

static Selector DoPrintOn("__printOn/1", 1);

static Ref TextWriter_print(Ref self, Ref arg0) {
    if (arg0.myScript == PrimString8Script) {
        FILE *file = (FILE*)self.myData.word.myOther;
        const char *str = arg0.myData.word.myPrimString8;
        fputs(str, file);
    } else {
        arg0.call(&DoPrintOn, self);
    }
    return Ref();
}

static Ref TextWriter_println(Ref self, Ref arg0) {
    TextWriter_print(self, arg0);
    return TextWriter_print(self, Ref("\n"));
}

static Method *TextWriterMethods[] = {
    new Method1("print/1", &TextWriter_print),
    new Method1("println/1", &TextWriter_println)
};

static Script *TextWriterScript = new VTable(2, TextWriterMethods, NULL);


Ref EOut(TextWriterScript, DataWord((void*)stdout));
Ref EErr(TextWriterScript, DataWord((void*)stderr));
