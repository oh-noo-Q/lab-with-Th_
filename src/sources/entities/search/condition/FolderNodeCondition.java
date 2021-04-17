package entities.search.condition;

import entities.parser.object.FolderNode;
import entities.parser.object.INode;
import entities.search.SearchCondition;


public class FolderNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof FolderNode;
    }
}
