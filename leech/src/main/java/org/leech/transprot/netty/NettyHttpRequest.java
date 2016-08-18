package org.leech.transprot.netty;

import org.anaconda.web.http.HttpRequest;
import org.jboss.netty.channel.Channel;

public class NettyHttpRequest extends HttpRequest {

	private org.jboss.netty.handler.codec.http.HttpRequest nettyRequest;

	public NettyHttpRequest(org.jboss.netty.handler.codec.http.HttpRequest request, Channel channel) {
		this.nettyRequest = request;
	}

	public org.jboss.netty.handler.codec.http.HttpRequest nettyRequest() {
		return nettyRequest;
	}

}
