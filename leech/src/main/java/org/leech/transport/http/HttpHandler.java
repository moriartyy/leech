package org.leech.transport.http;

public interface HttpHandler {

	void handleRequest(HttpRequest request, HttpChannel channel);
}
