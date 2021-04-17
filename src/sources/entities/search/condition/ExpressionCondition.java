package entities.search.condition;

import entities.parser.object.INode;
import entities.search.SearchCondition;
import entities.solverhelper.expression.ExpressionNode;

public class ExpressionCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof ExpressionNode;
    }
}
