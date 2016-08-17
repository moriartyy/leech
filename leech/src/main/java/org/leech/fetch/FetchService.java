package org.leech.fetch;

import com.google.inject.Inject;
import org.leech.common.component.AbstractLifecyleComponent;
import org.leech.task.SameThreadTaskExecutor;
import org.leech.task.Task;
import org.leech.task.TaskExecutor;
import org.leech.task.TaskExecutorGroup;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchService extends AbstractLifecyleComponent {

    private final TaskExecutorGroup fetcherGroup;
    private final FetchTask fetcher;

    @Inject
    public FetchService(final Settings settings, ParseService parseService) {
        super(settings);

        final int fetcherNumber = settings.getInt("leach.fetcher.numbers", Runtime.getRuntime().availableProcessors());

        this.fetcher = new FetchTask(settings, parseService);

        this.fetcherGroup = new TaskExecutorGroup(fetcherNumber) {
            @Override
            protected TaskExecutor createExecutor() {
                return new SameThreadTaskExecutor();
            }
        };
    }

    public void submit(final FetchTaskSpec taskSpec) {
        fetcherGroup.submit(new Task() {
            @Override
            public void run() {
                fetcher.asyncFetch(taskSpec);
            }
        });
    }

    @Override
    protected void doStart() {
        fetcher.start();
        fetcherGroup.start();
    }

    @Override
    protected void doStop() {
        fetcher.stop();
        fetcherGroup.shutdown();
    }
}
