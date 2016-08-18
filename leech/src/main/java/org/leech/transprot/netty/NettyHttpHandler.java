package org.leech.transprot.netty;

import org.anaconda.common.logging.Logger;
import org.anaconda.common.logging.Loggers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.timeout.ReadTimeoutException;

@ChannelHandler.Sharable
public class NettyHttpHandler extends SimpleChannelHandler {

    private NettyHttpServer server;
    private Logger logger;

    public NettyHttpHandler(NettyHttpServer server) {
        this.server = server;
        this.logger = Loggers.getLogger(this.getClass());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        HttpRequest request = (HttpRequest) e.getMessage();
        NettyHttpRequest nettyRequest = new NettyHttpRequest(request, e.getChannel());
        NettyHttpChannel nettyChannel = new NettyHttpChannel(nettyRequest, e.getChannel());
        server.dispatchRequest(nettyRequest, nettyChannel);
        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        if (e.getCause() instanceof ReadTimeoutException) {
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("Connection timeout [{%s}]", ctx.getChannel().getRemoteAddress().toString()));
            }
            ctx.getChannel().close();
        } else {
            if (!this.server.isRunning()) {
                // ignore
                return;
            }
            
            logger.debug("Caught exception while handling client http traffic, closing connection {}", e.getCause());
            ctx.getChannel().close();
        }
    }
}

