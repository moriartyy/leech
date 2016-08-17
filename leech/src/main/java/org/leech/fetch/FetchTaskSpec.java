package org.leech.fetch;

import org.leech.common.task.TaskSpec;

import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchTaskSpec extends TaskSpec {

    private final URL url;
    private transient AtomicInteger retries = new AtomicInteger();
    private int maxRetries;

    public FetchTaskSpec(URL url, int maxRetries) {
        this.url = url;
        this.maxRetries = maxRetries;
    }

    public int maxRetries() {
        return maxRetries;
    }

    public void maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public URL url() {
        return url;
    }

    public int retries() {
        return this.retries.get();
    }

    public void increaseRetries() {
        this.retries.incrementAndGet();
    }
}
