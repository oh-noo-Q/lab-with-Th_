package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.StructTypedefNode;
import entities.search.SearchCondition;

public class StructTypedefNodeCondifion extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof StructTypedefNode;
    }
}
