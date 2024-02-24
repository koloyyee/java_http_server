package co.loyyee;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;


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
	private Map<String, String> headers = new HashMap<>();
	private Map<String, String> queryParam = new HashMap<>();
	private BufferedReader in;
	private static Logger log = Logger.getLogger("co.loyyee.request");


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
		log.info(initialLine);

		/**
		 * Parsing the HTTP Request by tokenization
		 * StringTokenizer constructor will handle: " \t\n\r\f"
		 * The header will contain <em> CR-LF </em> with "\r\n"
		 * {@link StringTokenizer(String str)}
		 * **/
		/** */
		StringTokenizer token = new StringTokenizer(initialLine);
		/** why 3?
		 * GET <- 1
		 * /hello.html <- 2
		 * HTTP/1.1 <- 33
		 * */
		String[] components = new String[3];
		for (int i = 0; i < components.length; i++) {
			if (token.hasMoreTokens()) {
				components[i] = token.nextToken();
			} else {
				return false;
			}
		}
			/** e.g.: GET POST */
			this.method = components[0];
			/** URI */
			this.fullUrl = components[1];

			/** With unknown size for the HTTP Request we will use while loop*/
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
			}

			/** handle query
			 * e.g.:
			 * <p>POST /test.html?query=alibaba HTTP/1.1</p>
			 * */
			if (components[1].indexOf("?") == -1) {
				/** case when no query parameter */
				this.path = components[1];
			} else {
				/** case when with query parameter */
				/** we are slicing the URI by the ? */
				this.path = components[1].substring(0, components[1].indexOf("?"));
				parseQueryParam(components[1].substring(components[1].indexOf("?") + 1));
			}
			/** Home page */
			if ("/".equals(this.path)) {
				this.path = "index.html";
			}
		return true;
	}

	/**
	 *
	 * <p>POST /test.html?query=alibaba HTTP/1.1</p>
	 * The full query parameter will be query=alibaba
	 * when there are more than 1 will there will be connect by "&"
	 * **/
	public void parseQueryParam(String queryParam) {

		/**
		 * We will not know how many query a user will have
		 * first we will split by "&" then loop through them
		 * e.g.:
		 *  first_name=David&last_name=Ko&age=30
		 * */
		for(String param : queryParam.split("&")) {
			/** first_name=David **/
			/** why use indexOf + substring instead of split because we can save memory */
			int delimiterIdx = param.indexOf("=");
			/** queryParam Key = name Value = David **/
			if(delimiterIdx != -1) {
				this.queryParam.put(param.substring(0, delimiterIdx), param.substring(delimiterIdx+1));
			} else {
				this.queryParam.put(param, null); // TODO: Should be null or ""?
			}
		}
	}
}
