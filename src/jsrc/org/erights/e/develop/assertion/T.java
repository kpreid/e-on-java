package org.erights.e.develop.assertion;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

/**
 * This class provides assert and require checks you can add to your program.
 * <p/>
 * If the the checks fail, unchecked exceptions of class RuntimeException are
 * thrown.
 * <p/>
 * Once E can commit to Java 1.4, then calls to static assertion methods of
 * this class (the 'test' methods) should be converted to the new Java 1.4
 * "assert" syntax, in which case they can be switched off, or possibly
 * stripped from the code. Therefore, the meaning of a program should be
 * insensitive to whether an assertion check (a 'test') is performed.
 * <p/>
 * Calls to static 'require' methods of this class are non-optional -- they are
 * considered part of the program, and may not be removed.
 * <p/>
 * History: Brian Marick wrote the original as the class 'Assertion', designed
 * to work with his unassert utility, which was unfortunately not open sourced
 * with the rest of E. Now that Java 1.4 will be providing us an alternative
 * mechanism before we can expect to revive this, Mark S. Miller removed the
 * stripping support mechanism, while also renaming 'Assertion' to 'T', and
 * duplicated 'test' as 'require' in order to have both kinds of check. Most
 * previous users of 'test' have been moved to 'require', as it wasn't clear
 * whether the test was optional for them.
 *
 * @author Brian Marick
 * @author Mark S. Miller
 */
public final class T {

    /**
     * suppress instantiation
     */
    private T() {
    }

    /**
     * Does nothing.
     * <p/>
     * Exists so other code can call this in order to give a programmer a
     * convenient place (the call site) at which to place a breakpoint.
     */
    static public void noop() {
    }

    /**
     * If this method is executed, it throws a RuntimeException with the
     * message "Failed: 'Unreachable' code was reached."  Plant such assertions
     * in places the program should never reach (such as the default case in a
     * switch).
     */
    static public void fail() {
        fail("'Unreachable' code was reached.");
    }

    /**
     * If this method is executed, it throws a RuntimeException with the given
     * explanation, prefixed by "Failed: ".
     * <p/>
     * Must errors thrown by the E implementation go through this point, so if
     * you're running E in a Java debugger, this is a great place to put a
     * breakpoint.
     */
    static public void fail(String explanation) {
        throw new RuntimeException("Failed: " + explanation);
    }

    /**
     * If the argument is false, throws a RuntimeException with the message
     * "Failed: Assertion failed."
     */
    static public void test(boolean mustBeTrue) {
        if (false == mustBeTrue) {
            fail("Assertion failed.");
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation, prefixed by "Failed: ".
     */
    static public void test(boolean mustBeTrue, String explanation) {
        if (false == mustBeTrue) {
            fail(explanation);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ".
     * <p/>
     * Use this routine when you need to avoid paying the overhead of string
     * concatenation ("+") on every test. It does the concatenation only if the
     * test fails.
     */
    static public void testSI(boolean mustBeTrue,
                              Object explanation0,
                              int explanation1) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ".
     * <p/>
     * Use this routine when you need to avoid paying the overhead of string
     * concatenation ("+") on every test. It does the concatenation only if the
     * test fails.
     */
    static public void test(boolean mustBeTrue,
                            Object explanation0,
                            Object explanation1) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void test(boolean mustBeTrue,
                            Object explanation0,
                            Object explanation1,
                            Object explanation2) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void test(boolean mustBeTrue,
                            Object explanation0,
                            Object explanation1,
                            Object explanation2,
                            Object explanation3) {
        if (false == mustBeTrue) {
            fail(
              "" + explanation0 + explanation1 + explanation2 + explanation3);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void test(boolean mustBeTrue,
                            Object explanation0,
                            Object explanation1,
                            Object explanation2,
                            Object explanation3,
                            Object explanation4) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void test(boolean mustBeTrue,
                            Object explanation0,
                            Object explanation1,
                            Object explanation2,
                            Object explanation3,
                            Object explanation4,
                            Object explanation5) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4 + explanation5);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void test(boolean mustBeTrue,
                            Object explanation0,
                            Object explanation1,
                            Object explanation2,
                            Object explanation3,
                            Object explanation4,
                            Object explanation5,
                            Object explanation6) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4 + explanation5 + explanation6);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void test(boolean mustBeTrue,
                            Object explanation0,
                            Object explanation1,
                            Object explanation2,
                            Object explanation3,
                            Object explanation4,
                            Object explanation5,
                            Object explanation6,
                            Object explanation7) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4 + explanation5 + explanation6 +
              explanation7);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation, prefixed by "Failed: ".
     */
    static public void require(boolean mustBeTrue, String explanation) {
        if (false == mustBeTrue) {
            fail(explanation);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ".
     * <p/>
     * Use this routine when you need to avoid paying the overhead of string
     * concatenation ("+") on every test. It does the concatenation only if the
     * test fails.
     */
    static public void requireSI(boolean mustBeTrue,
                                 Object explanation0,
                                 int explanation1) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ".
     * <p/>
     * Use this routine when you need to avoid paying the overhead of string
     * concatenation ("+") on every test. It does the concatenation only if the
     * test fails.
     */
    static public void require(boolean mustBeTrue,
                               Object explanation0,
                               Object explanation1) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void require(boolean mustBeTrue,
                               Object explanation0,
                               Object explanation1,
                               Object explanation2) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void require(boolean mustBeTrue,
                               Object explanation0,
                               Object explanation1,
                               Object explanation2,
                               Object explanation3) {
        if (false == mustBeTrue) {
            fail(
              "" + explanation0 + explanation1 + explanation2 + explanation3);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void require(boolean mustBeTrue,
                               Object explanation0,
                               Object explanation1,
                               Object explanation2,
                               Object explanation3,
                               Object explanation4) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void require(boolean mustBeTrue,
                               Object explanation0,
                               Object explanation1,
                               Object explanation2,
                               Object explanation3,
                               Object explanation4,
                               Object explanation5) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4 + explanation5);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void require(boolean mustBeTrue,
                               Object explanation0,
                               Object explanation1,
                               Object explanation2,
                               Object explanation3,
                               Object explanation4,
                               Object explanation5,
                               Object explanation6) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4 + explanation5 + explanation6);
        }
    }

    /**
     * If the argument is false, throws a RuntimeException with the given
     * explanation arguments, concatenated as strings and prefixed by "Failed:
     * ". <p> Use this routine when you need to avoid paying the overhead of
     * string concatenation ("+") on every test. It does the concatenation only
     * if the test fails.
     */
    static public void require(boolean mustBeTrue,
                               Object explanation0,
                               Object explanation1,
                               Object explanation2,
                               Object explanation3,
                               Object explanation4,
                               Object explanation5,
                               Object explanation6,
                               Object explanation7) {
        if (false == mustBeTrue) {
            fail("" + explanation0 + explanation1 + explanation2 +
              explanation3 + explanation4 + explanation5 + explanation6 +
              explanation7);
        }
    }

    /**
     *
     */
    static public void notNull(Object specimen, String explanation) {
        if (null == specimen) {
            throw new NullPointerException(explanation);
        }
    }

    /**
     * 
     */
    static public void notNull(Object specimen,
                               Object explanation0,
                               Object explanation1) {
        if (null == specimen) {
            throw new NullPointerException("" + explanation0 + explanation1);
        }
    }
}
