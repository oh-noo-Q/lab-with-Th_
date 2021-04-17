package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.IVariableNode;
import entities.parser.object.VariableNode;
import entities.search.SearchCondition;

/**
 * Represent extern variable, e.g., "extern int MY_MAX_VALUE"
 *
 * @author DucAnh
 */
public class ExternVariableNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof VariableNode && ((IVariableNode) n).isExtern();
    }
}
