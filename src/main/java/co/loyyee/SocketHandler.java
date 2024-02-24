package co.loyyee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;


/**
 * Each client socket will spawn a Thread,
 * therefore we will implement {@link Runnable} interface for multithreading purpose.
 */
public class SocketHandler implements Runnable {
	private Socket socket;
	private Map<String, Map<String, Handler>> handlers;

	public SocketHandler(Socket socket, Map<String, Map<String, Handler>> handlers) {
		this.socket = socket;
		this.handlers = handlers;
	}

	public void respond(int statusCode, String message, OutputStream out) throws IOException {
		String responseMsg = "HTTP/1.1 " + statusCode + " " + message + "\r\n\r\n";
		Log.write(responseMsg, true);
		out.write(responseMsg.getBytes());
	}

	/**
	 * As mentioned before all HTTP Server handle 2 things
	 * <p>1. incoming Request with {@link BufferedReader} + {@link java.io.InputStreamReader} + {@link Socket#getInputStream()}</p>
	 * <p>2. outgoing Response</p>
	 * <p>
	 * run() is a must implemented method from {@link Runnable#run()}
	 * other methods are not recommended to implement.
	 */
	@Override
	public void run() {
		BufferedReader in = null;
		OutputStream out = null;

		try {
			socket.setSoTimeout(10000);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = socket.getOutputStream();

			boolean done = false;

			while (!done) {
				Request request = new Request(in);
				try {
					if (!request.parse()) {
						respond(500, "Server Error: unable to parse.", out);
						return;
					}
				} catch (SocketTimeoutException e) {
					break;
				}
				if ("close".equalsIgnoreCase(request.getHeader("connection"))) {
					done = true;
				}

				/** finding the method from methodHandlers Map<String, Handler>*/
				boolean foundHandler = false;
				Response response = new Response(out);
				Map<String, Handler> methodsHandler = handlers.get(request.getMethod());
				if (methodsHandler == null) {
					respond(405, "Method not supported", out);
					return;
				}

				for (String handlerPath : methodsHandler.keySet()) {
					if (handlerPath.equals(request.getPath())) {
						methodsHandler.get(request.getPath()).handle(request, response); // TODO: we can split it up, similar to what Express does.
						response.send();
						foundHandler = true;
						break;
					}
				}
				if (!foundHandler) {
					if (methodsHandler.get("/*") != null) {

						/** an over simplified version. TODO: explore production grade HTTP Server */
						methodsHandler.get("/*").handle(request, response);
						response.send();
					} else {
						respond(404, "Not Found.", out);
					}
				}
				request.getHeader("connection");
				response.send();
			}
//			socket.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			Log.save(true);
		}

	}
}
