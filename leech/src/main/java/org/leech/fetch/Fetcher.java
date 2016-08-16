package org.leech.fetch;

import org.leech.common.http.HttpClient;
import org.leech.common.task.TaskExecutor;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public class Fetcher implements TaskExecutor<FetchTask> {

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

    public Fetcher(Settings settings) {
    }

    @Override
    public void submit(FetchTask task) {
        this.executor.submit(task);
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

}
