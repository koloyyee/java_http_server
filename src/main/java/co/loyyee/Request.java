package co.loyyee;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Step 2:
 * Request take care of the HTTP requests
 * To learn more about HTTP Request Message
 *
 * @link <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages#http_requests">Mozilla Http Messages</a>
 * <p>
 * e.g.:
 * GET /hello.htm HTTP/1.1
 * User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)
 * Host: www.example.com
 * Accept-Language: en-us
 * Accept-Encoding: gzip, deflate
 * Connection: Keep-Alive
 */
public class Request {
	private String method;
	private String path;
	private String fullUrl;
	final private Map<String, String> headers = new HashMap<>();
	final private Map<String, String> queryParam = new HashMap<>();
	final private Map<String, String> cookies = new HashMap<>();
	final private BufferedReader in;

	public Request(BufferedReader in) {
		this.in = in;
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public String getHeader(String headerName) {
		return headers.get(headerName);
	}

	public String getParameter(String paramName) {
		return queryParam.get(paramName);
	}

	public String getCookie(String cookieName) {
		return cookies.get(cookieName);
	}

	/**
	 * Handling Post request and read the body
	 */
	public InputStream getBody() throws IOException {
		return new HttpInputStream(in, headers);
	}

	/**
	 * Parsing the HTTP Request
	 * e.g:
	 * GET /hello.html HTTP/1.1
	 * User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)
	 * Host: www.example.com
	 * Accept-Language: en-us
	 * Accept-Encoding: gzip, deflate
	 * Connection: Keep-Alive
	 **/
	public boolean parse() throws IOException {
		String initialLine = in.readLine();
		Log.write(initialLine, true);

		/*
		 * Parsing the HTTP Request by tokenization
		 * StringTokenizer constructor will handle: " \t\n\r\f"
		 * The header will contain <em> CR-LF </em> with "\r\n"
		 * {@link StringTokenizer(String str)}
		 * **/
		StringTokenizer token = new StringTokenizer(initialLine);
		/* why 3?
		 * GET <- 1
		 * /hello.html <- 2
		 * HTTP/1.1 <- 3
		 * */
		String[] components = new String[3];
		for (int i = 0; i < components.length; i++) {
			if (token.hasMoreTokens()) {
				components[i] = token.nextToken();
			} else {
				return false;
			}
		}
		/* e.g.: GET POST */
		this.method = components[0];
		/* URI e.g.: /hello.html */
		this.fullUrl = components[1];

		/* With unknown size for the HTTP Request we will use while loop*/
		while (true) {
			String headerLine = in.readLine();
			System.out.println(headerLine);
			if (headerLine.isEmpty()) {
				break;
			}
			int delimiterIdx = headerLine.indexOf(":");
			if (delimiterIdx == -1) {
				return false;
			}
			headers.put(headerLine.substring(0, delimiterIdx), headerLine.substring(delimiterIdx + 1));

			/* parsing Cookie values */
			String name = headerLine.substring(0, delimiterIdx);
			String value = headerLine.substring(delimiterIdx + 2);
			headers.put(name, value);
			if ("Cookie".equals(name)) {
				parseCookie(value);
			}
		}

		/*
		 * handle query
		 * e.g.:
		 * <p>POST /test.html?query=alibaba HTTP/1.1</p>
		 * */
		if (!components[1].contains("?")) {
			/* case when no query parameter */
			this.path = components[1];
		} else {
			/* case when with query parameter */
			/* we are slicing the URI by the ? */
			this.path = components[1].substring(0, components[1].indexOf("?"));
			parseQueryParam(components[1].substring(components[1].indexOf("?") + 1));
		}
		/* Home page */
		if ("/".equals(this.path)) {
			this.path = "/index.html";
		}
		return true;
	}

	/**
	 * <p>POST /test.html?query=alibaba HTTP/1.1</p>
	 * The full query parameter will be query=alibaba
	 * when there are more than 1 will there will be connect by "&"
	 **/
	public void parseQueryParam(String queryParam) {

		/**
		 * We will not know how many query a user will have
		 * first we will split by "&" then loop through them
		 * e.g.:
		 *  first_name=David&last_name=Ko&age=30
		 * */
		for (String param : queryParam.split("&")) {
			/* first_name=David **/
			/* why use indexOf + substring instead of split because we can save memory */
			int delimiterIdx = param.indexOf("=");
			/* queryParam Key = name Value = David **/
			if (delimiterIdx != -1) {
				this.queryParam.put(param.substring(0, delimiterIdx), param.substring(delimiterIdx + 1));
			} else {
				this.queryParam.put(param, null); // TODO: Should be null or ""?
			}
		}
	}

	public void parseCookie(String cookieString) {
		String[] cookiePairs = cookieString.split("; ");
		for (int i = 0; i < cookiePairs.length; i++) {
			System.out.println(cookiePairs[i].length());
			String[] cookieValue = cookiePairs[i].split("=");
			cookies.put(cookieValue[0], cookieValue[1]);
		}
	}

}
