package entities.resolver;

import entities.parser.object.INode;

import java.util.List;

public interface IUndeclaredResolver {
    void resolve();

    List<ResolvedSolution> getSolutions();
}
