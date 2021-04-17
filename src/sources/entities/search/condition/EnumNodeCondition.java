package entities.search.condition;

import entities.parser.object.EnumNode;
import entities.parser.object.EnumTypedefNode;
import entities.parser.object.INode;
import entities.parser.object.SpecialEnumTypedefNode;
import entities.search.SearchCondition;

/**
 * Demo a condition
 *
 * @author DucAnh
 */
public class EnumNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof EnumNode || n instanceof SpecialEnumTypedefNode || n instanceof EnumTypedefNode;
    }
}
