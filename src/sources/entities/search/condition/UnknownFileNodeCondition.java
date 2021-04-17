package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.UnknowObjectNode;
import entities.search.SearchCondition;

public class UnknownFileNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof UnknowObjectNode;
    }
}
