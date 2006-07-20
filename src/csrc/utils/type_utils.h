// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// type_utils.h - I can't believe I've got to do this myself

#ifndef __TYPE_UTILS
#define __TYPE_UTILS

#ifndef NULL
# define NULL ((void*)0)
#endif

/**
 * if BOOL is typedef'ed elsewhere, you must define BOOL_DEFINED
 * before including this file 
 */
#if (!defined(BOOL) && !defined(_WINDEF_) && !defined(BOOL_DEFINED))
# define BOOL int
#endif

#ifndef TRUE
# define TRUE 1
#endif

#ifndef FALSE
# define FALSE 0
#endif

#ifndef OPTIONAL
# define OPTIONAL /*param is optional, pass as NULL or 0 if not used*/
#endif

#endif /* __TYPE_UTILS */
