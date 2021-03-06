package entities.parser.object;

import entities.bound.IFunctionConfigBound;
import entities.bound.PointerOrArrayBound;
import entities.bound.PrimitiveBound;
//import entities.environment.Environment;
//import entities.parser.object.ICommonFunctionNode;
//import entities.parser.object.IVariableNode;
//import entities.testdata.gen.module.type.PointerTypeInitiation;
import entities.bound.UndefinedBound;
import entities.utils.Utils;
import entities.utils.VariableTypeUtils;
import entities.utils.VariableTypeUtilsForStd;
import com.google.gson.annotations.Expose;

import java.util.*;

/**
 * Reresent configuration of a function
 *
 * @author ducanh
 */
public class FunctionConfig implements IFunctionConfig {

    private ICommonFunctionNode functionNode;

    private Map<String, IFunctionConfigBound> boundOfArguments = new HashMap<>();

    @Expose
    protected long theMaximumNumberOfIterations; // >=1, equal to the number of test data

    @Expose
    private String solvingStrategy = SUPPORT_SOLVING_STRATEGIES.USER_BOUND_STRATEGY; // by default

    @Expose
    private PrimitiveBound boundOfPointer;

    @Expose
    private PrimitiveBound boundOfArray;

    @Expose
    private PrimitiveBound boundOfOtherCharacterVars;

    @Expose
    private PrimitiveBound boundOfOtherNumberVars;

    @Expose
    private String testdataGenStrategy;

    @Expose
    private String testdataExecStrategy;

    public FunctionConfig() {
        theMaximumNumberOfIterations = 10;
        boundOfArray = new PrimitiveBound(1, 3);
        boundOfPointer = new PrimitiveBound(0, 1);
        boundOfOtherCharacterVars = new PrimitiveBound(IFunctionConfigBound.MIN_VARIABLE_TYPE, IFunctionConfigBound.MAX_VARIABLE_TYPE);
        boundOfOtherNumberVars = new PrimitiveBound(IFunctionConfigBound.MIN_VARIABLE_TYPE, IFunctionConfigBound.MAX_VARIABLE_TYPE);
        testdataGenStrategy = TEST_DATA_GENERATION_STRATEGIES.CONCOLIC_TESTING_DIJKSTRA; // by default
        testdataExecStrategy = TEST_DATA_EXECUTION_STRATEGIES.MULTIPLE_COMPILATION;
    }

    @Override
    public String getSolvingStrategy() {
        return solvingStrategy;
    }

    @Override
    public void setSolvingStrategy(String solvingStrategy) {
        this.solvingStrategy = solvingStrategy;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        FunctionConfig clone = (FunctionConfig) super.clone();
//        clone.setCharacterBound((ParameterBound) characterBound.clone());
//        clone.setIntegerBound((ParameterBound) integerBound.clone());
        return clone();
    }

    public void setTestdataGenStrategy(String testdataGenStrategy) {
        this.testdataGenStrategy = testdataGenStrategy;
    }

    public String getTestdataGenStrategy() {
        return testdataGenStrategy;
    }

    public String getTestdataExecStrategy() {
        return testdataExecStrategy;
    }

    public void setTestdataExecStrategy(String testdataExecStrategy) {
        this.testdataExecStrategy = testdataExecStrategy;
    }

    public ICommonFunctionNode getFunctionNode() {
        return functionNode;
    }

