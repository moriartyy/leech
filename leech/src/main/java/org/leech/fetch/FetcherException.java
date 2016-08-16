package org.leech.fetch;

import org.leech.LeechException;

/**
 * @author Loster on 2016/8/16.
 */
public class FetcherException extends LeechException {

    public FetcherException(String message, Throwable cause) {
        super(message, cause);
    }

    public FetcherException(String message) {
        super(message);
    }
}
