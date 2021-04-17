package entities.testdata;

import entities.SpecialCharacter;
import entities.testdata.comparable.AssertMethod;
import entities.testdata.comparable.INullableComparable;
import entities.testdata.comparable.NullableStatementGenerator;
import entities.utils.VariableTypeUtils;

/**
 * for "void*"
 */
public class VoidPointerDataNode extends UnresolvedDataNode implements INullableComparable
{

//    private AbstractUserCode userCode = null;
    private String referenceType = null;
    private InputMethod inputMethod;

    @Override
    public boolean haveValue() {
        if (inputMethod == InputMethod.USER_CODE) {
            return true;// userCode != null;
        }

        return super.haveValue();
    }

    @Override
    public String getAssertionForGoogleTest(String method, String source, String target) throws Exception {
        StringBuilder assertStm = new StringBuilder();

        String actualOutputName = getVituralName().replace(source, target);

        for (IDataNode child : getChildren()) {
            if (child instanceof ValueDataNode) {
                ValueDataNode childVal = (ValueDataNode) child;
                String childRawType =childVal.getRawType();
                String actualOutputChildName = childVal.getVituralName().replace(source, target);
                String castStm = String.format("%s %s = (%s) %s;", childRawType, actualOutputChildName, childRawType, actualOutputName);
                assertStm.append(castStm);
                String assertChildStm = childVal.getAssertionForGoogleTest(method, source, target);
                assertStm.append(assertChildStm);
            }
        }

        return assertStm.toString();
    }

    private String getInputFromUserCode() {
//        if (userCode == null) return "";

//        String userCodeContent = userCode.getContent();
//        if (userCodeContent == null || userCodeContent.trim().length() == 0)
//            return "";
//
//        if (userCodeContent.trim().endsWith(DEFAULT_USER_CODE)) {
//            return "/*No code*/";
//        } else {
//            if (userCodeContent.contains("=") && userCodeContent.indexOf("=") == userCodeContent.lastIndexOf("=")) {
//
//                if (Environment.getInstance().getCompiler().isGPlusPlusCommand()){
//                    // Case 1: parameter "void* data = 2222;"---> "auto xxx = 222; void* data = &xxx";
//                    // Case 2: parameter "s.data = 2222;" (s.data is void*)---> "auto xxx = 222; s.data = &xxx";
//                    String initialization = userCodeContent.substring(userCodeContent.indexOf("=") + 1).trim();
//
//                    String newVar = "voidPointerTmp" + new RandomDataGenerator().nextInt(0, 999999);
//                    String stm = "auto " + newVar + "=" + initialization;
//                    if (!stm.endsWith(";"))
//                        stm += ";";
//
//                    String normalize = "";
//                    if (this.isPassingVariable())
//                        normalize = String.format("%s \n %s %s = &%s;", stm, getRawType(), getVituralName(), newVar);
//                    else
//                        normalize = String.format("%s \n %s = &%s;", stm, getVituralName(), newVar);
//                    return normalize;
//
//                } else if (Environment.getInstance().getCompiler().isGccCommand()){
//                    // Case 1: parameter "void* data = 2222;"---> "void* xxx = 222; void* data = xxx";
//                    // Case 2: parameter "s.data = 2222;" (s.data is void*)---> "void* xxx = 222; s.data = xxx";
//
//                    String initialization = userCodeContent.substring(userCodeContent.indexOf("=") + 1).trim();
//
//                    String newVar = "voidPointerTmp" + new RandomDataGenerator().nextInt(0, 999999);
//                    String stm = "void* " + newVar + "=" + initialization;
//                    if (!stm.endsWith(";"))
//                        stm += ";";
//
//                    String normalize = "";
//                    if (this.isPassingVariable()) // case 1
//                        normalize = String.format("%s \n %s %s = %s;", stm, getRawType(), getVituralName(), newVar);
//                    else // case 2
//                        normalize = String.format("%s \n %s = %s;", stm, getVituralName(), newVar);
//                    return normalize;
//                }
//            }
//            return "/* Do not know how to create initialization of void pointer */";
//        }
        return "";
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

        String input = SpecialCharacter.EMPTY;

        // get type of variable
        String typeVar = VariableTypeUtils.deleteStorageClassesExceptConst(this.getRawType())
                .replace(IDataNode.REFERENCE_OPERATOR, "");
        typeVar = VariableTypeUtils.deleteStorageClassesExceptConst(typeVar);

        if (isExternel())
            typeVar = "";

        if (!getChildren().isEmpty()) {
            ValueDataNode refDataNode = (ValueDataNode) getChildren().get(0);

            if (this.isPassingVariable()) {
                input = typeVar + " " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

            } else if (this.isAttribute() || this.isArrayElement()) {

            } else if (isSTLListBaseElement()) {
                input = typeVar + " " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

            } else if (this.isInConstructor()){
                input = typeVar + " " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

            } else {
                input = typeVar + " " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;
            }

            String valueInit = refDataNode.getInputForGoogleTest();
//            String valueRawTypePrefix = refDataNode.getRawType() + " ";
//            if (!valueInit.startsWith(valueRawTypePrefix))
//                valueInit = valueRawTypePrefix + valueInit;
            input += valueInit;

            input += String.format("%s = %s;", getVituralName(), refDataNode.getVituralName());

        } else if (isPassingVariable()) {
            input = typeVar + " " + getVituralName() + SpecialCharacter.END_OF_STATEMENT;
        }

        return input;
    }

//    @Override
//    public void setUserCode(AbstractUserCode userCode) {
//        this.userCode = userCode;
//    }

//    @Override
//    public AbstractUserCode getUserCode() {
//        if (userCode == null) {
//            userCode = new UsedParameterUserCode();
//            ((UsedParameterUserCode) userCode).setType(UsedParameterUserCode.TYPE_CODE);
//            userCode.setContent(generateInitialUserCode() + DEFAULT_USER_CODE);
//        }
//
//        return userCode;
//    }

//    @Override
//    public String getContextPath() {
//        UnitNode unitNode = getUnit();
//        String filePath;
//
//        if (unitNode != null) {
//            filePath = unitNode.getSourceNode().getAbsolutePath();
//        } else {
//            String[] pathItems = getCorrespondingVar().getAbsolutePath().split(File.separator);
//            filePath = pathItems[1];
//        }
//
//        return filePath;
//    }

