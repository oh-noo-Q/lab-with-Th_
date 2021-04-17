package entities;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

public class UILogger extends MessagesPaneLogger {
    final static UETLogger logger = UETLogger.get(UILogger.class);
    private static String logMode = UILogger.MODE_CLI;

    /**
     * singleton partern
     */
    private static UILogger uiLogger = null;

    public UILogger(String name, TextArea textArea) {
        super(name, textArea);
    }

    public UILogger(String name) {
        super(name);
    }

    public static UILogger getUiLogger() {
        if (logMode == null){
            uiLogger = new UILogger(UILogger.class.getSimpleName());
        } else if (logMode.equals(UILogger.MODE_GUI)) {
            TextArea messagesArea = null;// BaseSceneController.getBaseSceneController().getAkaMessagesTabController().getTextArea();

            if (uiLogger == null) {
                uiLogger = new UILogger(UILogger.class.getSimpleName(), messagesArea);
            }
        } else if (logMode.equals(UILogger.MODE_CLI)){
            if (uiLogger == null) {
                uiLogger = new UILogger(UILogger.class.getSimpleName());
            }
        }
        return uiLogger;
    }

    // UILogger have to initialize when base scene controller is initialized
    public static void initializeUiLogger(TextArea textArea) {
        UILogger.setLogMode(UILogger.MODE_GUI);
        uiLogger = new UILogger(UILogger.class.getSimpleName(), textArea);
    }

    // UIlogger have to reinitialize after create new environment, open an existed environment
    // after the BaseSceneController is prepared
    public static void reinitializeUiLogger() {
        uiLogger = null;
        uiLogger = getUiLogger();
    }

    public void log(String message) {
        // use Platform runLater to log realtime
//        if (UIController.getPrimaryStage() != null)
//            info(message);
    }

    public void logToBothUIAndTerminal(String message) {
        log(message);
        logger.debug(message);
    }

    public void clear() {
        Platform.runLater(() -> this.getTextArea().clear());
    }

    public static void setLogMode(String logMode) {
        UILogger.logMode = logMode;
    }

    public static String MODE_CLI = "MODE_CLI";
    public static String MODE_GUI = "MODE_GUI";
}
