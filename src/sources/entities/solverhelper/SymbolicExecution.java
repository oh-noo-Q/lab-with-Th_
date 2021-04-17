package entities.solverhelper;

import entities.UETLogger;
import entities.cfg.CFGGenerationforBranchvsStatementvsBasispathCoverage;
import entities.cfg.*;
import entities.testpath.IFullTestpath;
import entities.testpath.ITestpathInCFG;
import entities.testpath.PossibleTestpathGeneration;
import entities.bound.PointerOrArrayBound;
import entities.bound.PrimitiveBound;
import entities.testdatagen.RandomValue;
import entities.testdatagen.ValueToTestcaseConverter_UnknownSize;
import entities.parser.object.IFunctionConfig;
import entities.parser.object.INode;
import entities.solverhelper.memory.*;
import entities.solverhelper.normalstatementparser.*;
import entities.solverhelper.solver.RunZ3OnCMD;
import entities.solverhelper.solver.SmtLibGeneration;
import entities.solverhelper.solver.solutionparser.Z3SolutionParser;
import entities.testdatainit.VariableTypes;
import entities.utils.ASTUtils;
import entities.parser.object.FunctionConfig;
import entities.parser.ProjectParser;
import entities.parser.dependency.TypeDependency;
import entities.parser.object.*;
import entities.search.Search;
import entities.search.condition.FunctionNodeCondition;
import entities.search.condition.StructNodeCondition;
import entities.IRegex;
import entities.utils.Utils;
import entities.utils.VariableTypeUtils;
import com.ibm.icu.util.Calendar;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SymbolicExecution implements ISymbolicExecution {
    private final static UETLogger logger = UETLogger.get(SymbolicExecution.class);

    protected ICommonFunctionNode functionNode;

    /**
     * Represent a test path generated from control flow graph
     */
    protected ITestpathInCFG testpath = null;

    /**
     * The variable passing to the function
     */
    private Parameter parameters = null;

    /**
     * Table of variables
     */
    private VariableNodeTable tableMapping = new VariableNodeTable();

    /**
     * Store path constraints generated by performing symbolic execution the
     * given test path
     */
    protected PathConstraints constraints = new PathConstraints();

    /**
     * The return value of a test path, specified by "return ..."
     */
    private String returnValue = "";

    private List<NewVariableInSe> newVariables = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File("/Users/ducanhnguyen/Documents/akautauto/datatest/fsoft/c-algorithms/src"));

        parser.setFuncCallDependencyGeneration_enabled(true);
        parser.setTypeDependency_enable(true);
        parser.setParentReconstructor_enabled(true);
        parser.setExpandTreeuptoMethodLevel_enabled(true);
        parser.setCpptoHeaderDependencyGeneration_enabled(true);
        INode root = parser.getRootTree();
        IFunctionNode function = (IFunctionNode) Search
//				.searchNodes(root, new FunctionNodeCondition(), "trie_free(Trie*)").get(0);
//				.searchNodes(root, new FunctionNodeCondition(), "list_data(ListEntry*)").get(0);
                .searchNodes(root, new FunctionNodeCondition(), "list_free(ListEntry*)").get(0);

        //Environment.getInstance().setProjectNode((ProjectNode) root);
        INode TrieNode = Search.searchNodes(root, new StructNodeCondition(), "_ListEntry").get(0);
        IVariableNode firstArg = function.getArguments().get(0);
        firstArg.setCorrespondingNode(TrieNode);
        firstArg.getDependencies().add(new TypeDependency(firstArg, TrieNode));

        logger.debug(function.getAST().getRawSignature());

        FunctionConfig functionConfig = new FunctionConfig();
        functionConfig.getBoundOfArgumentsAndGlobalVariables().put("list", new PointerOrArrayBound(new String[]{"0:3"}, "ListEntry*"));
        functionConfig.setBoundOfPointer(new PrimitiveBound(0, 3));
        functionConfig.setSolvingStrategy("User-bound strategy");
        function.setFunctionConfig(functionConfig);

        // Choose a random test path to test
        PossibleTestpathGeneration tpGen = new PossibleTestpathGeneration(
                new CFGGenerationforBranchvsStatementvsBasispathCoverage(function).generateCFG(),
                2);
        tpGen.generateTestpaths();
        logger.debug("num tp = " + tpGen.getPossibleTestpaths().size());
        IFullTestpath randomTestpath = tpGen.getPossibleTestpaths().get(1);
        logger.debug(randomTestpath);

        // Get the passing variables of the given function
        Parameter paramaters = new Parameter();
        paramaters.addAll(function.getArguments());
        paramaters.addAll(function.getReducedExternalVariables());

        // Get the corresponding path constraints of the test path
        ISymbolicExecution se = new SymbolicExecution(randomTestpath, paramaters, function);
        logger.debug("constraints=\n" + se.getConstraints());
        logger.debug("normalized constraints=\n" + se.getNormalizedPathConstraints());
        logger.debug("new vars= " + se.getNewVariables());
