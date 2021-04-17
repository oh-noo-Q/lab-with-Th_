package entities.parser;

import java.io.File;

import entities.parser.object.IProjectNode;
import entities.parser.object.IProcessNotify;

/**
 * Parse the given project down to method, parameter, attribute level
 *
 * @author ducanh
 */
public interface IProjectParser {

    IProcessNotify getNotify();

    void setNotify(IProcessNotify notify);

    /**
     * Get the path of the given C/C++ project
     *
     * @return
     */
    File getProjectPath();

    void setProjectPath(File projectPath);

    /**
     * Get the root of the structure tree
     *
     * @return
     */
    IProjectNode getRootTree();

}
