package entities.search.condition;

import entities.parser.object.INode;
import entities.search.SearchCondition;
import entities.solverhelper.expression.OperatorNode;

public class OperatorCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof OperatorNode;
    }
}
