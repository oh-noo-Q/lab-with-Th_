package entities.parser.dependency;

import entities.parser.object.INode;

public class TypedefDependency extends Dependency {

    public TypedefDependency(INode startArrow, INode endArrow) {
        super(startArrow, endArrow);
    }

    public TypedefDependency() {}
}
