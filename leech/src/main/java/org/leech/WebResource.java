package org.leech;

import java.net.URL;

/**
 * @author Loster on 2016/8/17.
 */
public class WebResource {
    private URL url;

    public WebResource(URL url) {
        this.url = url;
    }

    public URL url() {
        return url;
    }
}
