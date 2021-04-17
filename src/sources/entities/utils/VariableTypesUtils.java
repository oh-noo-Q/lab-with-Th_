package entities.utils;

import entities.testdatainit.VariableTypes;
import entities.parser.ProjectParser;
import entities.parser.object.EnumNode;
import entities.parser.object.INode;
import entities.parser.object.TypedefDeclaration;
import entities.search.Search;
import entities.search.SearchCondition;
import entities.search.condition.EnumNodeCondition;
import entities.search.condition.TypedefNodeCondition;
import entities.utils.VariableTypeUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DucToan on 27/07/2017
 */
public class VariableTypesUtils {
    public static boolean isEnumNode(String type) {
        ProjectParser parser = new ProjectParser(new File(""));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new EnumNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static EnumNode findEnumNode(String type) {
        ProjectParser parser = new ProjectParser(new File(""));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new EnumNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                return (EnumNode) mydefine;
            }
        }

        return null;
    }

    public static boolean isDefineNode(String type) {
        ProjectParser parser = new ProjectParser(new File(""));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new TypedefNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isDefineNodeOfBasicType(String type) {
        ProjectParser parser = new ProjectParser(new File(""));

        List<SearchCondition> conditions = new ArrayList<>();
        conditions.add(new TypedefNodeCondition());

        List<INode> mydefines = Search.searchNodes(parser.getRootTree(), conditions);

        for (INode mydefine : mydefines) {
            if (mydefine.getNewType().equals(type)) {
                String oldType = ((TypedefDeclaration) mydefine).getOldType();
                if (VariableTypeUtils.isBasic(oldType))
                    return true;
            }
        }

        return false;
    }
}
