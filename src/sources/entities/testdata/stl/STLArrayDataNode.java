package entities.testdata.stl;

import entities.SpecialCharacter;
import entities.common.IGTestConstant;
import entities.testdata.IDataNode;
import entities.testdata.ValueDataNode;
import entities.utils.VariableTypeUtils;

public class STLArrayDataNode extends ListBaseDataNode {
    @Override
    public String getElementName(int index) {
        return String.format("%s[%d]", getName(), index);
    }

    @Override
    public String getPushMethod() {
        return "";
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
        String declaration = "";

        String type = VariableTypeUtils
                .deleteStorageClassesExceptConst(this.getRawType().replace(IDataNode.REFERENCE_OPERATOR, ""));

        if (!type.startsWith(SpecialCharacter.STD_NAMESPACE))
            type = SpecialCharacter.STD_NAMESPACE + type;

//        String coreType = type.replaceAll("\\[.*\\]", "");
        if (isExternel())
            type = "";

//        String originVirtualName = getVituralName();
//        String cloneVirtualName = getCloneVirtualName();

        if (!isAttribute() && !isArrayElement()) {
            declaration += type + SpecialCharacter.SPACE + getVituralName()
                    + SpecialCharacter.END_OF_STATEMENT + SpecialCharacter.LINE_BREAK;
        }

        for (IDataNode child : getChildren()) {
//            declaration += "//" + child.getClass().getSimpleName() + SpecialCharacter.SPACE
//                    + child.getName() + SpecialCharacter.LINE_BREAK;
            declaration += child.getInputForGoogleTest() + SpecialCharacter.LINE_BREAK;
        }

        return declaration;
    }

    @Override
    public String getAssertionForGoogleTest(String method, String source, String target) throws Exception {
        String output = "";

//        if (Environment.getInstance().isC())
//            return "";

        if (isSetSize()) {
            String actualOutputName = getVituralName().replace(source, target);

            output += String.format("%s(%s.size(), %s.size())%s\n", method, getVituralName(),
                    actualOutputName, IGTestConstant.LOG_FUNCTION_CALLS);

            for (IDataNode child : getChildren()) {
                if (child instanceof ValueDataNode) {
                    ValueDataNode dataNode = (ValueDataNode) child;

                    String actualOutputChildName = dataNode.getVituralName().replace(source, target);

                    String coreType = VariableTypeUtils
                            .deleteStorageClasses(dataNode.getRawType().replace(IDataNode.REFERENCE_OPERATOR, ""));

                    output += String.format("%s %s = %s[%d];\n",
                            coreType, actualOutputChildName, actualOutputName, getChildren().indexOf(child));

                    output += dataNode.getAssertionForGoogleTest(method, source, target) + SpecialCharacter.LINE_BREAK;
                }
            }
        }

        return output;
    }
}
