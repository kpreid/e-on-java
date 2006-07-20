// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html

// setuputil.c - some stuff for supporting E installation on Windows

#include "error_utils.h"
#include "string_utils.h"
#include "file_utils.h"

int main(int argc, char *argv[]) {
    char *templateFile;
    char *outFile;
    char *templateText;
    char *outText;
    require(argc >= 3,
            "Usage",
            "subst_test templateFile outFile args...");
    templateFile = argv[1];
    outFile = argv[2];
    argc -= 3;
    argv += 3;
    
    templateText = getFileText(templateFile);
    outText = substitute(templateText, argc, (const char **)argv);
    setFileText(outFile, outText);
    return 0;
}
