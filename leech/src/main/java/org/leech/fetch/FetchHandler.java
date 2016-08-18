package org.leech.fetch;

/**
 * @author Loster on 2016/8/18.
 */
public interface FetchHandler {
    void complete(FetchResult result);
    void caught(Throwable error);
}
