package entities.testdata;

import entities.SpecialCharacter;
import entities.utils.TemplateUtils;
import entities.utils.VariableTypeUtils;

public class OneDimensionStructureDataNode extends OneDimensionDataNode {

    @Override
    public String getInputForDisplay() throws Exception {
        String input = "";

        for (IDataNode child : this.getChildren())
            input += child.getInputForDisplay();
        if (this.isAttribute())
            input += this.getDotSetterInStr(this.getVituralName()) + SpecialCharacter.LINE_BREAK;
        return input;
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

        String declarationType = VariableTypeUtils
                .deleteStorageClassesExceptConst(this.getRawType().replace(IDataNode.REFERENCE_OPERATOR, ""));

        String coreType = VariableTypeUtils
                .deleteStorageClasses(declarationType).replaceAll("\\[.*\\]", "");

        if (TemplateUtils.isTemplate(declarationType))
            if (!getChildren().isEmpty())
                coreType = ((ValueDataNode) getChildren().get(0)).getRawType();

        if (isExternel())
            coreType = "";

        String input = "";

        if (this.isPassingVariable()){
            input += coreType + " " + getVituralName() + "[" + getSize() + "]" + SpecialCharacter.END_OF_STATEMENT;

        } else if (this.isAttribute()) {
            input += "";
        } else if (isSutExpectedArgument() || isGlobalExpectedValue()) {
            input += coreType + " " + getVituralName() + "[" + getSize() + "]" + SpecialCharacter.END_OF_STATEMENT;
        } else if (isVoidPointerValue()) {
            input += coreType + " " + getVituralName() + "[" + getSize() + "]" + SpecialCharacter.END_OF_STATEMENT;
        }

        return input + SpecialCharacter.LINE_BREAK + super.getInputForGoogleTest();
    }
}
