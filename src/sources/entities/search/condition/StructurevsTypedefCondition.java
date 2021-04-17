package entities.search.condition;

import entities.parser.object.*;
import entities.search.SearchCondition;

public class StructurevsTypedefCondition extends SearchCondition {

    @Override
    public boolean isSatisfiable(INode n) {
        if (n instanceof StructureNode || n instanceof ITypedefDeclaration)
            return true;
        else return n instanceof VariableNode && !(n.getParent() instanceof FunctionNode);
    }
}
