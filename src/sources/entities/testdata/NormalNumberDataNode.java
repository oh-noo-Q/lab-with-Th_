package entities.testdata;


import entities.SpecialCharacter;
import entities.UETLogger;
import entities.utils.VariableTypeUtils;

public class NormalNumberDataNode extends NormalDataNode {
    private final static UETLogger logger = UETLogger.get(NormalNumberDataNode.class);

    @Override
    public String getInputForGoogleTest() throws Exception {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

        String input =  super.getInputForGoogleTest() + SpecialCharacter.LINE_BREAK;

        // get type of variable
        String typeVar = VariableTypeUtils.deleteStorageClassesExceptConst(this.getRawType())
                .replace(IDataNode.REFERENCE_OPERATOR, "");
        typeVar = VariableTypeUtils.deleteStorageClassesExceptConst(typeVar);
        typeVar = VariableTypeUtils.deleteStructKeyword(typeVar);
        typeVar = VariableTypeUtils.deleteUnionKeyword(typeVar);

        if (this.getValue() != null) {
            if (isExternel())
                typeVar = "";

            // generate the statement
            if (isPassingVariable()) {
                input += typeVar + SpecialCharacter.SPACE + getVituralName() + SpecialCharacter.EQUAL + getValue() + SpecialCharacter.END_OF_STATEMENT;

            } else if (isAttribute()) {
                input += getVituralName() + SpecialCharacter.EQUAL + getValue() + SpecialCharacter.END_OF_STATEMENT;

            } else if (isArrayElement()) {
                input += getVituralName() + SpecialCharacter.EQUAL + getValue() + SpecialCharacter.END_OF_STATEMENT;

            } else if (isSTLListBaseElement()) {
                input += typeVar + SpecialCharacter.SPACE + getVituralName() + SpecialCharacter.EQUAL + getValue() + SpecialCharacter.END_OF_STATEMENT;
                
            } else if (isInConstructor()) {
                input += typeVar + SpecialCharacter.SPACE + getVituralName() + SpecialCharacter.EQUAL + getValue() + SpecialCharacter.END_OF_STATEMENT;

            } else {
                input += typeVar + " " + this.getVituralName() + "=" + getValue() + SpecialCharacter.END_OF_STATEMENT;
            }
        } else if (isPassingVariable()) {
            input += typeVar + " " + getVituralName() + SpecialCharacter.END_OF_STATEMENT;
        } else if (this.getValue() == null) {
            input += "/* "+getName()+" : null value -> no code */";
        }

        return input + SpecialCharacter.LINE_BREAK;
    }

//    @Override
//    public String getAssertionForGoogleTest(String method, String source, String target) throws Exception {
//        String assertion = "";//super.getAssertionForGoogleTest();
//
//        if (getValue() != null || getVituralName().equals(IGTestConstant.EXPECTED_OUTPUT)) {
//            String actualOutputName = getVituralName().replace(source, target);
//
//            if (Environment.getInstance().isC()) {
//                assertion += String.format("%s(%s, %s );", method, getVituralName(), actualOutputName);
//                assertion += getExportExeResultStm(actualOutputName, getVituralName());
//            } else {
//                String newMethod = method.substring(0, method.indexOf("_") + 1);
//                if ((VariableTypeUtils.isNumBasicFloat(getRawType()))) {
//                    if (getRawType().contains(VariableTypeUtils.BASIC.NUMBER.FLOAT.FLOAT))
//                        newMethod += "FLOAT_";
//                    else
//                        newMethod += "DOUBLE_";
//                }
//
//                newMethod += "EQ";
//
//                assertion += newMethod + "(" + getVituralName() + "," + actualOutputName + ")"
//                        + IGTestConstant.LOG_FUNCTION_CALLS;
//            }
//        }
//
//        return assertion;
//    }
}
