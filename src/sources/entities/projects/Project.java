package entities.projects;

import entities.common.SupportedCompilers;
import javafx.util.Pair;

import java.util.*;

public class Project
{
    private String name = "";

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private SupportedCompilers compiler = SupportedCompilers.GNU_NATIVE_C;

    private String preprocessCommand = "";
    private String preprocessExtraFlags = "";

    private String compileCommand = "";
    private String compileExtraFlag = "";

    private String outputFileFlag = "";
    private String objectFileExtension = "";

    private String linkerCommand = "";
    private String linkerOption = "";

    private String debuggerCommand = "";
    private boolean commandLineDebuggerEnabled = false;

    private ArrayList<Pair<String, String>> variableList = new ArrayList<Pair<String, String>>();
}
