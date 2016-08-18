package org.leech.transprot.netty;

import com.google.common.base.Charsets;
import org.leech.transport.http.HttpChannel;
import org.leech.transport.http.HttpResponse;
import org.leech.transport.http.HttpStatus;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.util.Map;

public class NettyHttpChannel extends HttpChannel {

	private org.jboss.netty.handler.codec.http.HttpRequest nettyRequest;
	private Channel channel;

	public NettyHttpChannel(NettyHttpRequest request, Channel channel) {
		super(request);
		this.nettyRequest = request.nettyRequest();
		this.channel = channel;
	}

	@Override
	public void sendResponse(HttpResponse response) {
	     // Decide whether to close the connection or not.
        boolean http10 = nettyRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0);
        boolean close = HttpHeaders.Values.CLOSE
                .equalsIgnoreCase(nettyRequest.headers().get(
                        HttpHeaders.Names.CONNECTION))
                || (http10 && !HttpHeaders.Values.KEEP_ALIVE
                        .equalsIgnoreCase(nettyRequest.headers().get(
                                HttpHeaders.Names.CONNECTION)));

        // Build the response object.
        org.jboss.netty.handler.codec.http.HttpResponseStatus status = getStatus(response.status());
        org.jboss.netty.handler.codec.http.HttpResponse resp;
        if (http10) {
            resp = new DefaultHttpResponse(HttpVersion.HTTP_1_0, status);
            if (!close) {
                resp.headers().add(HttpHeaders.Names.CONNECTION, "Keep-Alive");
            }
        } else {
            resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        }

        String opaque = nettyRequest.headers().get("X-Opaque-Id");
        if (opaque != null) {
            resp.headers().add("X-Opaque-Id", opaque);
        }

        // Add all custom headers
        Map<String, String> customHeaders = response.headers();
        if (customHeaders != null) {
            for (Map.Entry<String, String> headerEntry : customHeaders
                    .entrySet()) {
                resp.headers()
                        .add(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        ChannelBuffer buffer;
        try {
            byte[] bytes = response.content()
            		.toJson(request.params().containsKey("pretty"))
            		.getBytes(Charsets.UTF_8);
            buffer = ChannelBuffers.wrappedBuffer(bytes);

            resp.setContent(buffer);

            // If our response doesn't specify a content-type header, set one
            if (!resp.headers().contains(HttpHeaders.Names.CONTENT_TYPE)) {
                resp.headers().add(HttpHeaders.Names.CONTENT_TYPE,
                        response.contentType());
            }

            // If our response has no content-length, calculate and set one
            if (!resp.headers().contains(HttpHeaders.Names.CONTENT_LENGTH)) {
                resp.headers().add(HttpHeaders.Names.CONTENT_LENGTH,
                        String.valueOf(buffer.readableBytes()));
            }

            ChannelFuture future = channel.write(resp);
            if (close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } finally {

        }
	}
	
    private org.jboss.netty.handler.codec.http.HttpResponseStatus getStatus(HttpStatus status) {
        switch (status) {
        case ok:
            return org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
        case internal_server_error:
            return org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
        default:
            return org.jboss.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
