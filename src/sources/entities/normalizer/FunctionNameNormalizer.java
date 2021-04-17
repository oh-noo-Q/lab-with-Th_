package entities.normalizer;

import entities.parser.ProjectParser;
import entities.parser.object.IFunctionNode;
import entities.search.Search;
import entities.search.condition.FunctionNodeCondition;
import entities.SpecialCharacter;

import java.io.File;

/**
 * Ex1: "int test(Student a)"----------------> "int ClassA::test(Student a)
 * <br/>
 * <p>
 * Ex2: "int test2(Apple a)"----------------> "int
 * Namespace1::Namespace2:ClassB::test2(Apple a) <br/>
 *
 * @author ducanhnguyen
 */
public class FunctionNameNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {
    public static void main(String[] args) {
//        ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1_2));
//        IFunctionNode function = (IFunctionNode) Search
//                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "MathUtils::MinusTest(int,int)").get(0);
//
//        FunctionNameNormalizer normalizer = new FunctionNameNormalizer();
//        normalizer.setFunctionNode(function);
//        normalizer.normalize();
//        System.out.println(normalizer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        normalizeSourcecode = getFunctionNode().getAST().getRawSignature();
        String prefixPathofFunction = getFunctionNode().getLogicPathFromTopLevel();
        if (prefixPathofFunction.length() > 0)
            if (prefixPathofFunction.startsWith(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS))
                normalizeSourcecode = normalizeSourcecode.replace(getFunctionNode().getSimpleName(),
                        getFunctionNode().getLogicPathFromTopLevel() + getFunctionNode().getSimpleName());
            else {
                String newName = getFunctionNode().getLogicPathFromTopLevel()
                        + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + getFunctionNode().getSingleSimpleName();
                normalizeSourcecode = normalizeSourcecode.replace(getFunctionNode().getSimpleName(), newName);
            }

    }
}
