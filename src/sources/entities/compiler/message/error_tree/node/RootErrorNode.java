package entities.compiler.message.error_tree.node;

import entities.parser.object.SourcecodeFileNode;

public class RootErrorNode extends ErrorNode {
    private SourcecodeFileNode<?> source;

    public SourcecodeFileNode<?> getSource() {
        return source;
    }

    public void setSource(SourcecodeFileNode<?> source) {
        this.source = source;
    }
}
