package test.foo;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * Demonstrates a bug in Sun's j2sdk1.4.1's javac compiler.
 * <p/>
 * This class itself compiles, as it should, whether or not it is declared
 * abstract. However, if it is declared abstract, then its concrete subclass,
 * {@link test.bar.Class3}, refuses to compile:
 * <pre>
 * test/bar/Class3.java:14: test.bar.Class3 should be declared abstract; \
 *     it does not define zip() in test.foo.Class1
 * public class Class3 extends Class2 {
 *        ^
 * 1 error
 * </pre>
 * This is wrong, because the abstract zip() from {@link Class1} is defined
 * here in Class2. But perhaps because it is package (ie default) scope,
 * and Class3 is in a different package, the compiler misses this when
 * Class2 is abstract.
 *
 * @author Mark S. Miller
 */
public abstract class Class2 extends Class1 {

    void zip() {
    }
}
