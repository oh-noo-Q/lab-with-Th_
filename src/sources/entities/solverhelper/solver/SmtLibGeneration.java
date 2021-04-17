package entities.solverhelper.solver;

import entities.SpecialCharacter;
import entities.UETLogger;
import entities.bound.IFunctionConfigBound;
import entities.bound.PointerOrArrayBound;
import entities.bound.PrimitiveBound;
import entities.parser.ProjectParser;
import entities.parser.object.*;
import entities.search.Search;
import entities.search.condition.FunctionNodeCondition;
import entities.solverhelper.ISymbolicExecution;
import entities.solverhelper.NewVariableInSe;
import entities.solverhelper.PathConstraint;
import entities.solverhelper.memory.ISymbolicVariable;
import entities.solverhelper.memory.VariableNodeTable;
import entities.utils.Utils;
import entities.utils.VariableTypeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate SMT-Lib file
 *
 * @author anhanh
 */
public class SmtLibGeneration implements ISmtLibGeneration {
    final static UETLogger logger = UETLogger.get(SmtLibGeneration.class);

    // List of test cases
    private List<IVariableNode> testcases;
    // List of path constraints
    private List<PathConstraint> constraints;
    // SMT-Lib content
    private String smtLib = "";

    private List<NewVariableInSe> newVariableInSes = new ArrayList<>();
    private ICommonFunctionNode functionNode;

    public SmtLibGeneration(Parameter params, List<PathConstraint> constraints, ICommonFunctionNode functionNode,
                            List<NewVariableInSe> newVariableInSes) {
        this.newVariableInSes = newVariableInSes;
        this.testcases = new ArrayList<>();
        for (INode param : params)
            if (param instanceof IVariableNode)
                this.testcases.add((IVariableNode) param);
        this.constraints = constraints;
        this.functionNode = functionNode;
    }

    public SmtLibGeneration(List<IVariableNode> testcases, List<PathConstraint> constraints, ICommonFunctionNode functionNode,
                            List<NewVariableInSe> newVariableInSes) {
        this.testcases = testcases;
        this.constraints = constraints;
        this.functionNode = functionNode;
        this.newVariableInSes = newVariableInSes;
    }

    public static void main(String[] args) throws Exception {
        ProjectParser parser = new ProjectParser(new File("/Users/ducanhnguyen/Documents/akautauto/datatest/duc-anh/Algorithm"));
        parser.setCpptoHeaderDependencyGeneration_enabled(true);
        parser.setExtendedDependencyGeneration_enabled(true);
        parser.setExpandTreeuptoMethodLevel_enabled(true);

//		String nameFn = "concatenate(char[],char[])";
        String nameFn = "uninit_var(int[3],int[3])";
//		String nameFn = "Tritype(int,int,int)";
//		String nameFn = "compareGroup(Person*,Person*,int,int)";
        IFunctionNode function = (IFunctionNode) Search
                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), nameFn).get(0);
        logger.debug(function.getAST().getRawSignature());

        FunctionConfig functionConfig = new FunctionConfig();
        functionConfig.setBoundOfOtherCharacterVars(new PrimitiveBound(0, 10));
        functionConfig.setBoundOfOtherNumberVars(new PrimitiveBound(0, 20));
