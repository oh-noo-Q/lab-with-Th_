package entities.parser.systemlibrary;


import entities.UETLogger;
import entities.parser.object.HeaderNode;
import entities.parser.object.INode;
import entities.thread.AbstractTask;

import java.io.File;

public class SystemHeaderParseTask extends AbstractTask<HeaderNode>
{
    private static final UETLogger logger = UETLogger.get(SystemHeaderParseTask.class);

    private String path;
    private INode root;

    private static final String WD = "";// new WorkspaceConfig().fromJson().getHeaderPreprocessorDirectory();

    public SystemHeaderParseTask(INode root, String path) {
        this.path = path;
        this.root = root;
    }

    @Override
    protected HeaderNode call() {
        SystemHeaderGenerator generator = new SystemHeaderGenerator(WD, path);
        String sourcePath = generator.generate();

        if (sourcePath == null || !new File(sourcePath).exists())
            return null;

        try {
            SystemHeaderParser parser = new SystemHeaderParser(sourcePath, path);
            HeaderNode headerNode = parser.parse();

            root.getChildren().add(headerNode);
            logger.debug(headerNode + " done");

            return headerNode;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Fail to parse " + path + ": " + e.getMessage());
            return null;
        }
    }
}
