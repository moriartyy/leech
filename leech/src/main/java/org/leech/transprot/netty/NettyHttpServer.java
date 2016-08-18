package org.leech.transprot.netty;

import org.anaconda.common.settings.Settings;
import org.anaconda.components.Lifecycle;
import org.anaconda.web.http.HttpController;
import org.anaconda.web.http.HttpServer;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class NettyHttpServer extends HttpServer {
	
    private Channel channel;
    private ServerBootstrap serverBootstrap;
    private int port;
    private boolean isCompressionEnabled;
    private int compressionLevel;

	protected NettyHttpServer(Settings settings, HttpController httpController) {
		super(settings, httpController);

		port = settings.getAsInt("http.port", 8080);
		isCompressionEnabled = settings.getAsBoolean("http.compression", false);
		compressionLevel = settings.getAsInt("http.compression.level", 6);
	}
	
	@Override
	protected void doStart() {
        try {
            ChannelFactory factory = new NioServerSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());
            serverBootstrap = new ServerBootstrap(factory);
            serverBootstrap.setPipelineFactory(new NettyHttpChannelPipelineFactory(this));
            serverBootstrap.setOption("child.tcpNoDelay", true);
            serverBootstrap.setOption("child.keepAlive", false);
        	channel = serverBootstrap.bind(new InetSocketAddress(port));
        	if (logger.isDebugEnabled()) {
        		this.logger.debug("Bind to [" + port + "]");
        	}
        } catch (Exception e) {
            throw new RuntimeException("Failed to bind to [" + port + "]", e);
        }
	}

	@Override
	protected void doStop() {
        if (channel != null) {
        	channel.close().awaitUninterruptibly();
        	channel = null;
        }

        if (serverBootstrap != null) {
            serverBootstrap.releaseExternalResources();
            serverBootstrap = null;
        }
	}

	@Override
	protected void doClose() {
		
	}
	
    static class NettyHttpChannelPipelineFactory implements ChannelPipelineFactory {

        private NettyHttpServer server;

        NettyHttpChannelPipelineFactory(NettyHttpServer server) {
            this.server = server;
        }
        
        @Override
        public ChannelPipeline getPipeline() throws Exception {
            ChannelPipeline pipeline = Channels.pipeline();
            pipeline.addLast("decoder", new HttpRequestDecoder(4 * 1024, 8 * 1024, 8 * 1024));
            if (server.isCompressionEnabled){
                pipeline.addLast("decoder_compress", new HttpContentDecompressor());   
            }
            pipeline.addLast("aggregator", new HttpChunkAggregator(100 * 1024 * 1024));
            pipeline.addLast("encoder", new HttpResponseEncoder());
            if (server.isCompressionEnabled){
                pipeline.addLast("encoder_compress", new HttpContentCompressor(server.compressionLevel));
            }
            pipeline.addLast("handler", new NettyHttpHandler(server));
            return pipeline;
        }
        
    }

	public boolean isRunning() {
		return this.lifecycle.state() == Lifecycle.State.Started;
	}
}
