package antlr.debug;

public class SyntacticPredicateEvent extends GuessingEvent {

    static private final long serialVersionUID = -7875442876979766267L;


    public SyntacticPredicateEvent(Object source) {
        super(source);
    }

    public SyntacticPredicateEvent(Object source, int type) {
        super(source, type);
    }

    /**
     * This should NOT be called from anyone other than ParserEventSupport!
     */
    void setValues(int type, int guessing) {
        super.setValues(type, guessing);
    }

    public String toString() {
        return "SyntacticPredicateEvent [" + getGuessing() + "]";
    }
}
