package entities.parser.dependency;

import entities.parser.object.INode;

public class GetterDependency extends Dependency {

    public GetterDependency(INode startArrow, INode endArrow) {
        super(startArrow, endArrow);
    }

}
