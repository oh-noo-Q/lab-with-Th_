package entities.search.condition;

import entities.parser.object.EnumTypedefNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

/**
 * Created by DucToan on 14/07/2017.
 */
public class EnumTypedefNodeCondifion extends SearchCondition {
    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof EnumTypedefNode;
    }
}
