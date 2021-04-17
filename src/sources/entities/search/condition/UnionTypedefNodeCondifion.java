package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.UnionTypedefNode;
import entities.search.SearchCondition;

/**
 * Created by DucToan on 14/07/2017.
 */
public class UnionTypedefNodeCondifion extends SearchCondition {
    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof UnionTypedefNode;
    }
}
