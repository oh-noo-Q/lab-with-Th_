package entities.search.condition;

import entities.parser.object.CppFileNode;
import entities.parser.object.INode;
import entities.parser.object.NamespaceNode;
import entities.search.SearchCondition;

public class NamespaceNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof NamespaceNode;
    }
}
