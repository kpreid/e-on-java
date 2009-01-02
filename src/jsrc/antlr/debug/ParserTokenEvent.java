package antlr.debug;

public class ParserTokenEvent extends Event {

    static private final long serialVersionUID = 7597161360957105062L;

    private int value;
    private int amount;
    static public int LA = 0;
    static public int CONSUME = 1;


    public ParserTokenEvent(Object source) {
        super(source);
    }

    public ParserTokenEvent(Object source, int type, int amount, int value) {
        super(source);
        setValues(type, amount, value);
    }

    public int getAmount() {
        return amount;
    }

    public int getValue() {
        return value;
    }

    void setAmount(int amount) {
        this.amount = amount;
    }

    void setValue(int value) {
        this.value = value;
    }

    /**
     * This should NOT be called from anyone other than ParserEventSupport!
     */
    void setValues(int type, int amount, int value) {
        super.setValues(type);
        setAmount(amount);
        setValue(value);
    }

    public String toString() {
        if (getType() == LA) {
            return "ParserTokenEvent [LA," + getAmount() + "," + getValue() +
              "]";
        } else {
            return "ParserTokenEvent [consume,1," + getValue() + "]";
        }
    }
}
