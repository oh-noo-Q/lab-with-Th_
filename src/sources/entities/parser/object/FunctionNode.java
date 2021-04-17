package entities.parser.object;


import entities.cfg.ICFG;

public class FunctionNode extends AbstractFunctionNode {
    @Override
    public ICFG generateCFGofExecutionSourcecode()
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPath(String typeOfCoverage)
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForStmCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForStmCOverage()
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForBranchCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForBranchCoverage()
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForMcdcCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForMcdcCOverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePath(String typeOfCoverage)
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForBasisPathCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForBasisPathCoverage()
    {
        return null;
    }

    @Override
    public String getTemplateFilePath()
    {
        return null;
    }

//    public static void main(String[] args) {
//        ProjectParser parser = new ProjectParser(new File(Paths.SAMPLE01));
//        FunctionNode functionNode = (FunctionNode) Search
//                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(),"StackLinkedList::push(Node*)").get(0);
//
//        String name = functionNode.getSimpleName();
////        IASTFunctionDefinition fnAst = functionNode.getAST();
//        IASTNode fnAst = functionNode.getAST();
//
//        ASTVisitor visitor = new ASTVisitor() {
//            @Override
//            public int visit(IASTExpression expression) {
//                if (expression instanceof IASTFunctionCallExpression) {
//                    String name = ((IASTFunctionCallExpression) expression).getFunctionNameExpression().toString();
//                    IASTInitializerClause[] arguments = ((IASTFunctionCallExpression) expression).getArguments();
//                    for (IASTInitializerClause argument : arguments) {
//                        if (argument instanceof IASTExpression) {
//                            String typeArg = ((IASTExpression) argument).getExpressionType().toString();
//                        }
//                    }
//                }
//                return PROCESS_CONTINUE;
//            }
//        };
//
//        visitor.shouldVisitExpressions = true;
//
//        ASTVisitor visitor1 = new ASTVisitor(true) {
//            @Override
//            public int visit(IASTName name) {
//                IBinding binding = name.resolveBinding();
//                if (binding instanceof IVariable) {
//                    IType type = ((IVariable) binding).getType();
//                }
//                return PROCESS_CONTINUE;
//            }
//        };
//
//        fnAst.accept(visitor);
////        System.out.println();
//    }
}
