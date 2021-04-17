package entities.parser.externalvariable;

import java.util.List;

import entities.parser.object.IFunctionNode;
import entities.parser.object.IVariableNode;
import entities.search.*;

/**
 * Find all external variables of a function
 *
 * @author ducanhnguyen
 */
public interface IExternalVariableDetecter extends ISearch {
    /**
     * Find external variables of a function
     *
     * @return
     */
    List<IVariableNode> findExternalVariables();

    IFunctionNode getFunction();

    void setFunction(IFunctionNode function);
}