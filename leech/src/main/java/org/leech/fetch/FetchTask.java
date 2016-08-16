package org.leech.fetch;

import java.net.URL;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchTask {

    private final URL url;

    public FetchTask(URL url) {
        this.url = url;
    }

    public URL url() {
        return url;
    }
}
