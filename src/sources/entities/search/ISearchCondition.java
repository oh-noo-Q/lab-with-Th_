package entities.search;

import entities.parser.object.INode;

public interface ISearchCondition {

    boolean isSatisfiable(INode n);

}