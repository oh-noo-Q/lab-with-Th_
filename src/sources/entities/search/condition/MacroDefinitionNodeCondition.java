package entities.search.condition;

import entities.parser.object.ExeNode;
import entities.parser.object.INode;
import entities.parser.object.MacroDefinitionNode;
import entities.search.SearchCondition;

public class MacroDefinitionNodeCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        return n instanceof MacroDefinitionNode;
    }
}
