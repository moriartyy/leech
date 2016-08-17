package org.leech.task;

/**
 * @author Loster on 2016/8/17.
 */
public class SameThreadTaskExecutor implements TaskExecutor {

    @Override
    public void execute(Task task) {
        task.run();
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown() {

    }
}
