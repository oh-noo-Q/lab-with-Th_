package entities.solverhelper.normalstatementparser;

import entities.IRegex;
import entities.utils.Utils;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTBinaryExpression;

import entities.solverhelper.ExpressionRewriterUtils;
import entities.solverhelper.memory.ISymbolicVariable;
import entities.solverhelper.memory.LogicBlock;
import entities.solverhelper.memory.OneDimensionSymbolicVariable;
import entities.solverhelper.memory.OneLevelSymbolicVariable;
import entities.solverhelper.memory.PhysicalCell;
import entities.solverhelper.memory.VariableNodeTable;

/**
 * Parse new statement, e.g., "new int[3]"
 *
 * @author ducanhnguyen
 */
public class NewBinaryAssignmentParser extends BinaryAssignmentParser {

    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);
        if (ast instanceof ICPPASTBinaryExpression) {
            IASTExpression right = ((CPPASTBinaryExpression) ast).getOperand2();
            String nameVar = ((CPPASTBinaryExpression) ast).getOperand1().getRawSignature();
            /*
			 * Ex: new int[3]
			 */
            String sizeInStr = right.getChildren()[0] // Ex: int[3]
                    .getChildren()[1]// Ex: [3]
                    .getChildren()[0]// Ex: [3]
                    .getChildren()[0]// Ex: 3
                    .getRawSignature();

            sizeInStr = ExpressionRewriterUtils.rewrite(table, sizeInStr);

            if (sizeInStr.matches(IRegex.POSITIVE_INTEGER_REGEX)) {
                int size = Utils.toInt(sizeInStr);

                ISymbolicVariable var = table.findorCreateVariableByName(nameVar);

                LogicBlock block;
                if (var instanceof OneLevelSymbolicVariable)
                    block = ((OneLevelSymbolicVariable) var).getReference().getBlock();

                else if (var instanceof OneDimensionSymbolicVariable)
                    block = ((OneDimensionSymbolicVariable) var).getBlock();
                else
                    throw new Exception("Dont support " + right.getRawSignature());

                if (block != null)
                    for (int i = 0; i < size; i++) {
                        PhysicalCell newCell = new PhysicalCell(PhysicalCell.DEFAULT_VALUE);
                        String index = i + "";
                        block.addLogicalCell(newCell, index);
                    }
            } else
                throw new Exception("Dont support unspecified size of pointer in: " + right.getRawSignature());
        }
    }
}