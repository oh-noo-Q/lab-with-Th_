package entities.testdata;

import entities.parser.object.ICommonFunctionNode;

public interface IConstructorExpanableDataNode {
    ICommonFunctionNode getSelectedConstructor();

    void chooseConstructor(String constructor) throws Exception;

}
