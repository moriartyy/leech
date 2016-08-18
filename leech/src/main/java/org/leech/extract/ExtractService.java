package org.leech.extract;

import com.google.inject.Inject;
import org.leech.WebResource;
import org.leech.common.component.AbstractLifecycleComponent;
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
public class ExtractService extends AbstractLifecycleComponent {

    private final TaskExecutorGroup executors;
    private final static ThreadFactory parserThreadFactory = ThreadFactoryFactory.threadFactory("parsers", "parser");
    private ExtractorChooser extractorChooser;

    @Inject
    public ExtractService(Settings settings, ExtractorChooser extractorChooser) {
        final int parserNumber = settings.getInt("leach.parser.number", Runtime.getRuntime().availableProcessors());
        this.extractorChooser = extractorChooser;
        this.executors = new TaskExecutorGroup(parserNumber) {
            @Override
            protected TaskExecutor createExecutor() {
                return new SingleThreadTaskExecutor(parserThreadFactory);
            }
        };
    }

    public void extract(ExtractRequest request, ExtractHandler handler) {
        final TaskExecutor executor = executors.nextExecutor();
        final ExtractContext context = new ExtractContext(request, handler, executor);
        final Extractor extractor = extractor();
        executor.execute(new Task() {
            @Override
            protected void doRun() {
                extractor.process(context);
            }
        });
    }

    private Extractor extractor() {
        return extractorChooser.choose();
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
