package org.leech.common.task;

import org.leech.LeechException;
import org.leech.common.http.HttpClient;
import org.leech.fetch.FetchTask;
import org.leech.fetch.Fetcher;
import org.leech.fetch.FetcherGroup;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public class TaskExecutorGroup<T extends TaskExecutor> {

    private final TaskExecutor[] executors;
    private final AtomicInteger idx = new AtomicInteger();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public TaskExecutorGroup(int nExecutors) {
        this.executors = new TaskExecutor[nExecutors];
        for (int i=0; i<nExecutors; i++) {
            this.executors[i] = createFetcher();
        }
    }

    private abstract TaskExecutor createFetcher(HttpClient httpClient, ParseService parseService) {
        return new Fetcher(settings, httpClient, parseService);
    }

    public void submit(FetchTask task) {
        nextFetcher().submit(task);
    }

    private Fetcher nextFetcher() {
        return fetchers[Math.abs(idx.getAndIncrement() % fetchers.length)];
    }

    public void start() {
        for (int i=0;i<fetchers.length; i++) {
            try {
                fetchers[i].start();
            } catch (Exception e) {
                for(int j=0; j<i; j++) {
                    try {
                        fetchers[j].shutdown();
                    } catch (Exception shutdownError) {
                        logger.error("Failed to shutdown fetcher", shutdownError);
                    }
                }
                throw new LeechException("Failed to start fetcher, cause: " +  e.getMessage(), e);
            }
        }
    }

    public void shutdown() {
        for (int i=0;i<fetchers.length; i++) {
            try {
                fetchers[i].shutdown();
            } catch (Exception shutdownError) {
                logger.error("Failed to shutdown fetcher", shutdownError);
            }
        }
    }
}
