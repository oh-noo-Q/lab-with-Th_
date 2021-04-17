package entities.search.condition;

import entities.parser.object.CFileNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;

public class CFileNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof CFileNode;
    }
}
