package org.leech.task;

import org.leech.fetch.FetchException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Loster on 2016/8/16.
 */
public class SingleThreadTaskExecutor implements TaskExecutor {

    private final ThreadFactory threadFactory;
    private ExecutorService executor;


    public SingleThreadTaskExecutor(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public void execute(Task task) {
        executor.execute(task);
    }

    @Override
    public void start() {
        this.executor = Executors.newSingleThreadExecutor(threadFactory);
    }

    @Override
    public void shutdown() {
        this.executor.shutdownNow();
        boolean terminated = false;
        try {
            terminated = this.executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        if (!terminated) {
            throw new FetchException("Failed to shutdown fetcher.");
        }
    }
}