package entities.search;

import entities.parser.object.INode;

public abstract class SearchCondition implements ISearchCondition {

    /*
     * (non-Javadoc)
     *
     * @see
     * com.fit.utils.search.ISearchCondition#isSatisfiable(com.fit.tree.object.
     * INode)
     */
    @Override
    public abstract boolean isSatisfiable(INode n);
}
