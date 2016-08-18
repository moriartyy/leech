package org.leech;

import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/17.
 */
public class WebResource {

    private URL url;
    private transient AtomicInteger retries = new AtomicInteger();
    private int maxRetries;
    private WebResourceHandler handler;

    public WebResource(URL url) {
        this.url = url;
    }

    public WebResourceHandler handler() {
        return this.handler;
    }

    public URL url() {
        return url;
    }

    public int maxRetries() {
        return maxRetries;
    }

    public void maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int retries() {
        return this.retries.get();
    }

    public void increaseRetries() {
        this.retries.incrementAndGet();
    }
}
