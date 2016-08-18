package org.leech.fetch;

import com.google.inject.Inject;
import org.leech.WebResource;
import org.leech.WebResourceHandler;
import org.leech.common.component.AbstractLifecycleComponent;
import org.leech.common.http.*;
import org.leech.task.SameThreadTaskExecutor;
import org.leech.task.Task;
import org.leech.task.TaskExecutor;
import org.leech.task.TaskExecutorGroup;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchService extends AbstractLifecycleComponent {

    private final TaskExecutorGroup fetcherGroup;
    private final HttpClient httpClient;
    private final ParseService parseService;

    @Inject
    public FetchService(final Settings settings, ParseService parseService) {
        this.httpClient = createHttpClient(settings);
        this.parseService = parseService;

        this.fetcherGroup = new TaskExecutorGroup(1) {
            @Override
            protected TaskExecutor createExecutor() {
                return new SameThreadTaskExecutor();
            }
        };
    }

    private HttpClient createHttpClient(Settings settings) {
        final int ioThreads = settings.getInt("leach.fetcher.io_threads", Runtime.getRuntime().availableProcessors());
        return null;
    }

    public void fetch(final WebResource webResource) {
        final WebResource finalWebResource = webResource;
        fetcherGroup.execute(new Task() {
            @Override
            protected void doRun() {
                WebResourceHandler handler = webResource.handler();
                handler.fetch();
            }
        });
    }

    private void asyncFetch(final WebResource webResource) {
        HttpRequest request = HttpRequest.create(webResource.url());

        httpClient.get(request, new HttpCallback() {

            @Override
            public void onCompleted(HttpResponse response) {
                if (response.status() != HttpStatus.OK) {
                    handleFetchFailed(webResource);
                } else {
                    parseService.submit(new ParseTask(webResource));
                }
            }

            @Override
            public void onFailed(Exception ex) {
                handleFetchFailed(webResource);
            }

            @Override
            public void onCancelled() {
                handleFetchFailed(webResource);
            }
        });
    }

    private void handleFetchFailed(WebResource webResource) {
        if (webResource.retries() < webResource.maxRetries()) {
            webResource.increaseRetries();
            asyncFetch(webResource);
        } else {
            // TODO how to handle failed fetch task.
        }
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
