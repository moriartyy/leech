package org.leech.common.http;

import com.google.inject.Inject;
import org.leech.common.component.AbstractLifecyleComponent;
import org.leech.settings.Settings;

/**
 * @author Loster on 2016/8/16.
 */
public class HttpClient extends AbstractLifecyleComponent {

    @Inject
    public HttpClient(Settings settings) {
        super(settings);
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    public HttpResponse get(HttpRequest request) {
        return null;
    }

    public void get(HttpRequest request, HttpCallback callback) {

    }

    public HttpResponse post(HttpRequest request) {
        return null;
    }

    public void post(HttpRequest request, HttpCallback callback) {

    }
}
