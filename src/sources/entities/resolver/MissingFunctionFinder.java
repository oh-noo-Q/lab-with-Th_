package entities.resolver;

import entities.parser.FunctionCallParser;
import entities.parser.dependency.finder.MethodFinder;
import entities.parser.object.ICommonFunctionNode;
import entities.parser.object.IFunctionNode;
import entities.parser.object.INode;
import entities.parser.object.SourcecodeFileNode;
import entities.search.Search;
import entities.search.condition.AbstractFunctionNodeCondition;
import entities.UETLogger;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;

import java.util.ArrayList;
import java.util.List;

public class MissingFunctionFinder implements IMissingFinder {
    private final static UETLogger logger = UETLogger.get(MissingFunctionFinder.class);
    private INode root;

    public MissingFunctionFinder(INode root) {
        if (root instanceof SourcecodeFileNode)
            this.root = root;
        else
            logger.error(root.getName() + "is not a source code file");
    }

    public List<ICommonFunctionNode> getMissingFuntion() {
        List<ICommonFunctionNode> resolved = new ArrayList<>();
        List<INode> completeFunctions = Search.searchNodes(root, new AbstractFunctionNodeCondition());

        int prevSize;
        do {
            prevSize = resolved.size();

            for (INode functionNode : completeFunctions)
                resolved.addAll(getMissingFunctionIn((IFunctionNode) functionNode));
        } while (prevSize != resolved.size());

        return resolved;
    }

    public List<ICommonFunctionNode> getMissingFunctionIn(IFunctionNode owner) {
        IASTFunctionDefinition fnAst = owner.getAST();
        FunctionCallParser visitor = new FunctionCallParser();
        fnAst.accept(visitor);

        List<ICommonFunctionNode> resolvedFunctions = new ArrayList<>();

        for (IASTFunctionCallExpression expression : visitor.getExpressions()) {

            MethodFinder finder = new MethodFinder(owner);
            finder.ignoreArgsType = true;

            INode referredNode = finder.find(expression);
            if (referredNode == null) {
                logger.debug(expression.getFunctionNameExpression().getRawSignature() + " is missing.");
                String funcName = finder.getFunctionSimpleName(expression);
                String returnType = new ReturnTypePrediction(owner).getReturnType(expression);
                IASTInitializerClause[] args = expression.getArguments();
                MissingFuntionGenerator generator;
                ICommonFunctionNode functionNode;
                if (returnType != null && funcName != null) {
                    generator = new MissingFuntionGenerator(returnType, funcName, args);
                    generator.setContext(owner);
                    functionNode = generator.exec();
                } else
                    functionNode = null;

                if (functionNode != null) {
                    // Generate function call dependency
//                    FunctionCallDependency d = new FunctionCallDependency(owner, functionNode);
//                    if (!owner.getDependencies().contains(d)
//                            && !functionNode.getDependencies().contains(d)) {
//                        owner.getDependencies().add(d);
//                        functionNode.getDependencies().add(d);
//                    }
                    resolvedFunctions.add(functionNode);
                    logger.debug("resolve function " + functionNode);
                }
            }
        }

        return resolvedFunctions;
    }

}
