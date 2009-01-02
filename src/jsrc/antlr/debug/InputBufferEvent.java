package antlr.debug;

public class InputBufferEvent extends Event {

    static private final long serialVersionUID = 5937953044148720411L;

    char c;
    int lookaheadAmount; // amount of lookahead
    static public final int CONSUME = 0;
    static public final int LA = 1;
    static public final int MARK = 2;
    static public final int REWIND = 3;


    /**
     * CharBufferEvent constructor comment.
     *
     * @param source java.lang.Object
     */
    public InputBufferEvent(Object source) {
        super(source);
    }

    /**
     * CharBufferEvent constructor comment.
     *
     * @param source java.lang.Object
     */
    public InputBufferEvent(Object source,
                            int type,
                            char c,
                            int lookaheadAmount) {
        super(source);
        setValues(type, c, lookaheadAmount);
    }

    public char getChar() {
        return c;
    }

    public int getLookaheadAmount() {
        return lookaheadAmount;
    }

    void setChar(char c) {
        this.c = c;
    }

    void setLookaheadAmount(int la) {
        lookaheadAmount = la;
    }

    /**
     * This should NOT be called from anyone other than ParserEventSupport!
     */
    void setValues(int type, char c, int la) {
        super.setValues(type);
        setChar(c);
        setLookaheadAmount(la);
    }

    public String toString() {
        return "CharBufferEvent [" +
          (CONSUME == getType() ? "CONSUME, " : "LA, ") + getChar() + "," +
          getLookaheadAmount() + "]";
    }
}
