package org.leech.common.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Loster on 2016/8/17.
 */
public class ThreadFactoryFactory {

    public static ThreadFactory threadFactory(final String threadGroup, final String threadName) {
        return new ThreadFactory() {
            AtomicInteger fetchIdx = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(
                        new ThreadGroup(threadGroup),
                        r,
                        threadName + "#" + fetchIdx.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        };
    }
}
