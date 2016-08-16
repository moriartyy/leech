package org.leech.bootstrap;

import org.leech.node.Node;
import org.leech.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Loster on 2016/8/16.
 */
public class Bootstrap {

    final static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        logger.debug("Initiate boot sequence.");

        final Node node;
        try {
            node = NodeBuilder.newBuilder().build();
            node.start();
        } catch (Exception e) {
            logger.error("Failed to build or start node.", e);
            return;
        }

        final KeepAliveThread thread = KeepAliveThread.start(false);

        ShutdownHooks.add(new Runnable() {
            @Override
            public void run() {
                node.stop();
                thread.stop();
            }
        });

        logger.debug("Boot sequence finished, application started.");
    }
}
