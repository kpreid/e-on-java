package org.erights.e.elang.syntax.antlr;

import antlr.BaseAST;
import antlr.DumpASTVisitor;
import antlr.TokenMultiBuffer;
import antlr.TokenStream;
import antlr.collections.AST;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class eMain {

    private eMain() {
    }

    public static void main(String[] args) throws Exception {
        BaseAST.setVerboseStringConversion(true, EParser._tokenNames);
        File f = new File(args[0]);
        if (f.isFile()) {
            displayOne(f);
        } else {
            processOne(f);
        }
    }

    public static void processAll(File[] args) throws Exception {
        for (int i = 0, max = args.length; i < max; i++) {
            processOne(args[i]);
        }
    }

    public static void processOne(File f) throws Exception {
        if (f.isFile()) {
            String arg = f.getName();
            if (arg.endsWith(".e") || arg.endsWith(".e-awt") ||
              arg.endsWith(".e-swt") || arg.endsWith(".caplet") ||
              arg.endsWith(".emaker")) {

                parseOne(f);
            }
        } else {
            File[] nested = f.listFiles();
            if (nested != null) {
                processAll(nested);
            }
        }
    }

    static void parseOne(File arg) throws IOException {
        FileInputStream in = new FileInputStream(arg);
        try {
            System.out.print("Parsing " + arg.getName() + "...");
            EParser parser = makeParser(in, arg.getName());
            parser.start();
            //System.out.print("*");
            System.out.println("... done");
//            AST results = parser.getAST();
//            DumpASTVisitor visitor = new DumpASTVisitor();
//            visitor.visit(results);
//            if (results != null) {
//                System.out.println(results.toStringTree());
//            }
        } catch (Exception e) {
            System.out.println("...exception: " + e);
            e.printStackTrace();
        } finally {
            in.close();
        }
    }

    static void displayOne(File arg) throws IOException {
        FileInputStream in = new FileInputStream(arg);
        System.out.print("Parsing " + arg.getName() + "...");
        EParser parser = makeParser(in, arg.getName());
        try {
            parser.start();
            System.out.println("... done");
        } catch (Exception e) {
            System.out.println("...exception: " + e);
            e.printStackTrace();
        } finally {
            in.close();
            AST results = parser.getAST();
            DumpASTVisitor visitor = new DumpASTVisitor();
            visitor.visit(results);
            if (results != null) {
                System.out.println(results.toStringTree());
            }
        }
    }

    static private EParser makeParser(FileInputStream in, String fname) {
        EALexer elexer = new EALexer(new DataInputStream(in));
        QuasiLexer qlexer = new QuasiLexer(elexer.getInputState());
        String[] names = {"e", "quasi"};
        TokenStream[] streams = {elexer, qlexer};
        TokenMultiBuffer tb = new TokenMultiBuffer(names, streams);
        elexer.setSelector(tb);
        qlexer.setSelector(tb);
        elexer.setFilename(fname);
        qlexer.setFilename(fname);

//        TokenStreamSelector selector = new TokenStreamSelector();
//        elexer.addToSelector(selector, "e");
//        qlexer.addToSelector(selector,"quasi");
        //elexer.setTokenObjectClass("org.quasiliteral.antlr.AstroToken");
        //qlexer.setTokenObjectClass("org.quasiliteral.antlr.AstroToken");
        //elexer.setSchema(new BaseSchema("e", ConstList.fromArray(EParser._tokenNames), "CHAR_LITERAL", "INT", "float64","STRING"));
//        selector.select("e");
        EParser parser = new EParser(tb);
        parser.setFilename(fname);
        //parser.setSelector(tb);
        //parser.setASTNodeClass("org.quasiliteral.antlr.AstroAST");
        return parser;
    }
}
