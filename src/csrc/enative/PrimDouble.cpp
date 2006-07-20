// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// PrimDouble.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "PrimDouble.h"
#include "VTable.h"

static Method *DoubleMethods[] = {NULL};

Script *DoubleScript= new VTable(0, DoubleMethods, NULL);