    /**
     * [0] included path
     * [1] temporary path
     */
//    @Override
//    public String getTemporaryPath() {
//        UnitNode unitNode = getUnit();
//        String filePath;
//        String temporaryPath;
//
//        if (unitNode != null) {
//            filePath = unitNode.getSourceNode().getAbsolutePath();
//            temporaryPath = ProjectClone2.getClonedFilePath(filePath);
//        } else {
//            String[] pathItems = getCorrespondingVar().getAbsolutePath().split(File.separator);
//            filePath = pathItems[1];
//            temporaryPath = SystemLibrary.getLibrariesDirectory() + filePath + SystemLibrary.LIBRARY_EXTENSION;
//        }
//
//        int lastSeparator = temporaryPath.lastIndexOf(File.separator) + 1;
//        temporaryPath = temporaryPath.substring(0, lastSeparator) + "temporary.cpp";
//
//        return temporaryPath;
//    }

//    @Override
//    public String generateInitialUserCode() {
//        String input = "";
//
//        String typeVar = getRawType();
//
//        if (isExternel())
//            typeVar = "";
//
//        // generate the statement
//        if (this.isPassingVariable()) {
//            input += typeVar + " " + getVituralName() + " = ";
//
//        } else if (this.isAttribute()) {
//            input += getVituralName() + " = ";
//
//        } else if (this.isArrayElement()) {
//            input += getVituralName() + " = ";
//
//        } else if (isSTLListBaseElement()) {
//            input += typeVar + " " + getVituralName() + " = ";
//
//        } else if (this.isInConstructor()) {
//            input += typeVar + " " + getVituralName() + " = ";
//
//        }
//
//        return input;
//    }

//    @Override
//    public String assertEqual(String source, String target) {
//        return new ValueStatementGenerator(this).assertEqual(source, target);
//    }
//
//    @Override
//    public String assertNotEqual(String source, String target) {
//        return new ValueStatementGenerator(this).assertNotEqual(source, target);
//    }


    @Override
    public String getAssertion() {
        String actualName = getActualName();

        String output = SpecialCharacter.EMPTY;

        String assertMethod = getAssertMethod();
        if (assertMethod != null) {
            switch (assertMethod) {
                case AssertMethod.ASSERT_NULL:
                    output = assertNull(actualName);
                    break;

                case AssertMethod.ASSERT_NOT_NULL:
                    output = assertNotNull(actualName);
                    break;

//                case AssertMethod.USER_CODE:
//                    output = getAssertUserCode().normalize();
//                    break;
            }
        }

        return output + super.getAssertion();
    }

    @Override
    public String assertNull(String name) {
        return new NullableStatementGenerator(this).assertNull(name);
    }

    @Override
    public String assertNotNull(String name) {
        return new NullableStatementGenerator(this).assertNotNull(name);
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String realType) {
        this.referenceType = realType;
    }

    public InputMethod getInputMethod() {
        return inputMethod;
    }

    public void setInputMethod(InputMethod inputMethod) {
        this.inputMethod = inputMethod;
    }

    public enum InputMethod {
        AVAILABLE_TYPES, USER_CODE
    };
}
