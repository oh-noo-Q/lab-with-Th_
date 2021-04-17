package entities.search.condition;

import entities.parser.object.AbstractFunctionNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

public class AbstractFunctionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof AbstractFunctionNode;
    }
}
