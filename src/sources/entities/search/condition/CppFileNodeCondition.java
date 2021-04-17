package entities.search.condition;

import entities.parser.object.CppFileNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

public class CppFileNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof CppFileNode;
    }
}
