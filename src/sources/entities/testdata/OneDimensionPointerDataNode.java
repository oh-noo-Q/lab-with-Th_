package entities.testdata;

import entities.SpecialCharacter;
import entities.utils.TemplateUtils;
import entities.utils.VariableTypeUtils;

public class OneDimensionPointerDataNode extends OneDimensionDataNode {

    @Override
    public String getInputForGoogleTest() throws Exception {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

        String declaration = "";

        // get type
        String type = VariableTypeUtils
                .deleteStorageClassesExceptConst(this.getRawType().replace(IDataNode.REFERENCE_OPERATOR, ""));

        String coreType = type.replaceAll("\\[.*\\]", "");

        if (TemplateUtils.isTemplate(type))
            if (!getChildren().isEmpty())
                coreType = ((ValueDataNode) getChildren().get(0)).getRawType();

        if (isExternel()) {
            coreType = "";
        }

        // get indexes
//        List<String> indexes = Utils.getIndexOfArray(TemplateUtils.deleteTemplateParameters(type));
//        if (indexes.size() > 0) {
        String dimension = "[" + getSize() + "]";

        // generate declaration
        if (this.isAttribute()) {
            declaration += "";
        } else if (this.isPassingVariable()) {
            declaration += String.format("%s %s%s" + SpecialCharacter.END_OF_STATEMENT, coreType,
                    this.getVituralName(), dimension);
        } else if (isVoidPointerValue()) {
            declaration += String.format("%s %s%s" + SpecialCharacter.END_OF_STATEMENT, coreType,
                    this.getVituralName(), dimension);
        }
//        }

        return declaration + SpecialCharacter.LINE_BREAK + super.getInputForGoogleTest();
    }
}
