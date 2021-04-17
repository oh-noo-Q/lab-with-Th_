package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.StructNode;
import entities.parser.object.StructTypedefNode;
import entities.parser.object.UnionNode;
import entities.search.SearchCondition;

public class UnionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof UnionNode;
    }
}
