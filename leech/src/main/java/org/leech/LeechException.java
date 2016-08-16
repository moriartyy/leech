package org.leech;

/**
 * @author Loster on 2016/8/16.
 */
public class LeechException extends RuntimeException {

    public LeechException(String message, Throwable cause) {
        super(message, cause);
    }

    public LeechException(String message) {
        super(message);
    }
}
