package co.loyyee;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class Response {
	private OutputStream out;
	private int statusCode;
	private String statusMessage;
	/** expecting to handle multiple cookies values **/
	private Map<String, List<String>> headers;
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
		List<String> headerValues = this.headers.get(headerKey);
		if(headerValues == null)	 {
			headerValues = new ArrayList<>();
			this.headers.put(headerKey, headerValues);
		}
		headerValues.add(headerValue);
	}

	public void addBody(String body) {
		addHeader("Content-Length", Integer.toString(body.length()));
		this.body = body;
	}
//	public void addCookie(Cookie cookie) {
//		addHeader("Set-Cookie", cookie.toString());
//	}

	public void send() throws IOException {
		addHeader("Connect", "Close");
		/**  "\r\n" is the CR-LF */
		out.write(("HTTP/1.1 " + this.statusCode + " " + this.statusMessage + "\r\n").getBytes());
		for ( String headerName : headers.keySet()) {
			Iterator<String > headerValues = headers.get(headerName).iterator();
			while(headerValues.hasNext()) {
				out.write((headerName + ": " + this.headers.get(headerName) + "\r\n").getBytes());
			}
		}
		out.write("\r\n".getBytes());
		if (body != null) {
			out.write(body.getBytes());
		}
	}
}
