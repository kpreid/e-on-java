package antlr.debug;

public class Tracer extends TraceAdapter {

    String indent = ""; // TBD: should be StringBuffer


    protected void dedent() {
        if (2 > indent.length()) {
            indent = "";
        } else {
            indent = indent.substring(2);
        }
    }

    public void enterRule(TraceEvent e) {
        System.out.println(indent + e);
        indent();
    }

    public void exitRule(TraceEvent e) {
        dedent();
        System.out.println(indent + e);
    }

    protected void indent() {
        indent += "  ";
    }
}
