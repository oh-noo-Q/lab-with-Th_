package entities.solverhelper;

import entities.parser.object.Parameter;
import entities.testpath.ITestpathInCFG;
import entities.solverhelper.memory.IVariableNodeTable;
import entities.solverhelper.memory.VariableNodeTable;

import java.util.List;

/**
 * Interface of symbolic execution for a test path
 *
 * @author ducanhnguyen
 */
public interface ISymbolicExecution {
	String NO_SOLUTION_CONSTRAINT = "1<0";

	String NO_SOLUTION_CONSTRAINT_SMTLIB = "< 1 0";

	String UNSAT_IN_Z3 = "unsat";

	String ALWAYS_TRUE_CONSTRAINT = "1>0";

	String TO_INT_Z3 = "to_int";
	/**
	 * Represent type of statements
	 */
	int UNSPECIFIED_STATEMENT = -1;

	/**
	 * Ex1: a+b <br/>
	 * Ex2: a = new int[2]
	 */

	int BINARY_ASSIGNMENT = 0;
	/**
	 * Ex: a++; a--
	 */
	int UNARY_ASSIGNMENT = 1;

	int CONDITION = 2;

	int DECLARATION = 3;

	int RETURN = 5;

	int THROW = 6;

	int IGNORE = 7;

	int NAMESPACE = 8;

	String NAMESPACE_SIGNAL = "using namespace ";

	IPathConstraints getConstraints();

	void setConstraints(PathConstraints constraints);

	Parameter getParameters();

	void setParameters(Parameter parameters);

	String getReturnValue();

	void setReturnValue(String returnValue);

	IVariableNodeTable getTableMapping();

	void setTableMapping(VariableNodeTable tableMapping);

	ITestpathInCFG getTestpath();

	void setTestpath(ITestpathInCFG testpath);

	List<NewVariableInSe> getNewVariables();

	void setNewVariables(List<NewVariableInSe> newVariables);

	PathConstraints getNormalizedPathConstraints();
}