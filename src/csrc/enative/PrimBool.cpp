// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// PrimBool.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "PrimBool.h"
#include "VTable.h"

static Method *BoolMethods[] = {NULL};

Script *BoolScript = new VTable(0, BoolMethods, NULL);
