package org.leech.fetch;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/18.
 */
public class FetchRequest {

    private transient AtomicInteger retries = new AtomicInteger();
    private int maxRetries;

    public URI uri() {
        return null;
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
