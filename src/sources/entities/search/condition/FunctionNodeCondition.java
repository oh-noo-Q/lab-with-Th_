package entities.search.condition;

import entities.parser.object.IFunctionNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class FunctionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof IFunctionNode;
    }
}
