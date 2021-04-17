package entities.search.condition;

import entities.parser.object.ExeNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

public class ExeNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof ExeNode;
    }
}
