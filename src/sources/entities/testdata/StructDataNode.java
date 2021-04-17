package entities.testdata;

import entities.SpecialCharacter;
import entities.parser.object.INode;
import entities.utils.VariableTypeUtils;

/**
 * Represent struct variable
 *
 * @author DucAnh
 */
public class StructDataNode extends StructureDataNode {

    @Override
    public String getInputForGoogleTest() throws Exception {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

//        if (Environment.getInstance().isC()) {
            return getCInputGTest();
//        } else
//            return getCppInputGTest();
    }

    private String getCInputGTest() throws Exception {
        String input = "";

        String typeVar = this.getRawType().replace(IDataNode.REFERENCE_OPERATOR, "");
        typeVar = VariableTypeUtils.deleteStorageClassesExceptConst(typeVar);

        INode correspondingType = getCorrespondingType();

        typeVar = typeVar.replace(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS, SpecialCharacter.EMPTY);

//        if (correspondingType instanceof StructureNode && !((StructureNode) correspondingType).haveTypedef()) {
//            if (!typeVar.startsWith("struct"))
//                typeVar = "struct " + typeVar;
//        }

        if (isExternel())
            typeVar = "";

        if (this.isPassingVariable()){
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

        } else if (getParent() instanceof OneDimensionDataNode || getParent() instanceof PointerDataNode){
            input += "";

        } else if (isSutExpectedArgument() || isGlobalExpectedValue()) {
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

        } else if (isInstance()) {
            input += "";

        } else if (isVoidPointerValue()) {
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;
        }

        return  input + SpecialCharacter.LINE_BREAK + super.getInputForGoogleTest();
    }

    private String getCppInputGTest() throws Exception {
        String input = "";

        String typeVar = this.getRawType().replace(IDataNode.REFERENCE_OPERATOR, "");
        typeVar = VariableTypeUtils.deleteStorageClassesExceptConst(typeVar);

        if (isExternel())
            typeVar = "";

        if (this.isPassingVariable()){
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

        } else if (getParent() instanceof OneDimensionDataNode || getParent() instanceof PointerDataNode){
            input += "";

        } else if (isSutExpectedArgument() || isGlobalExpectedValue()) {
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

        } else if (isInstance()) {
            input += "";

        }
//        else if (isPassingVariable())  {
//            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;
//        }

        return  input + SpecialCharacter.LINE_BREAK + super.getInputForGoogleTest();
    }

    @Override
    public StructDataNode clone() {
        StructDataNode clone = (StructDataNode) super.clone();

        for (IDataNode child : getChildren()) {
            if (child instanceof ValueDataNode) {
                ValueDataNode cloneChild = ((ValueDataNode) child).clone();
                clone.getChildren().add(cloneChild);
                cloneChild.setParent(clone);
            }
        }

        return clone;
    }
}
