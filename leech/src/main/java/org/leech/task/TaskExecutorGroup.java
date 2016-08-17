package org.leech.task;

import org.leech.LeechException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public abstract class TaskExecutorGroup {

    private final List<TaskExecutor> executors;
    private final AtomicInteger idx = new AtomicInteger();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected TaskExecutorGroup(int nExecutors) {
        this.executors = new ArrayList<>(nExecutors);
        for (int i=0; i<nExecutors; i++) {
            this.executors.add(createExecutor());
        }
    }

    protected abstract TaskExecutor createExecutor();

    public void submit(Task task) {
        nextExecutor().execute(task);
    }

    private TaskExecutor nextExecutor() {
        return executors.get(Math.abs(idx.getAndIncrement() % executors.size()));
    }

    public void start() {
        for (int i=0;i<executors.size(); i++) {
            try {
                executors.get(i).start();
            } catch (Exception e) {
                for(int j=0; j<i; j++) {
                    try {
                        executors.get(i).shutdown();
                    } catch (Exception shutdownError) {
                        logger.error("Failed to shutdown event executor", shutdownError);
                    }
                }
                throw new LeechException("Failed to start fetcher, cause: " +  e.getMessage(), e);
            }
        }
    }

    public void shutdown() {
        for (TaskExecutor executor : executors) {
            try {
                executor.shutdown();
            } catch (Exception shutdownError) {
                logger.error("Failed to shutdown fetcher", shutdownError);
            }
        }
    }
}
