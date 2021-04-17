package entities.utils;

import entities.UETLogger;
import entities.config;

public class PathUtils {
    private final static UETLogger logger = UETLogger.get(PathUtils.class);
    public static String toAbsolute(String relativePath) {
        String absolutePath = relativePath;

        int offset = 0;

        if (relativePath.startsWith("..")) {
            offset = 2;
        } else if (relativePath.startsWith(".")) {
            offset = 1;
        }

        if (offset > 0) {
//            ProjectNode root = Environment.getInstance().getProjectNode();
//            if (root != null) {
//                absolutePath = root.getFile().getParent() + relativePath.substring(offset);
//            } else {
                String workspace = config.getInstance().fromJson().getOpeningWorkspaceDirectory();
                int index = -1;
                if (Utils.isWindows()) {
                    index = workspace.indexOf("\\aka-working-space");
                } else if (Utils.isUnix() || Utils.isMac()) {
                    index = workspace.indexOf("/aka-working-space");
                }
                if (index < 0) {
                    logger.debug("relativePath: " + relativePath);
                    if (Utils.isWindows()) {
                        logger.debug("wanna find " + "\\aka-working-space");
                    } else if (Utils.isUnix() || Utils.isMac()) {
                        logger.debug("wanna find " + "/aka-working-space");
                    }
                    absolutePath = relativePath;
                }
                else {
                    String prefix = workspace.substring(0, index);
                    absolutePath = prefix + relativePath.substring(offset);
//                }
            }
        }

        absolutePath = Utils.doubleNormalizePath(absolutePath);

        return absolutePath;
    }

    public static String toRelative(String absolutePath) {
        if (absolutePath == null)
            return "";

        String workspace = config.getInstance().fromJson().getOpeningWorkspaceDirectory();

        int index = -1;
        if (Utils.isWindows()) {
            index = workspace.indexOf("\\aka-working-space");
        } else if (Utils.isUnix() || Utils.isMac()) {
            index = workspace.indexOf("/aka-working-space");
        }

        if (index < 0) {
            logger.debug("absolutePath: " + absolutePath);
            if (Utils.isWindows()) {
                logger.debug("wanna find " + "\\aka-working-space");
            } else if (Utils.isUnix() || Utils.isMac()) {
                logger.debug("wanna find " + "/aka-working-space");
            }
            return absolutePath;
        }

        String prefix = workspace.substring(0, index);
        return absolutePath.replaceFirst("\\Q" + prefix + "\\E", ".");
    }
}
