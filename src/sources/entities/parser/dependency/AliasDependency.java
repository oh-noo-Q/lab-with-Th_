package entities.parser.dependency;


import entities.parser.object.INode;

public class AliasDependency extends Dependency {

    public AliasDependency(INode owner, INode refferedNode) {
        super(owner, refferedNode);
    }

}
