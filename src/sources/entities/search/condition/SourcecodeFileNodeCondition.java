package entities.search.condition;


import entities.parser.object.INode;
import entities.parser.object.SourcecodeFileNode;
import entities.search.SearchCondition;

public class SourcecodeFileNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof SourcecodeFileNode;
    }
}
