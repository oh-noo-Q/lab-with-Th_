package entities.search.condition;

import entities.parser.object.ClassNode;
import entities.parser.object.INode;
import entities.parser.object.NamespaceNode;
import entities.parser.object.StructNode;
import entities.search.SearchCondition;

public class ClassvsStructvsNamespaceCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof ClassNode || n instanceof StructNode || n instanceof NamespaceNode;
    }
}
