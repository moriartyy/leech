package org.leech;

/**
 * @author Loster on 2016/8/18.
 */
public interface WebResourceHandler {

    void fetch(WebResourceHandlerContext context);
    void extract(WebResourceHandlerContext context);
    void store(WebResourceHandlerContext context);
}
