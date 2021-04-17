package entities.cfg;

import java.util.List;

import entities.coverage.ICFGUpdater;
import entities.testpath.FullTestpaths;
import entities.testpath.ITestpathInCFG;
import entities.testpath.PartialTestpaths;
import entities.testpath.PossibleTestpathGeneration;
import entities.testpath.TestpathString_Marker;
import entities.solverhelper.IPathConstraints;
import entities.parser.object.IFunctionNode;

/**
 * Represent control flow graph
 *
 * @author DucAnh
 */
public interface ICFG {
	/**
	 * Find a cfg node having the specified content
	 *
	 * @param content
	 * @return
	 */
	ICfgNode findFirstCfgNodeByContent(String content);

	/**
	 * Find a cfg node having the specified id
	 *
	 * @param id
	 * @return
	 */
	ICfgNode findById(int id);

	/**
	 * Get number of branches in CFG
	 *
	 * @return
	 */
	int computeNumOfBranches();

	/**
	 * Get number of statements in CFG
	 *
	 * @return
	 */
	int computeNumofStatements();

	/**
	 * Get the beginning node of CFG
	 *
	 * @return
	 */
	ICfgNode getBeginNode();

	/**
	 * Get all nodes in CFG
	 *
	 * @return
	 */
	List<ICfgNode> getAllNodes();

	List<BranchInCFG> getUnvisitedBranches();

	/**
	 * Get the number of visited statements
	 *
	 * @return
	 */
	int computeNumofVisitedStatements();

	List<BranchInCFG> getVisitedBranches();

	List<List<ICfgNode>> getVisitedBasisPaths();

	/**
	 * Get the number of visited branches
	 *
	 * @return
	 */
	int computeNumofVisitedBranches();

	/**
	 * Get all visited statements
	 *
	 * @return
	 */
	List<ICfgNode> getVisitedStatements();

	/**
	 * Get the number of unsited statements in CFG
	 *
	 * @return
	 */
	int computeNumofUnvisitedStatements();

	/**
	 * Get all unvisited statements
	 *
	 * @return
	 */
	List<ICfgNode> getUnvisitedStatements();

	/**
	 * Each node in CFG may be visited or unvisited. This function used to reset
	 * the state of all nodes to unvisited
	 */
	void resetVisitedStateOfNodes();

	/**
	 * Each node in CFG is represented by an ID
	 */
	void setIdforAllNodes();

	/**
	 * Set the function node corresponding to the current CFG
	 *
	 * @return
	 */
	IFunctionNode getFunctionNode();

	/**
	 * Get the function node corresponding to the current CFG
	 *
	 * @param functionNode
	 */
	void setFunctionNode(IFunctionNode functionNode);

	FullTestpaths getTestpathsContainingUncoveredStatements(FullTestpaths inputTestpaths);

	FullTestpaths getTestpathsContainingUncoveredBranches(FullTestpaths inputTestpaths);

	/**
	 * Step 1. Get all possible test paths (P) <br/>
	 * 
	 * Step 2. Get all partial test paths (in P) which go though an unvisited
	 * branch
	 */
	PartialTestpaths getPartialTestpathcontainingUnCoveredBranches_Strategy1();

	/**
	 * Step 1. From the given test path, generate path constraints
	 * 
	 * Step 2. Negate constraints
	 * 
	 * @param testpath
	 * @return
	 */
	List<IPathConstraints> getNegatedConstraints_Strategy2(ITestpathInCFG testpath);

	PartialTestpaths getPartialTestpathcontainingUnCoveredStatements();

	/**
	 * Generate all possible test paths
	 *
	 * @param maximumIterationForEachLoop
	 *            maximum iterations for each loop
	 * @return
	 */
	FullTestpaths generateAllPossibleTestpaths(int maximumIterationForEachLoop);

	ICFGUpdater updateVisitedNodes_Marker(TestpathString_Marker testpath);

	PossibleTestpathGeneration getPossibleTestpaths();

	void setPossibleTestpaths(PossibleTestpathGeneration possibleTestpaths);

	int getMaxId();

	float computeBranchCoverage();

	float computeStatementCoverage();
}
