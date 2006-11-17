package org.quasiliteral.text;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.prim.E;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.EList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.Twine;
import org.erights.e.meta.java.io.FileGetter;
import org.erights.e.meta.java.io.FileSugar;
import org.quasiliteral.base.MatchMaker;
import org.quasiliteral.base.ValueMaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


/**
 * Post-processes the output of byacc/Java (Berkeley Yacc for Java) so that it
 * is valid Java.
 * <p/>
 * The problem with byacc/Java's output is simply that it's too large.
 * Specifically, Java turns the table initializers into bytecodes to perform
 * the initialization. These bytecodes exceed Java's limit of 64KB per method.
 * To work around this, EYaccFixer parses the table initializations, serializes
 * the resulting tables into a file to be included as a resource in the e.jar
 * file, and changes the table initializations to initialize these tables by
 * unserializing these resources.
 *
 * @author Mark S. Miller
 */
public class EYaccFixer {

    static private final SimpleQuasiParser QParser = SimpleQuasiParser.THE_ONE;

    static private final MatchMaker ParseQPatt =
      QParser.matchMaker(Twine.fromString("@{0}\n" +
        "final static short yytable[] = {@{1}};\n" +
        "final static short yycheck[] = {@{2}};\n" + "@{3}int yyparse()@{4}"));

    static private ValueMaker getParseQExpr(String rPath) {
        return QParser.valueMaker(Twine.fromString(
          "// Fixed by EYaccFixer to meet jvm size limits\n" + "${0}\n\n" +
            "//These two tables are not statically initialized, but rather\n" +
            "//initialized on first use, so that a failure to initialize\n" +
            "//them can successfully report the problem.\n" +
            "static private short[] yytable = null;\n" +
            "static private short[] yycheck = null;\n" +
            "/** Ensures that yytable and yycheck are initialized. */\n" +
            "static private void initTables() {\n" +
            "    if (null != yycheck) {\n" + "        return;\n" + "    }\n" +
            "    try {\n" + "        String rName = \"" + rPath + "\";\n" +
            "        InputStream inp = \n" +
            "          ClassLoader.getSystemResourceAsStream(rName);\n" +
            "        if (null == inp) {\n" +
            "            T.fail(rName + \" not found\");\n" + "        }\n" +
            "        ObjectInput obInp = new ObjectInputStream(inp);\n" +
            "        yytable = (short[])obInp.readObject();\n" +
            "        yycheck = (short[])obInp.readObject();\n" +
            "        long hash = EYaccFixer.checkhash(yytable, yycheck);\n" +
            "        if (hash != ${5}) {\n" +
            "            T.fail(rName + \" bad checkhash: \" +\n" +
            "                                       hash);\n" + "        }\n" +
            "    } catch (Exception ex) {\n" +
            "        throw new NestedException(ex, \"# initing parser\");\n" +
            "    }\n" + "}\n\n" + "${3}int yyparse() ${4}"));
    }

    /**
     * Used to make sure that the tables read in to the transformed parser are
     * the same as the tables extracted from the pre-transformed parser.
     * Ideally, this should be a hash function that's good at detecting errors,
     * like a CRC. But for now we just do something crude.
     */
    static public long checkhash(short[] yytable, short[] yycheck) {
        long result = 0;
        for (int i = 0; i < yytable.length; i++) {
            result = result * 3 + yytable[i];
        }
        for (int i = 0; i < yycheck.length; i++) {
            result = result * 3 + yycheck[i];
        }
        return result;
    }

    static private short[] eatMyShorts(String numbers) {
        FlexList result = FlexList.fromType(Short.TYPE, 1000);
        int len = numbers.length();
        int i = 0;
        while (i < len && !Character.isDigit(numbers.charAt(i))) {
            i++;
        }
        while (i < len) {
            int num = 0;
            while (i < len && Character.isDigit(numbers.charAt(i))) {
                num = num * 10 + Character.digit(numbers.charAt(i), 10);
                i++;
            }
            if (num > Short.MAX_VALUE || num < 0) {
                throw new ArithmeticException("Must be a positive short");
            }
            result.push(new Short((short)num));
            while (i < len && !Character.isDigit(numbers.charAt(i))) {
                i++;
            }
        }
        return (short[])result.getArray(Short.TYPE);
    }

    /**
     *
     */
    static public void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("usage: EYaccFixer src rRootDir rPath");
            System.exit(-1);
        }

        File srcFile = (File)FileGetter.THE_ONE.get(args[0]);
        File rRootDir = (File)FileGetter.THE_ONE.get(args[1]);
        String rPath = args[2];
        File rFile = FileSugar.get(rRootDir, rPath);
        File rParentDir = (File)FileGetter.THE_ONE.get(rFile.getParent());
        FileSugar.mkdirs(rParentDir, null);

        String original = FileSugar.getText(srcFile);
        EList parts =
          ParseQPatt.matchBind(ConstList.EmptyList, original, null);
        parts = parts.diverge();
        short[] yytable = eatMyShorts(parts.get(1).toString());
        short[] yycheck = eatMyShorts(parts.get(2).toString());
        OutputStream outp = new FileOutputStream(rFile);
        ObjectOutput obOutp = new ObjectOutputStream(outp);
        obOutp.writeObject(yytable);
        obOutp.writeObject(yycheck);
        obOutp.flush(); //redundant, but I'm paranoid.
        obOutp.close();

        String hash = new Long(checkhash(yytable, yycheck)).toString() + "L";
        ((FlexList)parts).push(hash);

        ValueMaker parseQExpr = getParseQExpr(rPath);
        FileSugar.setText(srcFile,
                          E.toString(parseQExpr.substitute(parts.snapshot())));
        System.err.println("" + args[0] + " generated\n");
        System.exit(0);
    }
}