//		logger.debug("table var=\n" + se.getTableMapping());

        for (NewVariableInSe newVar : se.getNewVariables()) {
            VariableNode var = new VariableNode();
            var.setRawType("int");
            var.setCoreType("int");
            var.setName(newVar.getOriginalName());
            paramaters.add(var);
        }
        for (INode var : paramaters)
            if (var instanceof IVariableNode)
                ((IVariableNode) var).setTypeDependencyState(false);
        SmtLibGeneration smt = new SmtLibGeneration(paramaters, se.getNormalizedPathConstraints(), function, se.getNewVariables());
        smt.generate();
        logger.debug("SMT-LIB file:\n" + smt.getSmtLibContent());
        String constraintFile = "/Users/ducanhnguyen/Desktop/tmp.smt2";
        Utils.writeContentToFile(smt.getSmtLibContent(), constraintFile);

        logger.debug("new variables: " + se.getNewVariables().toString());

        // solve
        logger.debug("Calling solver z3");
        String z3Path = "/Users/ducanhnguyen/Documents/akautauto/local/solver/z3-4.8.8-x64-osx-10.14.6/bin/z3";
        if (new File(z3Path).exists()) {
            RunZ3OnCMD z3Runner = new RunZ3OnCMD(
                    //AbstractSetting.getValue(ISettingv2.SOLVER_Z3_PATH),
                    z3Path,
                    constraintFile);
            z3Runner.execute();

            logger.debug("Original solution:\n" + z3Runner.getSolution());
            String staticSolution = new Z3SolutionParser().getSolution(z3Runner.getSolution());
            logger.debug("the next test data = " + staticSolution);

            logger.debug("Convert to standard format");
            ValueToTestcaseConverter_UnknownSize converter = new ValueToTestcaseConverter_UnknownSize(staticSolution);
            List<RandomValue> randomValues = converter.convert();
            logger.debug(randomValues);
        } else
            throw new Exception("Z3 path " + z3Path + " does not exist");
    }

    public SymbolicExecution(ITestpathInCFG testpath, Parameter parameters, ICommonFunctionNode functionNode)
            throws Exception {

        if (functionNode != null && parameters != null && parameters.size() > 0 && testpath != null) {
            Date startTime = Calendar.getInstance().getTime();

            this.testpath = testpath;
            this.parameters = parameters;
            this.functionNode = functionNode;

            tableMapping.setFunctionNode(this.functionNode);
            createMappingTable(parameters);

            SE(testpath.getAllCfgNodes(), this.tableMapping, this.functionNode);

            Date end = Calendar.getInstance().getTime();
            //AbstractAutomatedTestdataGeneration.symbolicExecutionTime += end.getTime() - startTime.getTime();
        }
    }

    private void SE(List<ICfgNode> cfgNodes, VariableNodeTable table, ICommonFunctionNode function) {
        int scopeLevel = 1;
        int count = 0;

        // STEP 2.
        // We perform the symbolic execution on all of the statements in the
        // normalized
        // test path until catch an supported statement.
        for (ICfgNode cfgNode : cfgNodes)
            // If the test path is always false, we stop the symbolic execution.
            if (!this.constraints.isAlwaysFalse())
                if (cfgNode instanceof BeginFlagCfgNode || cfgNode instanceof EndFlagCfgNode) {
                    // nothing to do
                } else
                    try {
                        logger.debug("Handle \"" + cfgNode.getContent() + "\"");
                        //AbstractAutomatedTestdataGeneration.numOfSymbolicStatements++;
                        boolean isContinue = true;

                        if (cfgNode instanceof NormalCfgNode) {
                            IASTNode ast = ((NormalCfgNode) cfgNode).getAst();

                            switch (this.getTypeOfStatement(ast)) {
                                case NAMESPACE:
                                    logger.debug("is NAMESPACE");
                                    new UsingNamespaceParser().parse(ast, table);
                                    break;

                                case UNARY_ASSIGNMENT: {
                                    logger.debug("is UNARY_ASSIGNMENT");
                                    addNewVariablesAndConstraintsFromFieldReference(ast, this.newVariables, constraints);
                                    new UnaryBinaryParser().parse(ast, table);
                                    break;
                                }
                                case BINARY_ASSIGNMENT: {
                                    logger.debug("is BINARY_ASSIGNMENT");
                                    addNewVariablesAndConstraintsFromFieldReference(ast, this.newVariables, constraints);
                                    new BinaryAssignmentParser().parse(ast, table);
                                    break;
                                }

                                case DECLARATION: {
                                    logger.debug("is DECLARATION");
                                    addNewVariablesAndConstraintsFromFieldReference(ast, this.newVariables, constraints);
                                    isContinue = this.parseDeclaration(ast, scopeLevel, table, function);
                                    break;
                                }

                                case CONDITION: {
                                    logger.debug("is CONDITION");
                                    addNewVariablesAndConstraintsFromFieldReference(ast, this.newVariables, constraints);
                                    isContinue = this.parseCondition(cfgNode, ast, count, table);
                                    break;
                                }

                                case RETURN: {
                                    break;
                                }

                                case THROW: {
                                    logger.debug("is THROW");
                                    ThrowParser throwParser = new ThrowParser();
                                    throwParser.parse(ast, table);
                                    this.returnValue = throwParser.getExceptionName();
                                    break;
                                }

                                case IGNORE:
                                    logger.debug("is IGNORE");
                                    break;

                                case UNSPECIFIED_STATEMENT:
                                    logger.debug("is UNSPECIFIED_STATEMENT");
                                    break;
                            }
                        } else if (cfgNode instanceof ScopeCfgNode) {
                            logger.debug("is ScopeCfgNode");
                            scopeLevel = this.parseScope(cfgNode, scopeLevel, table);

                        } else if (cfgNode instanceof FlagCfgNode) {
                            // nothing to do
                        } else
                            break;

                        Utils.containFunction = false;
                        count++;

                        if (!isContinue)
                            break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }

        this.constraints.setVariablesTableNode(this.tableMapping);
    }

    private void addNewVariablesAndConstraintsFromFieldReference(IASTNode node, List<NewVariableInSe> newVariables,
                                                                 PathConstraints constraints) {
        List<IASTFieldReference> tokens = ASTUtils.getFieldReferences(node);
        for (IASTFieldReference fieldReference : tokens) {
            /**
             * Ex: field ref = "trie[0].root_node"
             */

            // new var = "trie"
            String newVar1 = fieldReference.getFieldOwner().getRawSignature();
            try {
                newVar1 = ExpressionRewriterUtils.rewrite(tableMapping, newVar1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            newVariables.add(new NewVariableInSe(newVar1, newVar1));

            // new var = "trie[0].root_node"
            String newVar2 = newVar1 + "[0]." + fieldReference.getFieldName().getRawSignature();
            String newVar2_normalized = VariableNodeTable.normalizeNameOfVariable(newVar2);
            newVariables.add(new NewVariableInSe(newVar2, newVar2_normalized));

            // new var = "trie[0]"
            String newVar3 = newVar1 + "[0]";
            String newVar3_normalized = VariableNodeTable.normalizeNameOfVariable(newVar3);
            newVariables.add(new NewVariableInSe(newVar3, newVar3_normalized));

            // new constraint
            // pc: "trie!=1"
            constraints.add(new PathConstraint(newVar1 + "!=NULL", null, PathConstraint.CREATE_FROM_DECISION));
            // pc: "trie[0]!=1"
            constraints.add(new PathConstraint(newVar3 + "!=NULL", null, PathConstraint.CREATE_FROM_DECISION));
        }
    }

    /**
     * In order to trace the changes of variables, we create a table of
     * variables that stores its values.
     * <p>
     * For example, we have a table: (x, int, 2). The value of x is equivalent
     * to 2. After an increasing statement (i.g., x++), the table becomes (x,
     * int, 3)
     *
     * @param parameters
     * @throws Exception
     */
    private void createMappingTable(Parameter parameters) throws Exception {
        for (INode parameter : parameters)
            if (parameter instanceof VariableNode) {

                // All passing variables have global access
                VariableNode par = (VariableNode) parameter;

                INode nodeType = par.resolveCoreType();

                String name = par.getName();
                if (name == null || name.isEmpty())
                    continue;

                String defaultValue = ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + name;

                String realType = par.getRealType();
                if (realType == null || realType.length() == 0)
                    continue;

                IFunctionConfig functionConfig = functionNode.getFunctionConfig();

                SymbolicVariable v = createVariable(name, realType, nodeType, defaultValue, functionConfig);

                if (v != null) {
                    v.setFunction(functionNode);
                    this.tableMapping.add(v);
                    v.setNode(nodeType);
                }
            }
    }

    public static SymbolicVariable createVariable(String name, String realType, INode nodeType,
                                                  String defaultValue, IFunctionConfig functionConfig) {
        SymbolicVariable v = null;

        if (VariableTypes.isAuto(realType))
            logger.debug("Does not support type of the passing variable is auto");
        else {
            /*
             * ----------------NUMBER----------------------
             */
            if (VariableTypes.isNumBasic(realType)) {
                v = new NumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE, defaultValue);

            } else if (VariableTypes.isNumOneDimension(realType)) {
                v = new OneDimensionNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                ((OneDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

            } else if (VariableTypes.isNumTwoDimension(realType)) {
                v = new TwoDimensionNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                ((TwoDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

            } else if (VariableTypes.isNumOneLevel(realType)) {
                v = new OneLevelNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                ((OneLevelSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

            } else if (VariableTypes.isNumTwoLevel(realType)) {
                v = new TwoLevelNumberSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                ((TwoLevelNumberSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

            } else {
                /*
                 * ----------------CHARACTER----------------------
                 */
                if (VariableTypes.isChBasic(realType))
                    v = new CharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE, defaultValue);

                else if (VariableTypes.isChOneDimension(realType)) {
                    v = new OneDimensionCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                    ((OneDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

                } else if (VariableTypes.isChTwoDimension(realType)) {
                    v = new TwoDimensionCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                    ((TwoDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

                } else if (VariableTypes.isChOneLevel(realType)) {
                    v = new OneLevelCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                    ((OneLevelCharacterSymbolicVariable) v).getReference().getBlock().setName(defaultValue);
                    ((OneLevelCharacterSymbolicVariable) v)
                            .setSize(functionConfig.getBoundOfArray().getLower() + "");

                } else if (VariableTypes.isChTwoLevel(realType)) {
                    v = new TwoLevelCharacterSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                    ((TwoLevelCharacterSymbolicVariable) v).getReference().getBlock().setName(defaultValue);

                } else {
                    /*
                     * ----------------STRUCTURE----------------------
                     */
                    if (VariableTypeUtils.isStructureSimple(realType)) {

                        if (nodeType instanceof UnionNode)
                            v = new UnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof StructNode)
                            v = new StructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof ClassNode)
                            v = new ClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof EnumNode)
                            v = new EnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else
                            logger.debug("Do not support symbolic execution for " + realType);

                    } else if (VariableTypeUtils.isStructureOneDimension(realType)) {
                        if (nodeType instanceof UnionNode)
                            v = new OneDimensionUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof StructNode)
                            v = new OneDimensionStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof ClassNode)
                            v = new OneDimensionClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof EnumNode)
                            v = new OneDimensionEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else
                            logger.debug("Do not support symbolic execution for " + realType);

                        if (v != null)
                            ((OneDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

                    } else if (VariableTypeUtils.isStructureTwoDimension(realType)) {
                        if (nodeType instanceof UnionNode)
                            v = new TwoDimensionUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof StructNode)
                            v = new TwoDimensionStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof ClassNode)
                            v = new TwoDimensionClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof EnumNode)
                            v = new TwoDimensionEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else
                            logger.debug("Do not support symbolic execution for " + realType);

                        if (v != null)
                            ((TwoDimensionSymbolicVariable) v).getBlock().setName(defaultValue);

                    } else if (VariableTypeUtils.isStructureOneLevel(realType)) {
                        if (nodeType instanceof UnionNode)
                            v = new OneLevelUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof StructNode)
                            v = new OneLevelStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof ClassNode)
                            v = new OneLevelClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof EnumNode)
                            v = new OneLevelEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else
                            logger.debug("Do not support symbolic execution for " + realType);

                        if (v != null) {
                            ((OneLevelSymbolicVariable) v).getReference().getBlock().setName(defaultValue);
                            ((OneLevelSymbolicVariable) v)
                                    .setSize(functionConfig.getBoundOfArray().getLower() + "");
                        }

                    } else if (VariableTypeUtils.isStructureTwoLevel(realType)) {

                        if (nodeType instanceof UnionNode)
                            v = new TwoLevelUnionSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof StructNode)
                            v = new TwoLevelStructSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof ClassNode)
                            v = new TwoLevelClassSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else if (nodeType instanceof EnumNode)
                            v = new TwoLevelEnumSymbolicVariable(name, realType, ISymbolicVariable.GLOBAL_SCOPE);
                        else
                            logger.debug("Do not support symbolic execution for " + realType);

                        if (v != null) {
                            ((TwoLevelSymbolicVariable) v).getReference().getBlock().setName(defaultValue);
//							((TwoLevelSymbolicVariable) v)
//									.setSize(functionConfig.getBoundOfArray().getLower() + "");
                        }

                    } else
                        logger.debug(String.format("The variable %s with type %s is not supported now", name, realType));
                }
            }
        }

        return v;
    }

    /**
     * Get the type of the statement.
     * <p>
     * In this function, we define some rules to detect the type of the
     * statements exactly.
     *
     * @param stm
     * @return
     */
    private int getTypeOfStatement(IASTNode stm) {
        stm = Utils.shortenAstNode(stm);

        if (stm.getRawSignature().startsWith(ISymbolicExecution.NAMESPACE_SIGNAL))
            return ISymbolicExecution.NAMESPACE;

        else if (stm instanceof ICPPASTBinaryExpression && ASTUtils.isCondition((ICPPASTBinaryExpression) stm)
                || stm.getRawSignature().startsWith("!") /* Ex: !(a>0) */
                || stm.getRawSignature().matches(IRegex.NAME_REGEX)/* Ex: a */)
            return ISymbolicExecution.CONDITION;

        else if (stm instanceof ICPPASTUnaryExpression)
            return ISymbolicExecution.UNARY_ASSIGNMENT;

        else if (stm instanceof IASTExpressionStatement) {
            /*
             * In case the statement is expression (e.g., binary expression,
             * unary expression)
             */
            IASTExpression _stm = ((IASTExpressionStatement) stm).getExpression();

            if (_stm instanceof IASTBinaryExpression)
                if (_stm.getRawSignature().contains("="))
                    /*
                     * If the statement is binary expression and it contains
                     * "=", it means that the statement is binary assignment.
                     *
                     * For example: x=2*x
                     */
                    return ISymbolicExecution.BINARY_ASSIGNMENT;
                else
                    /*
                     * If the statement is binary expression and it does not
                     * contains "=", in this case it is hard to detect the type
                     * of the statement. We ignore this circumstance!
                     *
                     */
                    return ISymbolicExecution.IGNORE;

            else if (_stm instanceof IASTFunctionCallExpression)
                /*
                 * Ignore the statement corresponding to function call.
                 */
                return ISymbolicExecution.UNSPECIFIED_STATEMENT;

            else if (_stm instanceof IASTUnaryExpression) {
                IASTUnaryExpression unaryStm = (IASTUnaryExpression) _stm;

                switch (unaryStm.getOperator()) {
                    case IASTUnaryExpression.op_prefixIncr:
                    case IASTUnaryExpression.op_postFixIncr:
                    case IASTUnaryExpression.op_prefixDecr:
                    case IASTUnaryExpression.op_postFixDecr:
                    case IASTUnaryExpression.op_star:
                    case IASTUnaryExpression.op_amper:
                        /*
                         * Ex1: a++; Ex2: a--; Ex3: --a; Ex4:++a; Ex5: *a; Ex6: &a
                         */
                        return ISymbolicExecution.UNARY_ASSIGNMENT;

                    case IASTUnaryExpression.op_throw:
                        /*
                         * Ex: throw 1
                         */
                        return ISymbolicExecution.THROW;

                    default:
                        return ISymbolicExecution.UNSPECIFIED_STATEMENT;
                }
            } else
                /*
                 * Does not handle the remaining circumstances.
                 */
                return ISymbolicExecution.UNSPECIFIED_STATEMENT;

        } else if (stm instanceof IASTDeclarationStatement)
            return ISymbolicExecution.DECLARATION;

        else if (stm instanceof IASTReturnStatement)
            return ISymbolicExecution.RETURN;

        else if (stm instanceof ICPPASTBinaryExpression && ASTUtils.isBinaryAssignment((IASTBinaryExpression) stm))
            return ISymbolicExecution.BINARY_ASSIGNMENT;

        else if (containFunctionCall(stm))
            return ISymbolicExecution.UNSPECIFIED_STATEMENT;

        return ISymbolicExecution.UNSPECIFIED_STATEMENT;
    }

    private boolean containFunctionCall(IASTNode ast) {

        ASTVisitor visitor = new ASTVisitor() {

            @Override
            public int visit(IASTExpression expression) {
                if (expression instanceof IASTFunctionCallExpression) {
                    Utils.containFunction = true;
                    return ASTVisitor.PROCESS_ABORT;
                } else
                    return ASTVisitor.PROCESS_CONTINUE;
            }
        };
        visitor.shouldVisitStatements = true;
        visitor.shouldVisitExpressions = true;
        ast.accept(visitor);
        return Utils.containFunction;
    }

    /**
     * Parse the scope statement
     *
     * @param stm
     * @param scopeLevel
     *            the new scope level
     * @param table
     */
    private int parseScope(ICfgNode stm, int scopeLevel, IVariableNodeTable table) {
        if (stm.getContent().equals("{"))
            scopeLevel++;
        else {
            table.deleteScopeLevelAt(scopeLevel);
            scopeLevel--;
        }
        return scopeLevel;
    }

    /**
     * Parse the declaration
     *
     * @param ast
     * @param scopeLevel
     * @param table
     * @return true if the declaration of variable is supported
     * @throws Exception
     */
    private boolean parseDeclaration(IASTNode ast, int scopeLevel, VariableNodeTable table,
                                     INode function) throws Exception {
        if (ast instanceof IASTDeclarationStatement && function instanceof IFunctionNode) {
            IASTDeclarationStatement declarationStm = (IASTDeclarationStatement) ast;

            IASTDeclaration declaration = declarationStm.getDeclaration();
            if (declaration instanceof IASTSimpleDeclaration) {
                IASTSimpleDeclaration stm3 = (IASTSimpleDeclaration) declaration;

                DeclarationParser declarationParser = new DeclarationParser();
                declarationParser.setScopeLevel(scopeLevel);
                declarationParser.setFunction((IFunctionNode) function);
                declarationParser.parse(stm3, table);

                return true;
            }
        }
        return false;
    }

    /**
     * Parse the condition
     *
     * @param stm
     * @param ast
     * @param count
     * @param table
     * @return true if the condition is supported. Otherwise, it returns false
     * @throws Exception
     */
    private boolean parseCondition(ICfgNode stm, IASTNode ast, int count, VariableNodeTable table) throws Exception {
        ast = Utils.shortenAstNode(ast);

        if (ASTUtils.isSingleCodition(ast)) {
            if (ast instanceof ICPPASTLiteralExpression || ast instanceof IASTIdExpression) {
                /*
                 * Ex: (a)
                 */
                String var = ast.getRawSignature();
                PhysicalCell r = table.findPhysicalCellByName(var);

                if (r != null) {

                    List<ISymbolicVariable> vars = table.findVariablesContainingCell(r);
                    if (vars.size() == 1) {
                        ISymbolicVariable firstVar = vars.get(0);

                        if (firstVar instanceof BasicSymbolicVariable)
                            this.constraints
                                    .add(new PathConstraint(((BasicSymbolicVariable) firstVar).getSymbolicValue() + "==1",
                                            stm, PathConstraint.CREATE_FROM_DECISION));
                    } else
                        throw new Exception("Dont support!");
                }
            } else {
                String str = stm.getContent();

                ConditionParser conditionParser = new ConditionParser();
                conditionParser.parse(Utils.convertToIAST(str), table);
                this.constraints
                        .add(new PathConstraint(conditionParser.getNewConstraint(), stm, PathConstraint.CREATE_FROM_DECISION));
            }
        } else if (ASTUtils.isMultipleCodition(ast)) {
            logger.debug("is multiple condition");
            String str = stm.getContent();

            ConditionParser conditionParser = new ConditionParser();
            conditionParser.parse(Utils.convertToIAST(str), table);
            this.constraints
                    .add(new PathConstraint(conditionParser.getNewConstraint(), stm, PathConstraint.CREATE_FROM_DECISION));

        } else
            throw new Exception("Dont support " + stm.toString());

        return true;

    }

    /**
     * Get the normalized path constraints to be solved by smt-solver
     *
     * @return
     */
    @Override
    public PathConstraints getNormalizedPathConstraints() {
        PathConstraints newPcs = new PathConstraints();
        for (PathConstraint pc : this.constraints) {
            String constraintStr = pc.getConstraint();
            constraintStr = VariableNodeTable.normalizeNameOfVariable(constraintStr);

            // "NULL" --> "0" because SMT-Solver does not understand NULL value
            constraintStr = constraintStr.replaceAll("\\s*!=\\s*\\bNULL\\b", ">0");

            newPcs.add(new PathConstraint(constraintStr, pc.getCfgNode(), pc.getType()));
        }
        return newPcs;
    }

    @Override
    public IPathConstraints getConstraints() {
        return this.constraints;
    }

    @Override
    public void setConstraints(PathConstraints constraints) {
        this.constraints = constraints;
    }

    @Override
    public Parameter getParameters() {
        return this.parameters;
    }

    @Override
    public void setParameters(Parameter parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getReturnValue() {
        return this.returnValue;
    }

    @Override
    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public IVariableNodeTable getTableMapping() {
        return this.tableMapping;
    }

    @Override
    public void setTableMapping(VariableNodeTable tableMapping) {
        this.tableMapping = tableMapping;
    }

    @Override
    public ITestpathInCFG getTestpath() {
        return this.testpath;
    }

    @Override
    public void setTestpath(ITestpathInCFG testpath) {
        this.testpath = testpath;
    }

    public List<NewVariableInSe> getNewVariables() {
        return newVariables;
    }

    public void setNewVariables(List<NewVariableInSe> newVariables) {
        this.newVariables = newVariables;
    }
}