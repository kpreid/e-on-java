// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// SmallInteger.h:
//
//////////////////////////////////////////////////////////////////////

#if !defined(SMALLINTEGER_H)
#define SMALLINTEGER_H


#include "Script.h"

extern Script *SmallIntegerScript;

inline bool getSmallInteger(Ref ref, int *intPtr) {
    if (ref.myScript == SmallIntegerScript) {
        *intPtr = ref.myData.word.mySmallInteger;
        return true;
    } else {
        return false;
    }
}



#endif // !defined(SMALLINTEGER_H)
