// This code is hereby placed in the public domain - Bill Frantz, author

package org.erights.e.elib.prim.tests;

import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.MethodNode;
import org.erights.e.elib.prim.StaticMaker;

import java.lang.reflect.Method;
import java.math.BigInteger;


/**
 * @author Bill Frantz
 */
public class ArithTimer {

    static final int LOOP_COUNT = 1000000;
    static final int PASSES = 3;

    private ArithTimer() {
    }

    public static void main(String[] argv) {

        int repeat;
        for (repeat = 0; PASSES > repeat; repeat++) {
            int a;
            int b = 5;
            int c = 7;
            System.out.println("Start int test");
            long startTime = System.currentTimeMillis();
            int i;
            for (i = 0; LOOP_COUNT > i; i++) {
                a = b + c;
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.println("Start int subroutine test");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                a = intAdd(b, c);
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            Integer ia;
            Integer ib = new Integer(5);
            Integer ic = new Integer(7);
            System.out.println("Start Integer test");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                ia = new Integer(ib.intValue() + ic.intValue());
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.println("Start Integer Subroutine test");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                ia = integerAdd(ib, ic);
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.println("Start Integer CRAPI test");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                try {
                    Class[] ca = {Integer.class, Integer.class};
                    Method meth = ArithTimer.class.getMethod("integerAdd", ca);
                    Object[] parms = {ib, ic};
                    ia = (Integer)meth.invoke(null, parms);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.print("Start Integer cached CRAPI test");
            Method integerAddMeth = null;
            try {
                Class[] ca = {Integer.class, Integer.class};
                integerAddMeth = ArithTimer.class.getMethod("integerAdd", ca);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            Object[] integerParms = {ib, ic};
            System.out.println(".");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                try {
                    ia = (Integer)integerAddMeth.invoke(null, integerParms);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.print("Start int cached CRAPI test");
            Method intAddMeth = null;
            try {
                Class[] ca = {Integer.TYPE, Integer.TYPE};
                intAddMeth = ArithTimer.class.getMethod("intAdd", ca);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            System.out.println(".");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                try {
                    ia = (Integer)intAddMeth.invoke(null, integerParms);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.print("Start E.call Integer test");
            StaticMaker sm = StaticMaker.make(ArithTimer.class);
            System.out.println(".");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                try {
                    E.call(sm, "intAdd", ib, ic);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.print("Start cached E.call Integer test");
            MethodNode mn = sm.getVTable().optMethod("intAdd", 2);
            System.out.println(".");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                try {
                    mn.execute(sm, "intAdd", integerParms);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));

            System.out.print("Start E.call BigInteger test");
            BigInteger bib = BigInteger.valueOf(b);
            BigInteger bic = BigInteger.valueOf(c);
            System.out.println(".");
            startTime = System.currentTimeMillis();
            for (i = 0; LOOP_COUNT > i; i++) {
                try {
                    E.call(sm, "intAdd", bib, bic);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            System.out
              .println(
                "End, elapse " + (System.currentTimeMillis() - startTime));
        }
    }

    public static int intAdd(int b, int c) {
        return b + c;
    }

    public static Integer integerAdd(Integer ib, Integer ic) {
        return new Integer(ib.intValue() + ic.intValue());
    }
}
