// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// PrimChar16.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "PrimChar16.h"
#include "VTable.h"

static Method *Char16Methods[] = {NULL};

Script *Char16Script = new VTable(0, Char16Methods, NULL);
