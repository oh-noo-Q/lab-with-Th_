package entities.testdata;


import entities.SpecialCharacter;
import entities.utils.VariableTypeUtils;

/**
 * Represent union variable
 *
 * @author ducanhnguyen
 */
public class UnionDataNode extends StructureDataNode {

    @Override
    public String getInputForGoogleTest() throws Exception {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

        String input = "";

        String typeVar = this.getRawType().replace(IDataNode.REFERENCE_OPERATOR, "");
        typeVar = VariableTypeUtils.deleteStorageClassesExceptConst(typeVar);

        if (isExternel())
            typeVar = "";

        if(true){// (Environment.getInstance().isC()) {
            typeVar = typeVar.replace(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS, SpecialCharacter.EMPTY);

//            INode correspondingType = getCorrespondingType();
//            if (correspondingType instanceof StructureNode && !((StructureNode) correspondingType).haveTypedef()) {
//                if (!typeVar.startsWith("union"))
//                    typeVar = "union " + typeVar;
//            }
        }

        if (this.isPassingVariable()){
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;

        } else if (getParent() instanceof PointerDataNode) {
            input += getVituralName() + " = " + getRawType() + "()" + SpecialCharacter.END_OF_STATEMENT;

        } else if (getParent() instanceof OneDimensionDataNode){
            input += "";
        } else if (isSutExpectedArgument() || isGlobalExpectedValue())
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;
        else if (isVoidPointerValue()) {
            input += typeVar +" " + this.getVituralName() + SpecialCharacter.END_OF_STATEMENT;
        }
        return  input + SpecialCharacter.LINE_BREAK + super.getInputForGoogleTest();
    }

    @Override
    public UnionDataNode clone() {
        UnionDataNode clone = (UnionDataNode) super.clone();

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
