package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.SpecialStructTypedefNode;
import entities.parser.object.StructNode;
import entities.parser.object.StructTypedefNode;
import entities.search.SearchCondition;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class StructNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof StructNode || n instanceof SpecialStructTypedefNode || n instanceof StructTypedefNode;
    }
}
