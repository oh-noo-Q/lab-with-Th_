package entities.bound;

public class UndefinedBound implements IFunctionConfigBound {
    public static final String UNDEFINED = "N/A";

    public UndefinedBound() {
    }

    public String show() {
        return UNDEFINED;
    }
}
