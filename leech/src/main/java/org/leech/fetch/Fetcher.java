package org.leech.fetch;

import org.leech.common.http.HttpCallback;
import org.leech.common.http.HttpClient;
import org.leech.common.http.HttpRequest;
import org.leech.common.http.HttpResponse;
import org.leech.parse.ParseService;
import org.leech.parse.ParseTask;
import org.leech.settings.Settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public class Fetcher {

    private final HttpClient httpClient;
    private final ParseService parseService;
    private ExecutorService executor;
    private final static ThreadFactory fetcherThreadFactory = new ThreadFactory() {
        AtomicInteger fetchIdx = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(
                    new ThreadGroup("fetchers"),
                    r,
                    "fetcher#" + fetchIdx.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    };

    public Fetcher(Settings settings, HttpClient httpClient, ParseService parseService) {
        this.httpClient = httpClient;
        this.parseService = parseService;
    }

    public void submit(FetchTask task) {
        this.executor.submit(new RunnableFetchTask(task, httpClient));
    }

    public void start() {
        this.executor = Executors.newFixedThreadPool(1, fetcherThreadFactory);
    }

    public void shutdown() {
        this.executor.shutdownNow();
        boolean terminated = false;
        try {
            terminated = this.executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        if (!terminated) {
            throw new FetcherException("Failed to shutdown fetcher.");
        }
    }

    /**
     * @author Loster on 2016/8/16.
     */
    public class RunnableFetchTask implements Runnable {

        private final FetchTask task;
        private final HttpClient httpClient;

        public RunnableFetchTask(FetchTask task, HttpClient httpClient) {
            this.task = task;
            this.httpClient = httpClient;
        }

        @Override
        public void run() {
            HttpRequest request = createRequest(task);
            httpClient.get(request, new HttpCallback() {

                @Override
                public void onCompleted(HttpResponse response) {
                    parseService.submit(new ParseTask(response));
                }

                @Override
                public void onFailed(Exception ex) {

                }

                @Override
                public void onCancelled() {

                }
            });
        }

        private HttpRequest createRequest(FetchTask task) {
            return HttpRequest.create(task.url());
        }
    }
}
