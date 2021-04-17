package entities.normalizer;

import entities.parser.ProjectParser;
import entities.parser.dependency.AbstractDependencyGeneration;
import entities.parser.dependency.RealParentDependency;
import entities.parser.dependency.finder.Level;
import entities.parser.dependency.finder.VariableSearchingSpace;
import entities.parser.object.*;
import entities.search.Search;
import entities.search.condition.ClassvsStructvsNamespaceCondition;
import entities.search.condition.FunctionNodeCondition;
import entities.SpecialCharacter;
import entities.UETLogger;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;

import java.io.File;
import java.util.List;

/**
 * Get the real parent of a function.
 *
 * I do not consider DefinitionFunctionNode.
 *
 * I just consider ConstructorNode, DescontructorNode, and FunctionNode
 *
 * Real parent is physical parent if the function belongs to file level (not in any class/namespace)
 * For example: source code file x.cpp
 #include "../Person.h"
 int Person::getDoubleWeight(){...} // this function is defined in the class .../Person.h/Person
 *
 *
 * The function Person::getDoubleWeight() has a physical parent: x.cpp
 *
 * The function Person::getDoubleWeight() has a real parent (or logical parent): .../Person.h/Person
 */
public class RealParentDependencyGeneration extends AbstractDependencyGeneration {
    final static UETLogger logger = UETLogger.get(RealParentDependencyGeneration.class);

    public RealParentDependencyGeneration() throws Exception {
    }

    public static void main(String[] args) throws Exception {
        ProjectParser projectParser = new ProjectParser(new File("/Users/ducanhnguyen/Documents/akautauto/datatest/lamnt/mysample"));
        projectParser.setCpptoHeaderDependencyGeneration_enabled(true);
        projectParser.setExpandTreeuptoMethodLevel_enabled(true);
        projectParser.setExtendedDependencyGeneration_enabled(true);
        projectParser.setParentReconstructor_enabled(true);

        IFunctionNode sampleNode = (IFunctionNode) Search.searchNodes(projectParser.getRootTree(),
                new FunctionNodeCondition(), "Person::getDoubleWeight()").get(0);
        logger.debug(sampleNode.getAST().getRawSignature());
        System.out.println(sampleNode.getRealParent().getAbsolutePath());
    }

    @Override
    public void dependencyGeneration(INode functionNode) {
        if (functionNode instanceof AbstractFunctionNode) {

            AbstractFunctionNode f = (AbstractFunctionNode) functionNode;

            /*
             * Ex: void SinhVien::timSinhVien(int msv){...}
             *
             */
            if (f.getSimpleName().contains(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS)) {
                    reconstructWithAST(f);
            } else{
                RealParentDependency d = new RealParentDependency(functionNode, f.getParent());
                if (!functionNode.getDependencies().contains(d))
                    functionNode.getDependencies().add(d);
                if (!f.getParent().getDependencies().contains(d))
                    f.getParent().getDependencies().add(d);
            }
        }
    }

    private void reconstructWithAST(AbstractFunctionNode functionNode) {
        IASTName name = functionNode.getAST().getDeclarator().getName();

        if (name instanceof ICPPASTQualifiedName) {
            ICPPASTNameSpecifier[] chainNames = ((ICPPASTQualifiedName) name).getQualifier();
            String realParentName = chainNames[chainNames.length - 1].getRawSignature();

            List<Level> space = new VariableSearchingSpace(functionNode).generateExtendSpaces();

            boolean found = false;

            for (Level l : space) {
                for (INode n : l) {
                    List<INode> possibleNodes = Search.searchNodes(n, new ClassvsStructvsNamespaceCondition());
                    for (INode possibleNode : possibleNodes) {
                        if (possibleNode.getName().equals(realParentName)) {

                            RealParentDependency d = new RealParentDependency(functionNode, possibleNode);
                            if (!functionNode.getDependencies().contains(d))
                                functionNode.getDependencies().add(d);
                            if (!possibleNode.getDependencies().contains(d))
                                possibleNode.getDependencies().add(d);
                            found = true;
                            break;
                        }
                    }

                    if (found) break;
                }

                if (found) break;
            }
        }
    }
}
