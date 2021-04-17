package entities.solverhelper.normalstatementparser;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import entities.solverhelper.memory.VariableNodeTable;

/**
 * Ex: c = (char) x
 *
 * @author ducanhnguyen
 */
public class TypeCastingExpressionParser extends NormalBinaryAssignmentParser {
    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {

        super.parse(ast, table);
    }
}
