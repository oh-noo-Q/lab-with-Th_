package entities.solverhelper.normalstatementparser;

import entities.utils.Utils;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import entities.solverhelper.ExpressionRewriterUtils;
import entities.solverhelper.memory.VariableNodeTable;

public class ConditionParser extends StatementParser {

    private String newConstraint = "";

    @Override
    public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);
        newConstraint = ExpressionRewriterUtils.rewrite(table, ast.getRawSignature());
    }

    public String getNewConstraint() {
        return newConstraint;
    }

    public void setNewConstraint(String newConstraint) {
        this.newConstraint = newConstraint;
    }
}
