// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// VTable.h:
//
//////////////////////////////////////////////////////////////////////

#if !defined(VTABLE_H)
#define VTABLE_H


#include "Script.h"

/**
 *
 */
extern Ref VTableCallFunc(Ref *selfPtr, Selector *selector, Ref argv[]);

/**
 * The Script used by methodical objects
 */
class VTable : public Script {
  public:
    //XXX should use a separate hashtable abstraction
    int myNumMethods;
    Method **myMethods;

    Script *myOptOtherwise;

    VTable(int numMethods, Method **methods, Script *optOtherwise);
};


/**
 *
 */
typedef Ref (*Method0Func)(Ref self);

/**
 *
 */
class Method0 : public Method {
  public:
    Method0Func myMethod0Func;

    Method0(const char *verb, Method0Func method0Func);
};



typedef Ref (*Method1Func)(Ref self, Ref arg0);

class Method1 : public Method {
  public:
    Method1Func myMethod1Func;

    Method1(const char *verb, Method1Func method1Func);
};



typedef Ref (*Method2Func)(Ref self, Ref arg0, Ref arg1);

class Method2 : public Method {
  public:
    Method2Func myMethod2Func;

    Method2(const char *verb, Method2Func method2Func);
};



typedef Ref (*Method3Func)(Ref self, Ref arg0, Ref arg1, 
                           Ref arg2);
class Method3 : public Method {
  public:
    Method3Func myMethod3Func;

    Method3(const char *verb, Method3Func method3Func);
};



typedef Ref (*Method4Func)(Ref self, Ref arg0, Ref arg1, 
                           Ref arg2, Ref arg3);
class Method4 : public Method {
  public:
    Method4Func myMethod4Func;

    Method4(const char *verb, Method4Func method4Func);
};



typedef Ref (*Method5Func)(Ref self, Ref arg0, Ref arg1, 
                           Ref arg2, Ref arg3, Ref arg4);
class Method5 : public Method {
  public:
    Method5Func myMethod5Func;

    Method5(const char *verb, Method5Func method5Func);
};


#endif // !defined(VTABLE_H)
