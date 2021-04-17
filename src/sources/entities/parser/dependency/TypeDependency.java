package entities.parser.dependency;

import entities.parser.object.INode;

public class TypeDependency extends Dependency {

    public TypeDependency(INode startArrow, INode endArrow) {
        super(startArrow, endArrow);
    }

    public TypeDependency() {}
}
