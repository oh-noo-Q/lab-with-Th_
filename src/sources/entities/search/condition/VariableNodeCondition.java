package entities.search.condition;

import entities.parser.object.CloneVariableNode;
import entities.parser.object.INode;
import entities.parser.object.VariableNode;
import entities.search.SearchCondition;

public class VariableNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof VariableNode && !(n instanceof CloneVariableNode);
    }
}
