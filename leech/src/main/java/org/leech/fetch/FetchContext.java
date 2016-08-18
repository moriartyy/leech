package org.leech.fetch;

import java.net.URI;
import java.net.URL;

/**
 * @author Loster on 2016/8/18.
 */
public class FetchContext {


    public FetchContext(FetchRequest request, FetchHandler handler) {

    }

    public URI uri() {
        return null;
    }

    public FetchRequest request() {
        return null;
    }

    public URL url() {
        return null;
    }

    public FetchHandler handler() {
        return null;
    }

    public void complete(FetchResult fetchResult) {

    }
}
