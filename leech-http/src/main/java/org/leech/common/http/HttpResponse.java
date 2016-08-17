package org.leech.common.http;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author Loster on 2016/8/16.
 */
public class HttpResponse {

    private int status;
    private String encoding;
    private ByteBuffer content;
    private Map<String, String> headers;

    public int status() {
        return this.status;
    }

    public Map<String, String> headers() {
        return this.headers;
    }

    public String contentEncoding() {
        return encoding;
    }

    public ByteBuffer content() {
        return content;
    }

    public String contentString() {
        try {
            return new String(content.array(), content.position(), content.limit(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error happened while transform response content to string.", e);
        }
    }
}
