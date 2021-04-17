package entities.search.condition;

import entities.parser.object.DefinitionFunctionNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class DefinitionFunctionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof DefinitionFunctionNode;
    }
}
