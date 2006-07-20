// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// Script.h: declares DataWord, Selector, Ref, Method, & Script.
//
//////////////////////////////////////////////////////////////////////

#if !defined(SCRIPT_H)
#define SCRIPT_H


#include <stddef.h>

enum PtrState { NEAR, EVENTUAL, BROKEN };

class Script;
class ObjFrame;
class Selector;
class Method;

/**
 * The data that a script operates on
 */
class DataWord {
  public:
    union {
        int mySmallInteger;
        wchar_t myChar16;
        bool myTruthValue;
        double *myBoxedDouble;
        const char *myPrimString8;
        const wchar_t *myPrimString16;

        ObjFrame *myObjFrame;
        const void *myOther; //may not be GCed
    } word;

    /**
     *
     */
    inline DataWord() {
        word.myObjFrame = NULL;
    }

    inline DataWord(int smallInteger) {
        word.mySmallInteger = smallInteger;
    }

    inline DataWord(wchar_t char16) {
        word.myChar16 = char16;
    }

    inline DataWord(bool truthValue) {
        word.myTruthValue = truthValue;
    }

    inline DataWord(double unboxedDouble) {
        word.myBoxedDouble = new double;
        *word.myBoxedDouble = unboxedDouble;
    }

    inline DataWord(const char *primString8) {
        word.myPrimString8 = primString8;
    }

    inline DataWord(const wchar_t *primString16) {
        word.myPrimString16 = primString16;
    }

    inline DataWord(ObjFrame *objFrame) {
        word.myObjFrame = objFrame;
    }

    inline DataWord(void *other) {
        word.myOther = other;
    }

    /**
     * Simply bit-copied without taking the type into account.
     * <p>
     * Given that this constructor is provided as an implicit copy
     * constructor, we can't do better, since a DataWord by itself
     * doesn't know which type to use.
     */
    inline DataWord(DataWord& other) :
        word(other.word)
    {}
};

/**
 * A pass-by-copy object/facet reference consisting of two indivisible words:
 * A pointer to (a handle to) a script object, and a pointer-sized word to be
 * interpreted according to that script.
 */
class Ref  {

  public:

    Script *myScript;
    DataWord myData;

    /**
     *
     */
    inline Ref(Script *script, DataWord data) :
        myScript(script),
        myData(data)
    {}

    /**
     * Construct a reference to an E "null".
     */
    Ref();

    /**
     *
     */
    Ref(int smallInteger);

    /**
     *
     */
    Ref(wchar_t char16);

    /**
     *
     */
    Ref(bool truthValue);

    /**
     *
     */
    Ref(double unboxedDouble);

    /**
     *
     */
    Ref(const char *primString8);

    /**
     *
     */
    Ref(const wchar_t *primString16);



    /**
     *
     */
    inline Ref callAll(Selector *selector, Ref argv[]);

    inline Ref call(Selector *selector) {
        Ref argv[1];
        argv[0] = Ref();
        return this->callAll(selector, argv);
    }
    inline Ref call(Selector *selector, Ref arg0) {
        Ref argv[1];
        argv[0] = arg0;
        return this->callAll(selector, argv);
    }
    inline Ref call(Selector *selector, Ref arg0, Ref arg1) {
        Ref argv[2];
        argv[0] = arg0;
        argv[1] = arg1;
        return this->callAll(selector, argv);
    }
    inline Ref call(Selector *selector, Ref arg0, Ref arg1,
                    Ref arg2) {
        Ref argv[3];
        argv[0] = arg0;
        argv[1] = arg1;
        argv[2] = arg2;
        return this->callAll(selector, argv);
    }
    inline Ref call(Selector *selector, Ref arg0, Ref arg1,
                    Ref arg2, Ref arg3) {
        Ref argv[4];
        argv[0] = arg0;
        argv[1] = arg1;
        argv[2] = arg2;
        argv[3] = arg3;
        return this->callAll(selector, argv);
    }
    inline Ref call(Selector *selector, Ref arg0, Ref arg1,
                    Ref arg2, Ref arg3, Ref arg4) {
        Ref argv[5];
        argv[0] = arg0;
        argv[1] = arg1;
        argv[2] = arg2;
        argv[3] = arg3;
        argv[4] = arg4;
        return this->callAll(selector, argv);
    }

    /**
     *
     */
    inline Ref sendAll(Selector *selector, Ref argv[]);

    /**
     *
     */
    inline void sendAllOnly(Selector *selector, Ref argv[]);

    /**
     *
     */
    inline PtrState state();

    /**
     *
     */
    inline Ref resolution();

    /**
     *
     */
    inline Ref optProblem();
};

/**
 *
 */
typedef Ref (*CallFunc)(Ref *selfPtr, Selector *selector, Ref argv[]);

/**
 *
 */
typedef Ref (*SendFunc)(Ref *selfPtr, Selector *selector, Ref argv[]);

/**
 *
 */
typedef void (*SendOnlyFunc)(Ref *selfPtr, Selector *selector, Ref argv[]);

/**
 *
 */
typedef PtrState (*StateFunc)(Ref *selfPtr);

/**
 *
 */
typedef Ref (*ResolutionFunc)(Ref *selfPtr);

/**
 *
 */
typedef Ref (*OptProblemFunc)(Ref *selfPtr);

/**
 *
 */
class Script {
  public:
    CallFunc myCallFunc;
    SendFunc mySendFunc;
    SendOnlyFunc mySendOnlyFunc;
    StateFunc myStateFunc;
    ResolutionFunc myResolutionFunc;
    OptProblemFunc myOptProblemFunc;

    Script *myUpgrade; //used by an obsoleting CallFunc, etc...

    Script(CallFunc callFunc);
};

/**
 * Does call-site cacheing.
 */
class Selector {
  public:
    /**
     * A to-be-interned string that mangles in the arity, such as "foo/3".
     */
    const char *myVerb;
    int myArity; //must match
    Method *myLastMethod;

    Selector(const char *verb, int arity);
};

/**
 *
 */
typedef Ref (*ExecFunc)(Ref self, Method *method, Ref argv[]);

/**
 * Note that each Method object is specific to its containing Script. 
 * In other words, two Scripts may not share a Method object, though they
 * may share an ExecFunc that their respective Method objects point to.
 */
class Method {
  public:
    Script *myOptScript;    //used for call-site cacheing & class obsoleting.
                            //null iff obsolete or not yet installed
    const char *myVerb;     //interned with arity mangled in
    ExecFunc myOptExecFunc; //null iff obsolete

    inline Method(const char *verb, ExecFunc optExecFunc) :
        myOptScript(NULL),  //not yet installed
        myVerb(verb),
        myOptExecFunc(optExecFunc)
    {}
};


inline Ref Ref::callAll(Selector *selector, Ref argv[]) {
    Script *script = myScript;
    Method *method = selector->myLastMethod;
    if (method->myOptScript == script) {
        return method->myOptExecFunc(*this, method, argv);
    } else {
        return script->myCallFunc(this, selector, argv);
    }
}

inline Ref Ref::sendAll(Selector *selector, Ref argv[]) {
    return myScript->mySendFunc(this, selector, argv);
}

inline void Ref::sendAllOnly(Selector *selector, Ref argv[]) {
    myScript->mySendOnlyFunc(this, selector, argv);
}

inline PtrState Ref::state() {
    return myScript->myStateFunc(this);
}

inline Ref Ref::resolution() {
    return myScript->myResolutionFunc(this);
}

inline Ref Ref::optProblem() {
    return myScript->myOptProblemFunc(this);
}


#endif // !defined(SCRIPT_H)
