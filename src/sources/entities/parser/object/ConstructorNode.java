package entities.parser.object;

import entities.cfg.ICFG;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent a constructor
 *
 * @author DucAnh
 */
public class ConstructorNode extends AbstractFunctionNode {

//    public static final String PREFIX_NAME_BY_INDEX = "aka_parameter";

    public static void main(String[] args) {
//        ProjectParser parser = new ProjectParser(new File(Paths.SAMPLE01), null);
//        ProjectParser parser = new ProjectParser(new File(Paths.JOURNAL_TEST));
//        StructureNode structureNode = (StructureNode) Search
//                .searchNodes(parser.getRootTree(), new StructNodeCondition(), "BaseClass").get(0);
    }

    @Override
    public List<IVariableNode> getArguments() {
        if (this.arguments == null || this.arguments.size() == 0) {
            this.arguments = new ArrayList<>();

//            int index = 0;
            for (INode child : getChildren())
                if (child instanceof VariableNode) {
//                    index++;
//                    child.setName(PREFIX_NAME_BY_INDEX + index);
                    this.arguments.add((IVariableNode) child);
                }
        }
        return this.arguments;
    }

    @Override
    public ICFG generateCFGofExecutionSourcecode()
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPath(String typeOfCoverage)
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForStmCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForStmCOverage()
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForBranchCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForBranchCoverage()
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForMcdcCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForMcdcCOverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePath(String typeOfCoverage)
    {
        return null;
    }

    @Override
    public String getHighlightedFunctionPathForBasisPathCoverage()
    {
        return null;
    }

    @Override
    public String getProgressCoveragePathForBasisPathCoverage()
    {
        return null;
    }

    @Override
    public String getTemplateFilePath()
    {
        return null;
    }
}
