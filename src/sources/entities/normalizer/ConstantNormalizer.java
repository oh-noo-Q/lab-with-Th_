package entities.normalizer;

import entities.parser.ProjectParser;
import entities.parser.object.IFunctionNode;
import entities.search.Search;
import entities.search.condition.FunctionNodeCondition;

import java.io.File;

/**
 * Ex:"if (status != EXIT_SUCCESS) emit_try_help ();" --->"if (status != 0) emit_try_help ();"
 *
 * @author DucAnh
 */
public class ConstantNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {
    public ConstantNormalizer() {

    }

    public ConstantNormalizer(IFunctionNode functionNode) {
        this.functionNode = functionNode;
    }

    public static void main(String[] args) {
//        ProjectParser parser = new ProjectParser(new File(Paths.CORE_UTILS));
//        IFunctionNode function = (IFunctionNode) Search
//                .searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "usage(int)").get(0);
//
//        System.out.println(function.getAST().getRawSignature());
//        ConstantNormalizer normalizer = new ConstantNormalizer();
//        normalizer.setFunctionNode(function);
//        normalizer.normalize();
//
//        System.out.println(normalizer.getTokens());
//        System.out.println(normalizer.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        String content = functionNode.getAST().getRawSignature();
        normalizeSourcecode = content.replace("EXIT_SUCCESS", "0");
    }
}
