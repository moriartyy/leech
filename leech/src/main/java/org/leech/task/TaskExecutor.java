package org.leech.task;

/**
 * @author Loster on 2016/8/16.
 */
public interface TaskExecutor {

    void execute(Task task);

    void start();

    void shutdown();
}
