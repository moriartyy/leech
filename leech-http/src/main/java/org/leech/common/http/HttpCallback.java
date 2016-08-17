package org.leech.common.http;

/**
 * @author Loster on 2016/8/16.
 */
public interface HttpCallback {
    void onCompleted(HttpResponse response);
    void onFailed(Exception ex);
    void onCancelled();
}
