package org.leech.bootstrap;

/**
 * @author Loster on 2016/8/16.
 */
public class ShutdownHooks {

    public static void add(Runnable runnable) {
        add(new Thread(runnable));
    }

    public static void add(Thread thread) {
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
