package org.leech.common.task;

import org.leech.LeechException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public abstract class TaskExecutorGroup<T extends Task> {

    private final TaskExecutor[] executors;
    private final AtomicInteger idx = new AtomicInteger();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public TaskExecutorGroup(int nExecutors) {
        this.executors = new TaskExecutor[nExecutors];
        for (int i=0; i<nExecutors; i++) {
            this.executors[i] = createFetcher();
        }
    }

    protected abstract TaskExecutor createFetcher();

    public void submit(T task) {
        nextExecutor().submit(task);
    }

    private TaskExecutor nextExecutor() {
        return executors[Math.abs(idx.getAndIncrement() % executors.length)];
    }

    public void start() {
        for (int i=0;i<executors.length; i++) {
            try {
                executors[i].start();
            } catch (Exception e) {
                for(int j=0; j<i; j++) {
                    try {
                        executors[j].shutdown();
                    } catch (Exception shutdownError) {
                        logger.error("Failed to shutdown fetcher", shutdownError);
                    }
                }
                throw new LeechException("Failed to start fetcher, cause: " +  e.getMessage(), e);
            }
        }
    }

    public void shutdown() {
        for (int i=0;i<executors.length; i++) {
            try {
                executors[i].shutdown();
            } catch (Exception shutdownError) {
                logger.error("Failed to shutdown fetcher", shutdownError);
            }
        }
    }
}
