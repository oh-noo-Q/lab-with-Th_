package entities.solverhelper.normalstatementparser;

import java.util.List;

import entities.utils.ASTUtils;
import entities.utils.Utils;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import entities.solverhelper.ExpressionRewriterUtils;
import entities.solverhelper.memory.PhysicalCell;
import entities.solverhelper.memory.VariableNodeTable;

/**
 * Parse multiple assignments, e.g., "x=y=z+1"
 *
 * @author ducanhnguyen
 */
public class MultipleAssignmentParser extends BinaryAssignmentParser {

    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);
        if (ast instanceof IASTBinaryExpression) {
            List<String> expressions = ASTUtils.getAllExpressionsInBinaryExpression((IASTBinaryExpression) ast);
            int last = expressions.size() - 1;

            String finalExpression = expressions.get(last);
            finalExpression = ExpressionRewriterUtils.rewrite(table, finalExpression);

			/*
             * All variable corresponding to expressions, except the final
			 * expression, is assigned to the final expression
			 */
            for (int i = 0; i < last; i++) {

                String currentExpression = expressions.get(i);
                PhysicalCell cell = table.findPhysicalCellByName(currentExpression);

                if (cell != null)
                    cell.setValue(finalExpression);
            }
        }
    }

}
