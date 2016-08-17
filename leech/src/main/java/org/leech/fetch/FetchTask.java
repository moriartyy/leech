package org.leech.fetch;

import org.leech.WebResource;
import org.leech.common.http.*;
import org.leech.task.Task;
import org.leech.parse.ParseService;
import org.leech.parse.ParseTaskSpec;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchTask extends Task {

    private final WebResource webResource;
    private ParseService parseService;
    private HttpClient httpClient;
    private AtomicInteger retries = new AtomicInteger();
    private int maxRetries;

    public FetchTask(WebResource webResource, HttpClient httpClient, ParseService parseService) {
        this.parseService = parseService;
        this.httpClient = httpClient;
        this.webResource = webResource;
    }

    public void asyncFetch() {
        HttpRequest request = HttpRequest.create(webResource.url());

        httpClient.get(request, new HttpCallback() {

            @Override
            public void onCompleted(HttpResponse response) {
                if (response.status() != HttpStatus.OK) {
                    handleFetchFailed();
                } else {
                    parseService.submit(new ParseTask(webResource));
                }
            }

            @Override
            public void onFailed(Exception ex) {
                handleFetchFailed();
            }

            @Override
            public void onCancelled() {
                handleFetchFailed();
            }
        });
    }

    private void handleFetchFailed() {
        if (retries < maxRetries) {
            taskSpec.increaseRetries();
            asyncFetch(taskSpec);
        } else {
            // TODO how to handle failed fetch task.
        }
    }

    @Override
    protected void doRun() {
        asyncFetch();
    }
}
