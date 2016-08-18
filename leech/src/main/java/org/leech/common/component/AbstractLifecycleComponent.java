package org.leech.common.component;

import org.leech.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Loster on 2016/8/16.
 */
public abstract class AbstractLifecycleComponent implements LifecycleComponent {

    protected final Logger logger;
    private final String componentName;

    protected AbstractLifecycleComponent() {
        logger = LoggerFactory.getLogger(getClass());
        this.componentName = getClass().getName();
    }

    @Override
    public void start() {
        logger.debug("Starting {}...", this.componentName);
        doStart();
        logger.debug("{} started.");
    }

    protected abstract void doStart();

    @Override
    public void stop() {
        logger.debug("Stopping {}...", this.componentName);
        doStop();
        logger.debug("{} stopped.");
    }

    protected abstract void doStop();
}
