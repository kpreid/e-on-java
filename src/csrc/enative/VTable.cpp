// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// VTable.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "VTable.h"
#include <string.h>


Ref VTableCallFunc(Ref *selfPtr, Selector *selector, Ref argv[]) {
    Ref self = *selfPtr;
    VTable *vTable = (VTable*)self.myScript;
    const char *verb = selector->myVerb;
    //XXX should do a hash lookup
    for (int i = 0; i < vTable->myNumMethods; i++) {
        Method *method = vTable->myMethods[i];
        //XXX when verbs are interned, do == instead
        if (strcmp(method->myVerb, verb) == 0) {
            //update call-site cache
            selector->myLastMethod = method;

            //Since only non-obsolete Scripts would use VTableCallFunc as
            //their CallFunc, we don't need to check that myOptExecFunc
            //isn't null.
            return method->myOptExecFunc(self, method, argv);
        }
    }
    Script *optOtherwise = vTable->myOptOtherwise;
    if (NULL == optOtherwise) {
        //XXX should include diagnostic info
        throw Ref("No such method");
    } else {
        Ref newSelf(optOtherwise, self.myData);
        return optOtherwise->myCallFunc(&newSelf, selector, argv);
    }
}

VTable::VTable(int numMethods, Method **methods, Script *optOtherwise) :
    Script(VTableCallFunc),
    myNumMethods(numMethods),
    myMethods(methods),
    myOptOtherwise(optOtherwise)
{
    for (int i = 0; i < numMethods; i++) {
        methods[i]->myOptScript = this;     //installation
    }
}

        
/**
 *
 */
static Ref exec0Func(Ref self, Method *method, Ref argv[]) {
    Method0 *method0 = (Method0*)method;
    return method0->myMethod0Func(self);
}

static Ref exec1Func(Ref self, Method *method, Ref argv[]) {
    Method1 *method1 = (Method1*)method;
    return method1->myMethod1Func(self, argv[0]);
}

static Ref exec2Func(Ref self, Method *method, Ref argv[]) {
    Method2 *method2 = (Method2*)method;
    return method2->myMethod2Func(self, argv[0], argv[1]);
}

static Ref exec3Func(Ref self, Method *method, Ref argv[]) {
    Method3 *method3 = (Method3*)method;
    return method3->myMethod3Func(self, argv[0], argv[1],
                                  argv[2]);
}

static Ref exec4Func(Ref self, Method *method, Ref argv[]) {
    Method4 *method4 = (Method4*)method;
    return method4->myMethod4Func(self, argv[0], argv[1],
                                  argv[2], argv[3]);
}

static Ref exec5Func(Ref self, Method *method, Ref argv[]) {
    Method5 *method5 = (Method5*)method;
    return method5->myMethod5Func(self, argv[0], argv[1],
                                  argv[2], argv[3], argv[4]);
}


Method0::Method0(const char *verb, Method0Func method0Func) :
    Method(verb, exec0Func),
    myMethod0Func(method0Func)
{}

Method1::Method1(const char *verb, Method1Func method1Func) :
    Method(verb, exec1Func),
    myMethod1Func(method1Func)
{}

Method2::Method2(const char *verb, Method2Func method2Func) :
    Method(verb, exec2Func),
    myMethod2Func(method2Func)
{}

Method3::Method3(const char *verb, Method3Func method3Func) :
    Method(verb, exec3Func),
    myMethod3Func(method3Func)
{}

Method4::Method4(const char *verb, Method4Func method4Func) :
    Method(verb, exec4Func),
    myMethod4Func(method4Func)
{}

Method5::Method5(const char *verb, Method5Func method5Func) :
    Method(verb, exec5Func),
    myMethod5Func(method5Func)
{}
