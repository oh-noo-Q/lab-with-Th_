package entities.parser.dependency;

import entities.parser.ProjectParser;
import entities.parser.object.AvailableTypeNode;
import entities.parser.object.INode;
import entities.parser.object.StructOrClassNode;
import entities.parser.object.VariableNode;
import entities.search.Search;
import entities.search.condition.ClassNodeCondition;
import entities.UETLogger;
import entities.parser.object.INode;

import java.io.File;
import java.util.List;

public class ExtendedDependencyGeneration extends AbstractDependencyGeneration {
    final static UETLogger logger = UETLogger.get(ExtendedDependencyGeneration.class);

    public ExtendedDependencyGeneration() {
    }

    public static void main(String[] args) {
//        ProjectParser parser = new ProjectParser(new File("/Users/ducanhnguyen/Documents/akautauto/datatest/duc-anh/Algorithm"));
//        parser.setCpptoHeaderDependencyGeneration_enabled(true);
//        parser.setExpandTreeuptoMethodLevel_enabled(true);
//
//        INode root = parser.getRootTree();
//        List<INode> nodes = Search.searchNodes(root, new ClassNodeCondition());
//        for (INode n:nodes)
//            new ExtendedDependencyGeneration().dependencyGeneration(n);
//
//        ToString treeDisplayer = new ReducedDependencyTreeDisplayer(root);
//        System.out.println(treeDisplayer.getTreeInString());
    }

    public void dependencyGeneration(INode n) {
        if (n instanceof StructOrClassNode) {
            // if the current node is has ability to inherit and it is never analyzed extended dependency before
            List<String> extendClassNames = ((StructOrClassNode) n).getExtendedNames();
            if (extendClassNames != null)
                for (String extendClassName : extendClassNames) {
                    /*
                     * Create temporary variable
                     */
                    VariableNode v = new VariableNode();
                    v.setCoreType(extendClassName);
                    v.setRawType(extendClassName);
                    v.setName(extendClassName);
                    v.setParent(n);
                    /*
                     * Find type of temporary variable
                     */
                    CTypeDependencyGeneration typeGen;
                    try {
                        typeGen = new CTypeDependencyGeneration();
                        typeGen.setAddToTreeAutomatically(false);// because the variable node is fake, we can not update the tree
                        // if we found any type dependencies
                        typeGen.dependencyGeneration(v);

                        INode correspondingNode = typeGen.getCorrespondingNode();
                        if (correspondingNode != null && !(correspondingNode instanceof AvailableTypeNode)) {
                            INode refferedNode = correspondingNode;

                            ExtendDependency d = new ExtendDependency(n, refferedNode);

                            if (!n.getDependencies().contains(d)) {
                                n.getDependencies().add(d);
                                refferedNode.getDependencies().add(d);
                                logger.debug("Found an extended dependency: " + d.toString());
                            }
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                }
        }
    }
}
