// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// Script.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "Script.h"
#include "VTable.h"
#include "PrimNull.h"
#include "SmallInteger.h"
#include "PrimChar16.h"
#include "PrimBool.h"
#include "PrimDouble.h"
#include "PrimString.h"


Ref::Ref() : 
    myScript(NullScript), myData() {}

Ref::Ref(int smallInteger) : 
    myScript(SmallIntegerScript), myData(smallInteger) {}

Ref::Ref(wchar_t char16) : 
    myScript(Char16Script), myData(char16) {}

Ref::Ref(bool truthValue) : 
    myScript(BoolScript), myData(truthValue) {}

Ref::Ref(double unboxedDouble) : 
    myScript(DoubleScript), myData(unboxedDouble) {}

Ref::Ref(const char *primString8) : 
    myScript(PrimString8Script), myData(primString8) {}

Ref::Ref(const wchar_t *primString16) : 
    myScript(PrimString16Script), myData(primString16) {}



/**
 *
 */
static Ref defaultSendFunc(Ref *selfPtr, Selector *selector, Ref argv[]) {
    throw Ref("XXX Not yet implemented");
}

/**
 *
 */
static void defaultSendOnlyFunc(Ref *selfPtr, Selector *selector, Ref argv[]) {
    throw Ref("XXX Not yet implemented");
}

/**
 *
 */
static PtrState defaultStateFunc(Ref *selfPtr) {
    return NEAR;
}

/**
 *
 */
static Ref defaultResolutionFunc(Ref *selfPtr) {
    return *selfPtr;
}

/**
 *
 */
static Ref defaultOptProblemFunc(Ref *selfPtr) {
    return Ref();
}

Script::Script(CallFunc callFunc) :
    myCallFunc(callFunc),

    mySendFunc(defaultSendFunc),
    mySendOnlyFunc(defaultSendOnlyFunc),
    myStateFunc(defaultStateFunc),
    myResolutionFunc(defaultResolutionFunc),
    myOptProblemFunc(defaultOptProblemFunc),

    myUpgrade(NULL)
{}


static Method *ObsoleteMethod = NULL;

Selector::Selector(const char *verb, int arity) :
    myVerb(verb), //XXX should intern & check arity agreement
    myArity(arity),
    myLastMethod(NULL)
{
    //done in this peculiar way to avoid C++ static initialization 
    //order ambiguity
    if (NULL == ObsoleteMethod) {
        ObsoleteMethod = new Method("obsolete/0", NULL);
    }
    myLastMethod = ObsoleteMethod;
}
