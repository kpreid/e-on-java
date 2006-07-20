// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// SmallInteger.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "SmallInteger.h"
#include "VTable.h"
#include <stdio.h>

static Ref SmallInteger_add(Ref self, Ref other) {
    int a = self.myData.word.mySmallInteger;
    int b;
    if (getSmallInteger(other, &b)) {
        int sum = a + b;
        //XXX depends on "int" being 32 bits
        if (((sum ^ (sum << 1)) & 0x80000000) != 0) {
            throw Ref("BigInteger not yet implemented");
        }
        return Ref(sum);
    } else {
        throw Ref("XXX Integral Coercions not yet implemented");
    }
}

static Selector DoPrint("print/1", 1);

static Ref SmallInteger_printOn(Ref self, Ref textWriter) {
    int smallInteger = self.myData.word.mySmallInteger;
    char *buf = new char[20];
    sprintf(buf, "%d", smallInteger);
    textWriter.call(&DoPrint, Ref(buf));
    return Ref();
}

static Method *SmallIntegerMethods[] = {
    new Method1("add/1", &SmallInteger_add),
    new Method1("__printOn/1", &SmallInteger_printOn)
};

Script *SmallIntegerScript = new VTable(2, SmallIntegerMethods, NULL);

