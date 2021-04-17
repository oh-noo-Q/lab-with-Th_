package entities.testdata;

import entities.parser.object.FunctionPointerTypeNode;
import entities.parser.object.ICommonFunctionNode;
import entities.parser.object.INode;
import entities.search.Search;
import entities.testdata.comparable.*;
import entities.SpecialCharacter;

import java.util.List;

public class FunctionPointerDataNode extends OtherUnresolvedDataNode implements INullableComparable {

    private List<INode> possibleFunctions;

    private ICommonFunctionNode selectedFunction;

    public List<INode> getPossibleFunctions() {
        INode typeNode = getCorrespondingType();
        if (possibleFunctions == null && typeNode instanceof FunctionPointerTypeNode) {
            possibleFunctions = Search.searchAllMatchFunctions((FunctionPointerTypeNode) typeNode);
        }

        return possibleFunctions;
    }

    @Override
    public boolean haveValue() {
        return selectedFunction != null;
    }

    public void setSelectedFunction(ICommonFunctionNode selectedFunction) {
        this.selectedFunction = selectedFunction;
    }

    public INode getSelectedFunction() {
        return selectedFunction;
    }

    @Override
    public String getDisplayNameInParameterTree() {
        if (name.isEmpty()) {
            INode typeNode = getCorrespondingType();

            if (typeNode instanceof FunctionPointerTypeNode) {
                return ((FunctionPointerTypeNode) typeNode).getFunctionName();
            }
        }

        return super.getDisplayNameInParameterTree();
    }

    @Override
    public String getInputForGoogleTest() {
        if (isUseUserCode()) {
            //return getUserCodeContent();
        }

        String input = SpecialCharacter.EMPTY;

        String typeVar = getRawType();

        if (isExternel())
            typeVar = "";

        if (selectedFunction != null) {

            String valueVar = String.format("&%s;", selectedFunction.getSimpleName());

            if (this.isPassingVariable()) {
                input += typeVar + " " + this.getVituralName() + "=" + valueVar + SpecialCharacter.END_OF_STATEMENT;

            } else if (this.isAttribute()) {
                input += this.getVituralName() + "=" + valueVar + SpecialCharacter.END_OF_STATEMENT;

            } else if (this.isArrayElement()) {
                input += this.getVituralName() + "=" + valueVar + SpecialCharacter.END_OF_STATEMENT;

            } else if (isSTLListBaseElement()) {
                input += typeVar + " " + this.getVituralName() + "=" + valueVar + SpecialCharacter.END_OF_STATEMENT;

            } else if (this.isInConstructor()){
                input += typeVar + " " + this.getVituralName() + "=" + valueVar + SpecialCharacter.END_OF_STATEMENT;

            } else {
                input += typeVar + " " + this.getVituralName() + "=" + valueVar + SpecialCharacter.END_OF_STATEMENT;
            }
        } else if (isPassingVariable()) {
            input += typeVar + " " + getVituralName() + SpecialCharacter.END_OF_STATEMENT;
        }

        return input;
    }

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
}
