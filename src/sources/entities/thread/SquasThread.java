package entities.thread;

/**
 * Represent a thread in aka
 */
public class SquasThread extends Thread {

    private AbstractTask<?> task;

    public SquasThread(AbstractTask task) {
        super(task);
        this.task = task;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (task != null)
            task.cancel();
    }

    public AbstractTask<?> getTask() {
        return task;
    }

    public void setTask(AbstractTask<?> task) {
        this.task = task;
    }
}
