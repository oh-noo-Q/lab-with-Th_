package entities.parser.dependency;

import entities.parser.ProjectParser;
import entities.parser.dependency.finder.VariableFinder;
import entities.parser.object.FunctionNode;
import entities.parser.object.IFunctionNode;
import entities.parser.object.INode;
import entities.search.Search;
import entities.search.condition.FunctionNodeCondition;
import entities.UETLogger;
import entities.parser.object.INode;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;

import java.io.File;

public class GlobalVariableDependencyGeneration extends AbstractDependencyGeneration {
    final static UETLogger logger = UETLogger.get(GlobalVariableDependencyGeneration.class);

    public static void main(String[] args) {
//        ProjectParser parser = new ProjectParser(new File(Paths.JOURNAL_TEST));
//        parser.setExpandTreeuptoMethodLevel_enabled(true);
//        parser.setGlobalVarDependencyGeneration_enabled(true);
//
//        FunctionNode function = (FunctionNode) Search
//                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "quickSortRecur(struct Node*,struct Node*)").get(0);
//        for (Dependency d:function.getDependencies())
//            logger.debug(d);
    }

    public GlobalVariableDependencyGeneration() {
    }

    public void dependencyGeneration(INode root) {
        if (root instanceof IFunctionNode) {
            IFunctionNode owner = (IFunctionNode) root;

            if (!owner.isGlobalVariableDependencyState()) {

                IASTFunctionDefinition fnAst = owner.getAST();

                ASTVisitor visitor = new ASTVisitor() {
                    @Override
                    public int visit(IASTExpression expression) {
                        if (expression instanceof IASTIdExpression) {
                            String variableName = expression.getRawSignature();
                            VariableFinder finder = new VariableFinder(owner);
                            try {
                                INode refferedNode = finder.find(variableName);
                                if (refferedNode != null) {
                                    GlobalVariableDependency d = new GlobalVariableDependency(owner, refferedNode);
                                    if (owner.getAbsolutePath().contains("quickSortRecur(struct Node*,struct Node*)")){
                                        int a = 0;
                                    }
                                    if (!owner.getDependencies().contains(d)
                                            && !refferedNode.getDependencies().contains(d)) {
                                        owner.getDependencies().add(d);
                                        refferedNode.getDependencies().add(d);

                                        logger.debug("Found a global dependency: " + d.toString());
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                        return PROCESS_CONTINUE;
                    }
                };

                visitor.shouldVisitExpressions = true;
                fnAst.accept(visitor);
                owner.setGlobalVariableDependencyState(true);
            } else {
                logger.debug(owner.getAbsolutePath() + " is analyzed global dependency before");
            }

        }
    }
}
