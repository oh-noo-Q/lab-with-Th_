package entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import entities.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class config
{
    private static config _instance = null;

    private config(){}

    public static config getInstance()
    {
        if (_instance == null)
            _instance =new config();

        return _instance;
    }

    final static UETLogger logger = UETLogger.get(config.class);

    public static final File LOCAL_DIRECTORY = new File("local");
    //    public static final File DEFAULT_WORKING_DIRECTORY = new File("local/working-directory");
    public static final File SETTING_PROPERTIES_PATH = new File("local/application.uet");

    @Expose
    private String localDirectory = LOCAL_DIRECTORY.getAbsolutePath();

    @Expose
    private String workingDirectory = "";

    @Expose
    private String openingWorkspaceDirectory = "";

    @Expose
    private String openWorkspaceConfig = "";

    @Expose
    private String z3Path = "";

//    @Expose
//    private String creatingWorkspaceDirectory = "";// not null when we create new environment
//
//    @Expose
//    private String creatingWorkspaceConfig = "";// not null when we create new environment

    @Expose
    private List<String> recentEnvironments = new ArrayList<>(); // path to env files

    synchronized public config fromJson() {
        if (SETTING_PROPERTIES_PATH.exists()) {
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.setPrettyPrinting().create();
            config setting = gson.fromJson(Utils.readFileContent(SETTING_PROPERTIES_PATH), config.class);
            return setting;
        } else {
            logger.error("The " + SETTING_PROPERTIES_PATH.getAbsolutePath() + " does not exist!");
            config config = new config();
//            config.setWorkingDirectory(DEFAULT_WORKING_DIRECTORY.getAbsolutePath());
            config.exportToJson();
            return config;
        }
    }

    synchronized public void exportToJson() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.setPrettyPrinting().create();
        String json = gson.toJson(this);
        Utils.writeContentToFile(json, SETTING_PROPERTIES_PATH.getAbsolutePath());
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public config setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        new File(workingDirectory).mkdirs();
        return this;
    }

    public String getOpeningWorkspaceDirectory() {
        return openingWorkspaceDirectory;
    }

    public config setOpeningWorkspaceDirectory(String openingWorkspaceDirectory) {
        this.openingWorkspaceDirectory = openingWorkspaceDirectory;
        new File(openingWorkspaceDirectory).mkdirs();

        this.workingDirectory = new File(openingWorkspaceDirectory).getParent();

        return this;
    }

    public String getLocalDirectory() {
        return localDirectory;
    }

    public config setLocalDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
        new File(localDirectory).mkdirs();
        return this;
    }

    public String getOpenWorkspaceConfig() {
        return openWorkspaceConfig;
    }

    public config setOpenWorkspaceConfig(String openWorkspaceConfig) {
        this.openWorkspaceConfig = openWorkspaceConfig;
        return this;
    }

    public List<String> getRecentEnvironments() {
        for (int i = recentEnvironments.size() - 1; i >=0 ;i --)
            if (!new File(recentEnvironments.get(i)).exists())
                recentEnvironments.remove(i);
        return recentEnvironments;
    }

    public config setRecentEnvironments(List<String> recentEnvironments) {
        this.recentEnvironments = recentEnvironments;
        return this;
    }


    public String getZ3Path() {
        return z3Path;
    }

    public config setZ3Path(String z3Path) {
        this.z3Path = z3Path;
        return this;
    }
}
