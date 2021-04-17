package entities.testdata;

import entities.SpecialCharacter;
import entities.common.DriverConstant;
import entities.parser.object.INode;
import entities.parser.object.InstanceVariableNode;
import entities.utils.VariableTypeUtils;

public class ConstructorDataNode extends SubprogramNode {
    public ConstructorDataNode(INode fn) {
        super(fn);
    }

    @Override
    public String getRawType() {
        if (super.getRawType() == null) {
            if (getParent() instanceof ValueDataNode) {
                String type = ((ValueDataNode) getParent()).getCorrespondingVar().getFullType();
                type = VariableTypeUtils.deleteStorageClassesExceptConst(type);
                type = VariableTypeUtils.deleteVirtualAndInlineKeyword(type);
                setRawType(type);

                String realType = ((ValueDataNode) getParent()).getCorrespondingVar().getRealType();
                realType = VariableTypeUtils.deleteStorageClassesExceptConst(realType);
                realType = VariableTypeUtils.deleteVirtualAndInlineKeyword(realType);
                setRealType(realType);
            }
        }

        return super.getRawType();
    }

    @Override
    public void setFunctionNode(INode functionNode) {
        this.functionNode = functionNode;
    }

    public ConstructorDataNode() {
        super();
    }

    @Override
    public String getDisplayNameInParameterTree() {
        return getName();
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
        //TODO: openSSL_locks_ not member of httplib::detail
        if (getVituralName().contains("openSSL_locks_"))
            return "";

        String input = super.getInputForGoogleTest();

        ValueDataNode subclassVar = (ValueDataNode) getParent();
        IDataNode tempClassVar = subclassVar.getParent();
        
        ValueDataNode classVar = null;
        if (tempClassVar instanceof ValueDataNode) {
            classVar = (ValueDataNode) tempClassVar;
        }

        if (!(classVar instanceof ClassDataNode))
            classVar = subclassVar;

        if (getTestCaseRoot().getFunctionNode().equals(getFunctionNode())) {
            if (classVar.getCorrespondingVar() instanceof InstanceVariableNode) {
                input += DriverConstant.MARK + "(\"<<PRE-CALLING>>\");";
            }
        }

        input += "";// ProjectClone.generateCallingMark(String.format("%s|%s", functionNode.getAbsolutePath(), getPathFromRoot()));

        String realType = VariableTypeUtils.getFullRawType(subclassVar.getCorrespondingVar());
        String originType = realType;

        if (classVar.isExternel())
            originType = "";

        String argumentInput = getConstructorArgumentsInputForGoogleTest();

        if (classVar.isInstance()) {
            input += getVituralName() + " = new " + realType
                    + argumentInput + SpecialCharacter.END_OF_STATEMENT;

        } else if (subclassVar.isArrayElement() || subclassVar.isAttribute()) {
            // can not use new
            input += getVituralName() + " = " + realType
                    + argumentInput + SpecialCharacter.END_OF_STATEMENT;

        } else if (!(classVar instanceof PointerDataNode)) {
            input += originType + " " + getVituralName() + " = " + realType
                    + argumentInput + SpecialCharacter.END_OF_STATEMENT;

        } else {
            // which case?
            input += realType + " " + getVituralName() + " = new " + getRawType()
                    + argumentInput + SpecialCharacter.END_OF_STATEMENT;
        }

        return input;
    }

    private String getConstructorArgumentsInputForGoogleTest() {
        StringBuilder input = new StringBuilder();
        input.append("(");

        if (getChildren().size() > 0) {
            for (IDataNode parameter : getChildren()) {
                input.append(parameter.getVituralName()).append(",");
            }
        }

        input.append(")");

        input = new StringBuilder(input.toString().replace(",)", ")"));

        return input.toString();
    }
}
