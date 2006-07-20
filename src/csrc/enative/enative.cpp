// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// enative.cpp : Defines main()
//
//////////////////////////////////////////////////////////////////////

#include "Script.h"
#include "TextWriter.h"
#include <stdio.h>

static Selector DoPrint("print/1", 1);

static Selector DoPrintln("println/1", 1);

static Selector DoStupid("stupid/1", 1);

static Selector DoAdd("add/1", 1);

int main(int argc, char* argv[])
{
    printf("Hello World!\n");

    EOut.call(&DoPrint, Ref("Hello EWorld!\n"));
    
    try {
        EOut.call(&DoStupid, Ref("Hello EWorld!\n"));
    } catch (Ref& ex) {
        EErr.call(&DoPrintln, ex);
    }

    try {
        EOut.call(&DoPrintln,
                  Ref(3).call(&DoAdd, Ref(4)));
    } catch (Ref& ex) {
        EErr.call(&DoPrintln, ex);
    }
    
    try {
        EOut.call(&DoPrintln,
                  Ref(1000000000).call(&DoAdd, Ref(1000000000)));
    } catch (Ref& ex) {
        EErr.call(&DoPrintln, ex);
    }
    
    return 0;
}

