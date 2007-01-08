package antlr.debug;

import java.util.EventObject;

public abstract class Event extends EventObject {

    static private final long serialVersionUID = -7031312406674205238L;

    private int type;


    public Event(Object source) {
        super(source);
    }

    public Event(Object source, int type) {
        super(source);
        setType(type);
    }

    public int getType() {
        return type;
    }

    void setType(int type) {
        this.type = type;
    }

    /**
     * This should NOT be called from anyone other than ParserEventSupport!
     */
    void setValues(int type) {
        setType(type);
    }
}
