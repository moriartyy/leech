package org.leech.fetch;

import org.leech.LeechException;
import org.leech.common.http.HttpClient;
import org.leech.common.task.TaskExecutor;
import org.leech.common.task.TaskExecutorGroup;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public class FetcherGroup extends TaskExecutorGroup<FetchTask> {

    private final Settings settings;

    public FetcherGroup(Settings settings) {
        super(settings.getInt("leach.fetcher.numbers", Runtime.getRuntime().availableProcessors()));
        this.settings = settings;
    }

    @Override
    protected TaskExecutor createFetcher() {
        return new Fetcher(settings);
    }
}
