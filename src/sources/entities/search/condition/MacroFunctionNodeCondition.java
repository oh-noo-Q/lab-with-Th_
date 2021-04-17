package entities.search.condition;

import entities.parser.object.AbstractFunctionNode;
import entities.parser.object.INode;
import entities.parser.object.MacroFunctionNode;
import entities.search.SearchCondition;

public class MacroFunctionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof MacroFunctionNode;
    }
}
