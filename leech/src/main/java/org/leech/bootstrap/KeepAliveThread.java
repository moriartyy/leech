package org.leech.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Loster on 2016/8/16.
 */
public class KeepAliveThread {

    private CountDownLatch keepAliveLatch;
    private final Logger logger = LoggerFactory.getLogger(KeepAliveThread.class);
    private boolean addShutdownHook;
    private CountDownLatch startLatch;
    private volatile int state;
    private final static int STARTING = 1;
    private final static int STATED = 2;
    private final static int STOPPED = 0;

    private KeepAliveThread(boolean addShutdownHook) {
        this.addShutdownHook = addShutdownHook;
    }

    public static KeepAliveThread start(boolean addShutdownHook) {
        KeepAliveThread thread = new KeepAliveThread(addShutdownHook);
        thread.start();
        return thread;
    }

    public void start() {

        final int state = this.state;
        if (state == STARTING || state == STATED)
            return;

        this.state = STARTING;

        keepAliveLatch = new CountDownLatch(1);

        if (addShutdownHook) {
            ShutdownHooks.add(new Runnable() {
                @Override
                public void run() {
                    keepAliveLatch.countDown();
                }
            });
        }

        startLatch = new CountDownLatch(1);

        startThread();

        boolean started = false;
        try {
            started = startLatch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        if (!started) {
            keepAliveLatch.countDown(); // in case thread started later.
            throw new RuntimeException("Thread failed to started in 1s.");
        }
    }

    private void startThread() {
        Thread keepAlive = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.debug("Keep alive thread started.");
                state = STATED;
                startLatch.countDown();
                try {
                    keepAliveLatch.await();
                } catch (InterruptedException ignored) {
                }
                logger.debug("Keep alive thread exited.");
            }
        });
        keepAlive.setName("keep-alive");
        keepAlive.setDaemon(false);
        keepAlive.start();
    }

    public void stop() {

        if (state == STOPPED)
            return;

        this.state = STOPPED;

        keepAliveLatch.countDown();
    }
}
