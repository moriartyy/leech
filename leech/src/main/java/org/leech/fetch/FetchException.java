package org.leech.fetch;

import org.leech.LeechException;

/**
 * @author Loster on 2016/8/16.
 */
public class FetchException extends LeechException {

    public FetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public FetchException(String message) {
        super(message);
    }
}
