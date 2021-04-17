package entities.search.condition;

import entities.parser.object.INode;
import entities.parser.object.TypedefDeclaration;
import entities.search.SearchCondition;

public class TypedefNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof TypedefDeclaration;
    }
}
