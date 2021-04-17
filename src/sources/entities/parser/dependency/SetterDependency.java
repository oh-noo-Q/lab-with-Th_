package entities.parser.dependency;

import entities.parser.object.INode;

public class SetterDependency extends Dependency {

    public SetterDependency(INode startArrow, INode endArrow) {
        super(startArrow, endArrow);
    }

}