    public void setFunctionNode(ICommonFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public long getTheMaximumNumberOfIterations() {
        return theMaximumNumberOfIterations;
    }

    public void setTheMaximumNumberOfIterations(long theMaximumNumberOfIterations) {
        this.theMaximumNumberOfIterations = theMaximumNumberOfIterations;
    }

    public PrimitiveBound getBoundOfOtherNumberVars() {
        return boundOfOtherNumberVars;
    }

    public void setBoundOfOtherNumberVars(PrimitiveBound boundOfOtherNumberVars) {
        this.boundOfOtherNumberVars = boundOfOtherNumberVars;
    }

    public void setBoundOfOtherCharacterVars(PrimitiveBound boundOfOtherCharacterVars) {
        this.boundOfOtherCharacterVars = boundOfOtherCharacterVars;
    }

    public PrimitiveBound getBoundOfOtherCharacterVars() {
        return boundOfOtherCharacterVars;
    }

    public Map<String, IFunctionConfigBound> getBoundOfArgumentsAndGlobalVariables() {
        return boundOfArguments;
    }

    public void setBoundOfArguments(Map<String, IFunctionConfigBound> boundOfArguments) {
        this.boundOfArguments = boundOfArguments;
    }

    private PointerOrArrayBound updateArrayElement(IFunctionConfig functionConfig, String type) {
        // primitive array
        List<String> indexes = Utils.getIndexOfArray(type);
        if (indexes.size() > 0) {
            List<String> normalizedIndexes = new ArrayList<>();
            for (String index : indexes)
                if (index.length() == 0)
                    normalizedIndexes.add(functionConfig.getBoundOfArray().getLower()
                            + IFunctionConfigBound.RANGE_DELIMITER + functionConfig.getBoundOfArray().getUpper());
                else normalizedIndexes.add(index);
            return new PointerOrArrayBound(normalizedIndexes, type);
        }
        return null;
    }

    private PointerOrArrayBound updatePointerElement(IFunctionConfig functionConfig, String type) {
        // primitive pointer
        int level = 0;// PointerTypeInitiation.getLevel(type);
        if (level > 0) {
            List<String> normalizedIndexes = new ArrayList<>();
            for (int i = 0; i < level; i++)
                normalizedIndexes.add(functionConfig.getBoundOfPointer().getLower() + IFunctionConfigBound.RANGE_DELIMITER
                        + functionConfig.getBoundOfPointer().getUpper());
            return new PointerOrArrayBound(normalizedIndexes, type);
        }
        return null;
    }

    @Override
    public void createDefaultConfig(ICommonFunctionNode functionNode) {
        FunctionConfig functionConfig = this == null ? new FunctionConfig() : this;

        functionConfig.setTheMaximumNumberOfIterations(1);
        functionConfig.setBoundOfArray(new PrimitiveBound(1, 3));
        functionConfig.setBoundOfPointer(new PrimitiveBound(IFunctionConfigBound.NULL_POINTER, 3));
        functionConfig.setBoundOfOtherNumberVars(new PrimitiveBound(IFunctionConfigBound.MIN_VARIABLE_TYPE, IFunctionConfigBound.MAX_VARIABLE_TYPE));
        functionConfig.setBoundOfOtherCharacterVars(new PrimitiveBound(IFunctionConfigBound.MIN_VARIABLE_TYPE, IFunctionConfigBound.MAX_VARIABLE_TYPE));
        functionConfig.setTestdataGenStrategy(IFunctionConfig.TEST_DATA_GENERATION_STRATEGIES.RANDOM);
        functionConfig.setTestdataExecStrategy(IFunctionConfig.TEST_DATA_EXECUTION_STRATEGIES.MULTIPLE_COMPILATION);

        Map<String, IFunctionConfigBound> bounds = createBoundOfArgument(functionConfig, functionNode);
        functionConfig.setBoundOfArguments(bounds);
        functionConfig.setFunctionNode(functionNode);
    }

    public Map<String, IFunctionConfigBound> createBoundOfArgument(IFunctionConfig functionConfig, ICommonFunctionNode functionNode) {
        // create bound
        Map<String, IFunctionConfigBound> bounds = new HashMap<>();

        boolean detectedBound = false;
        for (IVariableNode variableNode : functionNode.getArguments()) {
            String realRawType = variableNode.getRealType();

            detectedBound = false;
            String type = VariableTypeUtils.removeRedundantKeyword(realRawType);
            type = VariableTypeUtils.deleteReferenceOperator(type);
            type = VariableTypeUtils.deleteSizeFromArray(type);
            type = type.trim();

            Set<String> keys = null;//
            //Environment.getBoundOfDataTypes().getBounds().keySet();
            if (keys != null)
                for (String definedBound : keys) {
                    PrimitiveBound b = new PrimitiveBound();// Environment.getBoundOfDataTypes().getBounds().get(definedBound);

                    if (definedBound.equals(type)) {
                        // just a primitive variable
                        bounds.put(variableNode.getName(), new PrimitiveBound(b.getLower(), b.getUpper()));
                        detectedBound = true;
                        break;
                    }
                }
            if (detectedBound)
                continue;

            // bound of pointer
            if (VariableTypeUtils.isChMultiLevel(realRawType)
                    || VariableTypeUtils.isNumMultiLevel(realRawType)
                    || VariableTypeUtils.isStrMultiLevel(realRawType)
                    || VariableTypeUtils.isBoolMultiLevel(realRawType)
                    || VariableTypeUtils.isStructureMultiLevel(realRawType)
                    // std
                    || VariableTypeUtilsForStd.isStdMapMultiLevel(realRawType)
                    || VariableTypeUtilsForStd.isStdPairMultiLevel(realRawType)
                    || VariableTypeUtilsForStd.isStdListMultiLevel(realRawType)
                    || VariableTypeUtilsForStd.isStdQueueMultiLevel(realRawType)
                    || VariableTypeUtilsForStd.isStdStackMultiLevel(realRawType)
                    || VariableTypeUtilsForStd.isStdVectorMultiLevel(realRawType)
                    || VariableTypeUtilsForStd.isStdSetMultiLevel(realRawType)) {
                PointerOrArrayBound pointerBound = updatePointerElement(functionConfig, realRawType);
                if (pointerBound != null) {
                    bounds.put(variableNode.getName(), pointerBound);
                    detectedBound = true;
                }
            }
            if (detectedBound)
                continue;
            // bound of array
            if (VariableTypeUtils.isBoolMultiDimension(realRawType) ||
                    VariableTypeUtils.isChMultiDimension(realRawType) ||
                    VariableTypeUtils.isNumMultiDimension(realRawType) ||
                    VariableTypeUtils.isStrMultiDimension(realRawType) ||
                    VariableTypeUtils.isStructureMultiDimension(realRawType)
                    // std
                    || VariableTypeUtilsForStd.isStdMapMultiDimension(realRawType)
                    || VariableTypeUtilsForStd.isStdPairMultiDimension(realRawType)
                    || VariableTypeUtilsForStd.isStdListMultiDimension(realRawType)
                    || VariableTypeUtilsForStd.isStdQueueMultiDimension(realRawType)
                    || VariableTypeUtilsForStd.isStdStackMultiDimension(realRawType)
                    || VariableTypeUtilsForStd.isStdVectorMultiDimension(realRawType)
                    || VariableTypeUtilsForStd.isStdSetMultiDimension(realRawType)) {
                PointerOrArrayBound arrayBound = updateArrayElement(functionConfig, realRawType);
                if (arrayBound != null) {
                    bounds.put(variableNode.getName(), arrayBound);
                    detectedBound = true;
                }
            }

            if (detectedBound)
                continue;
            if (VariableTypeUtils.isVoidPointer(realRawType)){
//                VoidPointerBound voidPointerBound = new VoidPointerBound();
//                bounds.put(variableNode.getName(), new UndefinedBound());
//                //
//                int a = 0;
            }

            if (!detectedBound) {
                bounds.put(variableNode.getName(), new UndefinedBound());
            }
        }

        functionConfig.setBoundOfArguments(bounds);
        return bounds;
    }

    public PrimitiveBound getBoundOfArray() {
        return boundOfArray;
    }

    public void setBoundOfArray(PrimitiveBound boundOfArray) {
        this.boundOfArray = boundOfArray;
    }

    public PrimitiveBound getBoundOfPointer() {
        return boundOfPointer;
    }

    public void setBoundOfPointer(PrimitiveBound boundOfPointer) {
        this.boundOfPointer = boundOfPointer;
    }
}