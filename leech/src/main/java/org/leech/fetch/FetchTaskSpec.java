package org.leech.fetch;

import org.leech.common.task.Task;

import java.net.URL;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchTaskSpec {

    private final URL url;

    public FetchTaskSpec(URL url) {
        this.url = url;
    }

    public URL url() {
        return url;
    }
}
