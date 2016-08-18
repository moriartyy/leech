package org.leech.transport.http;

import org.leech.common.component.AbstractLifecycleComponent;
import org.leech.common.settings.Settings;

public abstract class HttpServer extends AbstractLifecycleComponent {

	private HttpController httpController;

	protected HttpServer(Settings settings, HttpController httpController) {
		this.httpController = httpController;
	}
	
	public void dispatchRequest(HttpRequest request, HttpChannel channel) {
		HttpHandler handler = httpController.getHandler(request);
		if (handler == null) {
			channel.sendResponse(HttpResponse.NoHandlerFoundResponse(request));
		} else {
			handler.handleRequest(request, channel);
		}
	}
}
