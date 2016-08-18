package org.leech.transport.http;

import java.util.HashMap;
import java.util.Map;


public abstract class HttpResponse {
	
	protected Map<String, String> headers = new HashMap<String, String>();
	protected final static String contentType = "application/json; charset=UTF-8";
	protected HttpStatus status;
	
	public HttpStatus status() {
		return status;
	}
	
	public void status(HttpStatus status) {
		this.status = status;
	}

	public Map<String, String> headers() {
		return headers;
	}

	public String contentType() {
		return contentType;
	}

	public abstract Object content();

	public final static HttpResponse NoHandlerFoundResponse(HttpRequest request) {
		return new NoHandlerFoundResponse(request);
	}


	public static class NoHandlerFoundResponse extends HttpResponse {

		private HttpRequest request;
		private Object content;

		protected NoHandlerFoundResponse(HttpRequest request) {
			this.request = request;
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", "error");
			map.put("message", "No handler found for request uri " + request.url());
			this.content = map;
		}

		public HttpRequest request() {
			return request;
		}

		@Override
		public Object content() {
			return content;
		}
	}
}
