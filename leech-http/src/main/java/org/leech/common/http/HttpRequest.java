package org.leech.common.http;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Loster on 2016/8/16.
 */
public class HttpRequest {

    private URL url;

    public HttpRequest(URL url) {
        this.url = url;
    }

    public HttpRequest(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    public static HttpRequest create(URL url) {
        return new HttpRequest(url);
    }

    public URL url() {
        return url;
    }
}
