// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
//
// PrimString.cpp:
//
//////////////////////////////////////////////////////////////////////

#include "PrimString.h"
#include "VTable.h"

static Method *PrimString8Methods[] = {NULL};

Script *PrimString8Script = new VTable(0, PrimString8Methods, NULL);


static Method *PrimString16Methods[] = {NULL};

Script *PrimString16Script = new VTable(0, PrimString16Methods, NULL);

