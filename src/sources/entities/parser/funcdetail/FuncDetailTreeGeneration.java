package entities.parser.funcdetail;

import entities.SpecialCharacter;
import entities.UETLogger;
import entities.common.IGTestConstant;
import entities.parser.dependency.Dependency;
import entities.parser.dependency.FunctionCallDependency;
import entities.parser.dependency.IncludeHeaderDependency;
import entities.parser.dependency.TypeDependency;
import entities.parser.object.*;
import entities.search.Search;
import entities.search.SearchCondition;
import entities.search.condition.ClassNodeCondition;
import entities.search.condition.GlobalVariableNodeCondition;
import entities.search.condition.StructNodeCondition;
import entities.utils.TemplateUtils;
import entities.utils.Utils;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;

import java.util.ArrayList;
import java.util.List;

public class FuncDetailTreeGeneration implements IFuncDetailTreeGeneration {
    private final static UETLogger logger = UETLogger.get(FuncDetailTreeGeneration.class);

    public FuncDetailTreeGeneration(RootNode root, ICommonFunctionNode fn) {
        generateTree(root, fn);
    }

    @Override
    public void generateTree(RootNode root, ICommonFunctionNode fn) {
        logger.debug("generateGlobalSubTree");
        generateGlobalSubTree(root, fn);

        logger.debug("generateUUTSubTree");
        generateUUTSubTree(root, fn);

        logger.debug("generateStubSubTree");
        generateStubSubTree(root, fn);
    }

    List<INode> includeNodes = new ArrayList<>();

    private boolean isSystemUnit(INode unit) {
//        List<INode> sources = Search
//                .searchNodes(Environment.getInstance().getProjectNode(), new SourcecodeFileNodeCondition());

//        return !sources.contains(unit);
        return true;
    }

    private List<Node> getAllIncludedNodes(INode n) {
        List<Node> output = new ArrayList<>();

        if (n != null) {
            try {
                for (Dependency child : n.getDependencies()) {
                    if (child instanceof IncludeHeaderDependency) {
                        if (child.getStartArrow().equals(n)) {
                            includeNodes.add(n);

                            INode end = child.getEndArrow();
                            if (!includeNodes.contains(end) && !isSystemUnit(end)) {
                                output.add((Node) end);
                                /*
                                 * In case recursive include
                                 */
                                output.addAll(getAllIncludedNodes(end));
                            }
                        }
                    }
                }
            } catch (StackOverflowError e) {
                e.printStackTrace();
            }
        }

        return output;
    }


    @Override
    public void generateGlobalSubTree(RootNode root, ICommonFunctionNode fn) {
        RootNode globalRoot = new RootNode(NodeType.GLOBAL);

        /*
         * Them cac bien global co trong unit
         */
        INode unit = Utils.getSourcecodeFile(fn);
        List<INode> globalVariables = Search.searchNodes(unit, new GlobalVariableNodeCondition());

        List<Node> includedNodes = getAllIncludedNodes(unit);
        for (Node node : includedNodes) {
            List<INode> includedGlobalVariables = Search.searchNodes(node, new GlobalVariableNodeCondition());
            includedGlobalVariables.forEach(global -> {
                if (!globalVariables.contains(global))
                    globalVariables.add(global);
            });
        }

        for (INode node : globalVariables) {
            if ((node instanceof ExternalVariableNode)) {
                boolean isConst = ((ExternalVariableNode) node).getRawType().contains("const ");
                if (!isConst)
                    globalRoot.addElement(node);
            }
        }

        /*
         * Them instance trong truong hop test method cua class
         */
        if (true){//!Environment.getInstance().isC()) {
            List<INode> instances = searchAllInstances(unit);

            for (INode instance : instances) {
                InstanceVariableNode instanceVarNode = generateInstance(instance);

                globalRoot.addElement(instanceVarNode);
            }
        }

        root.addElement(globalRoot);
    }

    private InstanceVariableNode generateInstance(INode correspondingType) {
        InstanceVariableNode instance = new InstanceVariableNode();
        String type = Search.getScopeQualifier(correspondingType);

        if (correspondingType instanceof ClassNode && ((ClassNode) correspondingType).isTemplate()) {
            String[] templateParams = TemplateUtils.getTemplateParameters(correspondingType);
            if (templateParams != null) {
                type += TemplateUtils.OPEN_TEMPLATE_ARG;

                for (String param : templateParams)
                    type += param + ", ";

                type += TemplateUtils.CLOSE_TEMPLATE_ARG;
                type = type.replace(", >", ">");
            }
        }

        instance.setCoreType(type);
        instance.setRawType(type);
        instance.setReducedRawType(type);

        String instanceVarName = type.replaceAll("[^\\w]", SpecialCharacter.UNDERSCORE);
        instanceVarName = IGTestConstant.INSTANCE_VARIABLE + SpecialCharacter.UNDERSCORE + instanceVarName;
        instance.setName(instanceVarName);

        instance.setParent(correspondingType);

        instance.setCorrespondingNode(correspondingType);
        TypeDependency d = new TypeDependency(instance, correspondingType);
        instance.getDependencies().add(d);
        correspondingType.getDependencies().add(d);

        return instance;
    }

    private List<INode> searchAllInstances(INode unit) {
        List<INode> instances = new ArrayList<>();

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new StructNodeCondition());
        conditions.add(new ClassNodeCondition());
        instances = Search.searchNodes(unit, conditions);

        instances.removeIf(instance -> ((StructOrClassNode) instance).getVisibility() != ICPPASTVisibilityLabel.v_public);

        return instances;
    }

    @Override
    public void generateUUTSubTree(RootNode root, ICommonFunctionNode fn) {
        RootNode uutRoot = new RootNode(NodeType.UUT);
        uutRoot.addElement(fn);
        root.addElement(uutRoot);
    }

    @Override
    public void generateStubSubTree(RootNode root, ICommonFunctionNode fn) {
        RootNode dontStubRoot = new RootNode(NodeType.DONT_STUB);
        RootNode stubRoot = new RootNode(NodeType.STUB);

        for (Dependency d : fn.getDependencies()) {
            if (d instanceof FunctionCallDependency && ((FunctionCallDependency) d).fromNode(fn)) {
                INode referNode = d.getEndArrow();
                stubRoot.addElement(referNode);
            }
        }

        root.addElement(dontStubRoot);
        root.addElement(stubRoot);
    }
}
