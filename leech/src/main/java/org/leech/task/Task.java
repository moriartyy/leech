package org.leech.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Loster on 2016/8/17.
 */
public abstract class Task implements Runnable {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void run() {
        try {
            doRun();
        } catch(Exception ignored) {
            //TODO what we should do here?
        }
    }

    protected abstract void doRun();
}
