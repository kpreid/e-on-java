// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// PrimNull.cpp: 
//
//////////////////////////////////////////////////////////////////////

#include "PrimNull.h"
#include "VTable.h"

static Method *NullMethods[] = {NULL};

Script *NullScript = new VTable(0, NullMethods, NULL);
