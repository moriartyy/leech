package org.leech.fetch;

import com.google.inject.Inject;
import org.leech.common.component.AbstractLifecyleComponent;
import org.leech.common.http.HttpClient;
import org.leech.common.task.TaskExecutor;
import org.leech.common.task.TaskExecutorGroup;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchService extends AbstractLifecyleComponent {

    private final TaskExecutorGroup<FetchTask> fetcherGroup;
    private final HttpClient httpClient;
    private final ParseService parseService;

    @Inject
    public FetchService(final Settings settings, HttpClient httpClient, ParseService parseService) {
        super(settings);

        this.httpClient = httpClient;
        this.parseService = parseService;

        final int fetcherNumber = settings.getInt("leach.fetcher.numbers", Runtime.getRuntime().availableProcessors());

        //TODO... add settings
        this.fetcherGroup = new TaskExecutorGroup<FetchTask>(fetcherNumber) {
            @Override
            protected TaskExecutor createFetcher() {
                return new Fetcher(settings);
            }
        };
    }

    public void submit(FetchTaskSpec task) {
        fetcherGroup.submit(new FetchTask(task, httpClient, parseService));
    }

    @Override
    protected void doStart() {
        fetcherGroup.start();
    }

    @Override
    protected void doStop() {
        fetcherGroup.shutdown();
    }
}
