package org.leech.fetch;

import com.google.inject.Inject;
import org.leech.common.component.AbstractLifecyleComponent;
import org.leech.common.http.HttpClient;
import org.leech.parse.ParseService;
import org.leech.settings.Settings;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchService extends AbstractLifecyleComponent {

    private final FetcherGroup fetcherGroup;

    @Inject
    public FetchService(Settings settings, HttpClient httpClient, ParseService parseService) {
        super(settings);

        //TODO... add settings
        this.fetcherGroup = new FetcherGroup(settings, httpClient, parseService);
    }

    public void submit(FetchTask task) {
        fetcherGroup.submit(task);
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
