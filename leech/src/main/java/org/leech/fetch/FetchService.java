package org.leech.fetch;

import com.google.inject.Inject;
import org.leech.common.component.AbstractLifecycleComponent;
import org.leech.common.http.*;
import org.leech.task.SameThreadTaskExecutor;
import org.leech.task.Task;
import org.leech.task.TaskExecutor;
import org.leech.task.TaskExecutorGroup;
import org.leech.extract.ExtractService;
import org.leech.settings.Settings;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchService extends AbstractLifecycleComponent {

    private final TaskExecutorGroup executors;
    private final HttpClient httpClient;
    private final ExtractService extractService;

    @Inject
    public FetchService(final Settings settings, ExtractService extractService) {
        this.httpClient = createHttpClient(settings);
        this.extractService = extractService;

        this.executors = new TaskExecutorGroup(1) {
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

    public void fetch(final FetchRequest request, final FetchHandler handler) {
        final FetchContext context = new FetchContext(request, handler);
        executors.execute(new Task() {
            @Override
            protected void doRun() {
                asyncFetch(context);
            }
        });
    }

    private void asyncFetch(final FetchContext context) {
        HttpRequest httpRequest = HttpRequest.create(context.url());
        httpClient.get(httpRequest, new HttpCallback() {

            @Override
            public void onCompleted(HttpResponse response) {
                if (response.status() != HttpStatus.OK) {
                    handleFetchFailed(context);
                } else {
                    context.complete(new FetchResult(response));
                }
            }

            @Override
            public void onFailed(Exception ex) {
                handleFetchFailed(context);
            }

            @Override
            public void onCancelled() {
                handleFetchFailed(context);
            }
        });
    }

    private void handleFetchFailed(FetchContext context) {
        final FetchRequest request = context.request();
        if (request.retries() < request.maxRetries()) {
            request.increaseRetries();
            asyncFetch(context);
        } else {
            // TODO how to handle failed fetch task.
        }
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
