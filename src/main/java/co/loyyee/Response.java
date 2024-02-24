package co.loyyee;

import co.loyyee.enums.HeaderKey;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static co.loyyee.enums.HeaderKey.*;

public class Response {
final	private OutputStream out;
private int statusCode;
	private String statusMessage;
	/** expecting to handle multiple cookies values **/
	final private Map<String, List<String>> headers;
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
		List<String> headerValues = this.headers.computeIfAbsent(headerKey, k -> new ArrayList<>());
		headerValues.add(headerValue);
	}
	public void addHeader(HeaderKey headerKey, String headerValue) {
		List<String> headerValues = this.headers.computeIfAbsent(headerKey.value, k -> new ArrayList<>());
		headerValues.add(headerValue);
	}

	public void addBody(String body) {
		addHeader(ContentLength, Integer.toString(body.length()));
		this.body = body;
	}
	public void addCookie(Cookie cookie) {
		addHeader(SetCookie, cookie.toString());
	}

	/**  "\r\n" is the CR-LF */
	public void send() throws IOException {
		addHeader(Connection, "Close");
		String responseMsg = ("HTTP/1.1 " + this.statusCode + " " + this.statusMessage + "\r\n");
		out.write(responseMsg.getBytes());
		Log.write(responseMsg.trim(), true);

		for ( String headerName : headers.keySet()) {
			Iterator<String > headerValues = headers.get(headerName).iterator();
			while(headerValues.hasNext()) {
				out.write((headerName + ": " + headerValues.next() + "\r\n").getBytes());
			}
		}
		out.write("\r\n".getBytes());
		if (body != null) {
			out.write(body.getBytes());
		}
	}
}
