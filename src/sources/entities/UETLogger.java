package entities;


import org.apache.log4j.Level;

public class UETLogger extends org.apache.log4j.Logger
{

    protected UETLogger(String name) {
        super(name);
    }

    public static UETLogger get(Class<?> c) {
        org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();

        UETLogger logger = new UETLogger(c.getName());

        logger.repository = root.getLoggerRepository();
        logger.parent = root;

        // disable stdout target logger if using command line version
//        if (UIController.getPrimaryStage() == null) {
//            root.removeAppender("stdout");
//        }

        return logger;
    }

    private static final String FQCN = UETLogger.class.getName();

    @Override
    public void debug(Object message) {
//        if (LoadingPopupController.getInstance() != null)
//            LoadingPopupController.getInstance().setText(message);

        message = "[" + Thread.currentThread().getName() + "] " + message;
        if (!this.repository.isDisabled(10000)) {
            if (Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel())) {
                this.forcedLog(FQCN, Level.DEBUG, message, null);
            }
        }
    }

    @Override
    public void error(Object message) {
        message = "[" + Thread.currentThread().getName() + "] " + message;
        if (!this.repository.isDisabled(40000)) {
            if (Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel())) {
                this.forcedLog(FQCN, Level.ERROR, message, null);
            }

        }
    }

    @Override
    public void info(Object message) {
        message = "[" + Thread.currentThread().getName() + "] " + message;
        if (!this.repository.isDisabled(20000)) {
            if (Level.INFO.isGreaterOrEqual(this.getEffectiveLevel())) {
                this.forcedLog(FQCN, Level.INFO, message, null);
            }
        }
    }
}
