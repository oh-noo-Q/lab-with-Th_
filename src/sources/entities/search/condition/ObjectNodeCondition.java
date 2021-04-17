package entities.search.condition;

import entities.parser.object.*;
import entities.search.SearchCondition;

public class ObjectNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof ObjectNode;
    }

}
