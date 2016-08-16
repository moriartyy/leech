package org.leech.fetch;

import org.leech.LeechException;
import org.leech.common.http.HttpClient;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public class FetcherGroup {

    private final Fetcher[] fetchers;
    private final Settings settings;
    private final AtomicInteger idx = new AtomicInteger();
    private final static Logger logger = LoggerFactory.getLogger(FetcherGroup.class);

    public FetcherGroup(Settings settings, HttpClient httpClient, ParseService parseService) {
        this.settings = settings;
        final int fetcherNumber = settings.getInt("leach.fetcher.numbers", Runtime.getRuntime().availableProcessors());
        this.fetchers = new Fetcher[fetcherNumber];
        for (int i=0; i<fetcherNumber; i++) {
            this.fetchers[i] = createFetcher(httpClient, parseService);
        }
    }

    private Fetcher createFetcher(HttpClient httpClient, ParseService parseService) {
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
