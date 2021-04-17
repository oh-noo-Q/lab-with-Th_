package entities.thread;

import entities.UILogger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Represent a thread in aka
 */
public class ExecutorService
{
    private AbstractTask task;
    private ThreadPoolExecutor threadPoolExecutor;

    public ExecutorService(int nThreads){
        threadPoolExecutor =   new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }


    public AbstractTask getTask() {
        return task;
    }

    public void setTask(AbstractTask task) {
        this.task = task;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public boolean isTerminated() {
        return threadPoolExecutor == null || threadPoolExecutor.isTerminated();
    }

    public void shutdownNow() {
        if (threadPoolExecutor != null) {
            UILogger.getUiLogger().log("Shutdown " + this);
            threadPoolExecutor.shutdownNow();

            // Wait a while for tasks to respond to being cancelled
            try {
                if (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS))
                    System.err.println("\t\tPool did not terminate. Shutdown again!");

                if (!threadPoolExecutor.isShutdown()){
                    threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

                // (Re-)Cancel if current thread also interrupted
                threadPoolExecutor.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }
}
