package org.leech.fetch;

import org.leech.common.http.HttpCallback;
import org.leech.common.http.HttpClient;
import org.leech.common.http.HttpRequest;
import org.leech.common.http.HttpResponse;
import org.leech.common.task.Task;
import org.leech.parse.ParseService;
import org.leech.parse.ParseTask;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchTask implements Task {

    private ParseService parseService;
    private final FetchTaskSpec task;
    private final HttpClient httpClient;

    public FetchTask(FetchTaskSpec task, HttpClient httpClient, ParseService parseService) {
        this.parseService = parseService;
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

    private HttpRequest createRequest(FetchTaskSpec task) {
        return HttpRequest.create(task.url());
    }
}
