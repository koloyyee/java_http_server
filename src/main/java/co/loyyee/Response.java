package co.loyyee;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Response {
	private OutputStream out;
	private int statusCode;
	private String statusMessage;
	private Map<String, String> headers;
	private String body;

	public Response(OutputStream out) {
		this.headers = new HashMap<>();
		this.out = out;
	}

	public void setResponseCode(int statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}

	public void addHeader(String headerKey, String headerValue) {
		this.headers.put(headerKey, headerValue);
	}

	public void addBody(String body) {
		this.headers.put("Content-length", Integer.toString(body.length()));
		this.body = body;
	}

	public void send() throws IOException {
		this.headers.put("Connect", "Close");
		/**  "\r\n" is the CR-LF */
		out.write(("HTTP/1.1 " + this.statusCode + " " + this.statusMessage + "\r\n").getBytes());
		for ( String headerName : headers.keySet()) {
			out.write(( headerName  + ": " + this.headers.get(headerName) + "\r\n" ).getBytes());
		}
		out.write("\r\n".getBytes());
		if (body != null) {
			out.write(body.getBytes());
		}
	}
}
