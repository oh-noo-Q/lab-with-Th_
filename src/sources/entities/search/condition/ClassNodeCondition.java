package entities.search.condition;

import entities.parser.object.ClassNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

public class ClassNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof ClassNode;
    }
}
