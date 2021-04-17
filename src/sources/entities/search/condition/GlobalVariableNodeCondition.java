package entities.search.condition;

import entities.parser.object.ExternalVariableNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

/**
 * Represent global or extern variable, e.g., "int MY_MAX_VALUE"
 *
 * @author TungLam
 */
public class GlobalVariableNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof ExternalVariableNode;
    }
}
