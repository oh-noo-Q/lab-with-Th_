package entities.solverhelper.memory;

/**
 * Represent one level pointer
 *
 * @author ducanh
 */
public class TwoLevelSymbolicVariable extends PointerSymbolicVariable {

    public TwoLevelSymbolicVariable(String name, String type, int scopeLevel) {
        super(name, type, scopeLevel);
    }
}
