package org.leech.parse;

import com.google.inject.Inject;
import org.leech.common.component.AbstractLifecyleComponent;
import org.leech.common.concurrent.ThreadFactoryFactory;
import org.leech.task.SingleThreadTaskExecutor;
import org.leech.task.Task;
import org.leech.task.TaskExecutor;
import org.leech.task.TaskExecutorGroup;
import org.leech.settings.Settings;

import java.util.concurrent.ThreadFactory;

/**
 * @author Loster on 2016/8/16.
 */
public class ParseService extends AbstractLifecyleComponent {

    private final TaskExecutorGroup executors;
    private final Parser parser;
    private final static ThreadFactory parserThreadFactory = ThreadFactoryFactory.threadFactory("parsers", "parser");

    @Inject
    public ParseService(final Settings settings) {
        super(settings);
        final int parserNumber = settings.getInt("leach.parser.number", Runtime.getRuntime().availableProcessors());
        this.parser = new Parser(settings);
        this.executors = new TaskExecutorGroup(parserNumber) {
            @Override
            protected TaskExecutor createExecutor() {
                return new SingleThreadTaskExecutor(parserThreadFactory);
            }
        };
    }

    public void submit(final ParseTaskSpec taskSpec) {
        executors.submit(new Task() {
            @Override
            public void run() {
                parser.parse(taskSpec);
            }
        });
    }

    @Override
    protected void doStart() {
        executors.start();
    }

    @Override
    protected void doStop() {
        executors.shutdown();
    }
}
