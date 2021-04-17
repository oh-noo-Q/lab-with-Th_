package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.Node;
import entities.search.SearchCondition;

public class NodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof Node;
    }
}