//		functionConfig.getBoundOfArguments().put("i", new PrimitiveBound(0, 4));
//		functionConfig.getBoundOfArguments().put("j", new PrimitiveBound(0, 4));
//		functionConfig.getBoundOfArguments().put("k", new PrimitiveBound(0, 4));

        PointerOrArrayBound b = new PointerOrArrayBound();
        b.getIndexes().add("1:3");
        b.getIndexes().add("1:3");
        functionConfig.getBoundOfArgumentsAndGlobalVariables().put("a", b);
        functionConfig.getBoundOfArgumentsAndGlobalVariables().put("b", b);

        functionConfig.setBoundOfPointer(new PrimitiveBound(0, 3));
        functionConfig.setFunctionNode(function);
        function.setFunctionConfig(functionConfig);

        // Get the passing variables of the given function
        Parameter paramaters = new Parameter();
        paramaters.addAll(function.getArguments());
        paramaters.addAll(function.getExternalVariables());
        for (INode param : paramaters)
            if (param instanceof IVariableNode)
                logger.debug("Param " + param.getName() + ", type = " + ((IVariableNode) param).getRawType());

        //
        List<PathConstraint> constraints = new ArrayList<>();
        SmtLibGeneration smt = new SmtLibGeneration(paramaters, constraints, function, new ArrayList<>());
        smt.generate();
        logger.debug(smt.getSmtLibContent());
    }

    @Override
    public void generate() throws Exception {
        smtLib = ISmtLibGeneration.OPTION_TIMEOUT + SpecialCharacter.LINE_BREAK +
//				ISmtLibGeneration.NULL_VALUE + SpecialCharacter.LINE_BREAK +
                getDeclarationFun(-1, testcases, "", functionNode) + SpecialCharacter.LINE_BREAK + SpecialCharacter.LINE_BREAK;

        // Generate body of the smt-lib file
//		if (constraints.size() == 0)
//			smtLib = EMPTY_SMT_LIB_FILE;
//		else {
        for (PathConstraint constraint : constraints)
            switch (constraint.getConstraint()) {
                case ISymbolicExecution.NO_SOLUTION_CONSTRAINT:
                    smtLib = EMPTY_SMT_LIB_FILE;
                    return;
                case ISymbolicExecution.ALWAYS_TRUE_CONSTRAINT:
                    // nothing to do
                    break;
                default:
                    SmtLibv2Normalizer2 normalizer = new SmtLibv2Normalizer2(constraint.getConstraint(), newVariableInSes);
                    normalizer.normalize();

                    if (normalizer.getNormalizedSourcecode() != null
                            && normalizer.getNormalizedSourcecode().length() > 0) {
                        smtLib += "(assert" + normalizer.getNormalizedSourcecode() + ")" + SpecialCharacter.LINE_BREAK;
                    } else {
                        // If we can not normalize the constraint, we ignore it
                        // :)
                    }
                    break;
            }

        smtLib += ISmtLibGeneration.SOLVE_COMMAND;
//		}
    }

    private String getArgDecla(int n) {
        if (n == 0)
            return "Int";

        String argDecla = "";
        for (int i = 0; i < n; i++)
            argDecla += "Int ";
        return argDecla.substring(0, argDecla.length() - 1);
    }

    private static final int MAX_LOOP = 5;
    private int loopCount = 0;
    private INode prevStructureNode;

    private String getDeclarationFunForPointer(int depth, String name) throws Exception {
        String fullName = name;
        String tmp1 =  generateTab(depth) + String.format("(declare-fun %s () Int)", fullName);
        String tmp2 = generateTab(depth) + "; for pointer: value = 0 (means NULL),  value = 1 (not NULL)";
        String tmp3 = generateTab(depth) + String.format("(assert (and (>= %s %s) (<= %s %s)))\n", fullName, "0", fullName, "1");
        return tmp1 + "\n" + tmp2 + "\n" + tmp3 + "\n";
    }

    /**
     * Generate "(declare-fun...)"
     *
     * @return
     * @throws Exception
     */
    private String getDeclarationFun(int depth, List<IVariableNode> variables, String prefix, ICommonFunctionNode functionNode) throws Exception {
        depth++;
        logger.debug("getDeclarationFun");
        StringBuilder output = new StringBuilder();
        if (variables.size() > 0) {
            for (IVariableNode var : variables) {
				logger.debug("Analyze " + var.getName());
//				output.append("\n" + generateTab(depth) + "; -----------------------\n");
                output.append(/*generateTab(depth) + */"; Variable \"" + var.getName() + "\" , real type = \"" + var.getRealType() + "\", " + var.getClass() + "\n");

                String type = VariableTypeUtils.removeRedundantKeyword(var.getRealType());
                type = VariableTypeUtils.deleteReferenceOperator(type);

                String originalName = var.getName();
                String modifiedName = prefix + ISymbolicVariable.PREFIX_SYMBOLIC_VALUE + var.getName();

                modifiedName = VariableNodeTable.normalizeNameOfVariable(modifiedName);

                // PRIMITIVE TYPES
                if (VariableTypeUtils.isNumBasicFloat(type)) {
                    output.append(generateTab(depth) + String.format("(declare-fun %s () Real)\n", modifiedName));
                    output.append(generateTab(depth) + addBoundInStr(originalName, modifiedName, type, functionNode));
                    loopCount = 0;
                    prevStructureNode = null;

                } else if (VariableTypeUtils.isBoolBasic(type)
                        || VariableTypeUtils.isChBasic(type)
                        || VariableTypeUtils.isNumBasicInteger(type)) {
                    output.append(generateTab(depth) + String.format("(declare-fun %s () Int)\n", modifiedName));
                    output.append(generateTab(depth) + addBoundInStr(originalName, modifiedName, type, functionNode));
                    loopCount = 0;
                    prevStructureNode = null;

                }
                // primitive pointer
                else if (VariableTypeUtils.isNumIntegerMultiLevel(type)
                        || VariableTypeUtils.isBoolMultiLevel(type)
                        || VariableTypeUtils.isChMultiLevel(type)) {
                    int level = 0;// PointerTypeInitiation.getLevel(type);
                    output.append(String.format("(declare-fun %s (%s) Int)\n", modifiedName, getArgDecla(level)));
                    output.append(addBoundInStr(originalName, modifiedName, type, functionNode));
                    loopCount = 0;
                    prevStructureNode = null;

                } else if (VariableTypeUtils.isNumFloatMultiDimension(type)) {
                    int size = Utils.getIndexOfArray(type).size();
                    output.append(generateTab(depth) + String.format("(declare-fun %s (%s) Real)\n", modifiedName, getArgDecla(size)));
                    output.append(generateTab(depth) + addBoundInStr(originalName, modifiedName, type, functionNode));
                    loopCount = 0;
                    prevStructureNode = null;

                }
                // primitive array
                else if (VariableTypeUtils.isNumIntergerMultiDimension(type)
                        || VariableTypeUtils.isBoolMultiDimension(type)
                        || VariableTypeUtils.isChMultiDimension(type)) {
                    int size = Utils.getIndexOfArray(type).size();
                    output.append(generateTab(depth) + String.format("(declare-fun %s (%s) Int)\n", modifiedName, getArgDecla(size)));
                    output.append(generateTab(depth) + addBoundInStr(originalName, modifiedName, type, functionNode));
                    loopCount = 0;
                    prevStructureNode = null;

                } else if (VariableTypeUtils.isNumFloatMultiLevel(type)) {
                    int level = 0;// PointerTypeInitiation.getLevel(type);
                    output.append(generateTab(depth) + String.format("(declare-fun %s (%s) Real)\n", modifiedName, getArgDecla(level)));
                    output.append(generateTab(depth) + addBoundInStr(originalName, modifiedName, type, functionNode));
                    loopCount = 0;
                    prevStructureNode = null;

                }
                //
                else if (VariableTypeUtils.isStructureMultiLevel(type)) {
                    output.append(generateTab(depth) + String.format("(declare-fun %s () Int)\n", modifiedName));
                    output.append(generateTab(depth) + String.format("(assert (>= %s %s))", modifiedName, "0"));
//					INode correspondingType = var.resolveCoreType();
//					if (correspondingType == prevStructureNode)
//						loopCount++;
//					else {
//						loopCount = 0;
//						prevStructureNode = null;
//					}
//					prevStructureNode = correspondingType;
//					if (loopCount < MAX_LOOP) {
//						if (correspondingType instanceof StructNode) {
//							List<IVariableNode> allAttributes = ((StructNode) correspondingType).getAttributes();
//
//							PrimitiveBound bound = functionNode.getFunctionConfig().getBoundOfPointer();
//							if (bound != null)
//								for (long i = bound.getLowerAsLong(); i < bound.getUpperAsLong(); i++) {
//									output.append(String.format("; Element %s of %s (START)\n", i, modifiedName));
//									//TODO: infinity loop
//									output.append(getDeclarationFun(allAttributes,
//											modifiedName + ISymbolicVariable.ARRAY_OPENING + i + ISymbolicVariable.ARRAY_CLOSING,
//											functionNode));
//									output.append(String.format("; Element %s of %s (END)\n\n", i, modifiedName));
//								}
//
//						} else
//							output.append(String.format("; do not support this type\n"));
//					} else
//						logger.debug("break infinity loop");
                } else
                    output.append(generateTab(depth) + String.format("; do not support this type\n"));
            }
        }
        depth--;
        return output.toString();
    }

    private String generateTab(int depth){
        String tab = "";
        for (int i = 0; i < depth; i++)
            tab += "\t";
        return tab;
    }

    private String addBoundInStr(String originalName, String modifiedName, String type, ICommonFunctionNode functionNode) {
        String output = "";
        IFunctionConfigBound b = functionNode.getFunctionConfig().getBoundOfArgumentsAndGlobalVariables().get(originalName);
        if (b != null) {
            if (b instanceof PrimitiveBound) {
                output = String.format("(assert (and (>= %s %s) (<= %s %s)))\n",
                        modifiedName, ((PrimitiveBound) b).getLower(), modifiedName, ((PrimitiveBound) b).getUpper());

            } else if (b instanceof PointerOrArrayBound) {
                // bound of element type
                String elementType = "";
                if (type.endsWith("*")) {
                    // is pointer
                    elementType = type.substring(0, type.indexOf("*")).trim();
                } else if (type.endsWith("]")) {
                    // is array
                    elementType = type.substring(0, type.indexOf("[")).trim();
                }

                IFunctionConfigBound bound = null;
                if (VariableTypeUtils.isBoolBasic(elementType)
                        || VariableTypeUtils.isNumBasicInteger(elementType))
                    bound = functionNode.getFunctionConfig().getBoundOfOtherNumberVars();
                else if (VariableTypeUtils.isChBasic(elementType)) {
                    bound = functionNode.getFunctionConfig().getBoundOfOtherCharacterVars();
                }

                //
                if (bound != null && bound instanceof PrimitiveBound) {
                    int size = ((PointerOrArrayBound) b).getIndexes().size();

                    String lower = ((PrimitiveBound) bound).getLower();
                    String upper = ((PrimitiveBound) bound).getUpper();

                    // "MIN", "MAX" ---> specific values
                    IFunctionConfigBound envBound = null;// Environment.getBoundOfDataTypes().getBounds().get(elementType);
                    if (envBound instanceof PrimitiveBound) {
                        lower = lower.replace(IFunctionConfigBound.MIN_VARIABLE_TYPE, ((PrimitiveBound) envBound).getLower())
                                .replace(IFunctionConfigBound.MAX_VARIABLE_TYPE, ((PrimitiveBound) envBound).getUpper());
                        upper = upper.replace(IFunctionConfigBound.MIN_VARIABLE_TYPE, ((PrimitiveBound) envBound).getLower())
                                .replace(IFunctionConfigBound.MAX_VARIABLE_TYPE, ((PrimitiveBound) envBound).getUpper());
                    }

                    //
                    output = addBoundOfElement(modifiedName, elementType, ((PointerOrArrayBound) b).getIndexes(), lower, upper);
                }
            }
        } else {

        }
        return output;
    }

    private String addBoundOfElement(String name, String type, List<String> size, String lower, String upper) {
        String output = "";

        if (size.size() == 1) {
            long dimen1 = Long.parseLong(size.get(0).contains(IFunctionConfigBound.RANGE_DELIMITER) ?
                    size.get(0).split(IFunctionConfigBound.RANGE_DELIMITER)[1] : size.get(0));
            dimen1 = dimen1 > MAX_DIMENSION_1 ? MAX_DIMENSION_1 : dimen1;

            for (long idx1 = 0; idx1 < dimen1; idx1++) {
                String nameEle = String.format("(%s %s)", name, idx1);
                String andIndex = String.format("(assert(and (>= %s %s) (<= %s %s)))\n",
                        nameEle, lower, nameEle, upper);
                output += andIndex;
            }

        } else if (size.size() == 2) {
            long dimen1 = Long.parseLong(size.get(0));
            long dimen2 = Long.parseLong(size.get(1));

            dimen1 = dimen1 > MAX_DIMENSION_1 ? MAX_DIMENSION_1 : dimen1;
            dimen2 = dimen1 > MAX_DIMENSION_2 ? MAX_DIMENSION_2 : dimen2;

            for (long idx1 = 0; idx1 < dimen1; idx1++)
                for (long idx2 = 0; idx2 < dimen2; idx2++) {
                    String nameEle = String.format("(%s %s %s)", name, idx1, idx2);
                    String andIndex = String.format("(assert(and (>= %s %s) (<= %s %s)))\n",
                            nameEle, lower, nameEle, upper);
                    output += andIndex;
                }

        } else if (size.size() == 3) {
            long dimen1 = Long.parseLong(size.get(0));
            long dimen2 = Long.parseLong(size.get(1));
            long dimen3 = Long.parseLong(size.get(1));

            dimen1 = dimen1 > MAX_DIMENSION_1 ? MAX_DIMENSION_1 : dimen1;
            dimen2 = dimen1 > MAX_DIMENSION_2 ? MAX_DIMENSION_2 : dimen2;
            dimen3 = dimen3 > MAX_DIMENSION_3 ? MAX_DIMENSION_3 : dimen3;

            for (long idx1 = 0; idx1 < dimen1; idx1++)
                for (long idx2 = 0; idx2 < dimen2; idx2++)
                    for (long idx3 = 0; idx3 < dimen3; idx3++) {
                        String nameEle = String.format("(%s %s %s %s)", name, idx1, idx2, idx3);
                        String andIndex = String.format("(assert(and (>= %s %s) (<= %s %s)))\n",
                                nameEle, lower, nameEle, upper);
                        output += andIndex;
                    }
        }
        return output;
    }

    @Override
    public String getSmtLibContent() {
        return smtLib;
    }

    public ICommonFunctionNode getFunctionNode() {
        return functionNode;
    }

    public void setFunctionNode(ICommonFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public void setNewVariableInSes(List<NewVariableInSe> newVariableInSes) {
        this.newVariableInSes = newVariableInSes;
    }

    public List<NewVariableInSe> getNewVariableInSes() {
        return newVariableInSes;
    }

    public static final int MAX_DIMENSION_1 = 100; // to avoid too many element of array/pointer in smt-lib
    public static final int MAX_DIMENSION_2 = 10;
    public static final int MAX_DIMENSION_3 = 10;
}
