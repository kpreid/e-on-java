/* Copyright 2002 Combex, Inc. under the terms of the MIT X license */
/* found at http://www.opensource.org/licenses/mit-license.html */

/* reg_test.c - tests registry access function */

#include <stdio.h>
#include <windows.h>

#include "error_utils.h"
#include "string_utils.h"
#include "reg_utils.h"

int main(int argc, char *argv[]) {
    char *cmd;
    HKEY root;
    DWORD type;

    /* setFatalErrorHandler(monologueBoxHandler); */
    require(argc >= 2, "Usage", "reg_test command args...");

    root = OpenKey(HKCR, "playtime");

    cmd = argv[1];
    if (streq(cmd, "open")) {
        require(3 == argc, "Usage", "reg_test open subkey");
        if (NULL == OpenKey(root, argv[2])) {
            printf("key %s not found\n", argv[2]);
        }

    } else if (streq(cmd, "create")) {
        require(3 == argc, "Usage", "reg_test create subkey");
        CreateKey(root, argv[2]);

    } else if (streq(cmd, "set")) {
        require(4 == argc, "Usage", "reg_test set name value");
        SetStringValue(root, NULL, argv[2], argv[3]);

    } else if (streq(cmd, "get")) {
        require(3 == argc, "Usage", "reg_test get name");
        type = GetValueType(root, NULL, argv[2]);
        if (REG_SZ == type) {
            printf("%s\n", GetStringValue(root, NULL, argv[2]));
        } else if (REG_ABSENT == type) {
            printf("value %s not found\n", argv[2]);
        } else {
            printf("value %s of type %d\n", argv[2], (int)type);
        }
    } else {
        fatalError("Unrecognized command", cmd);
    }
    return 0;
}

