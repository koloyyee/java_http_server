package co.loyyee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;


/**
 * Each socket client will spawn a Thread,
 * therefore we will implement {@link Runnable} interface for multithreading purpose.
 * */
public class SocketHandler implements Runnable {
	private Socket client;
	private Map<String, Map<String, Handler>> handlers;
	public SocketHandler(Socket client, Map<String, Map<String, Handler>> handlers) {
		this.client = client;
		this.handlers = handlers;
	}

	public void respond(int statusCode, String message, OutputStream out) throws IOException {
		String responseMsg = "HTTP/1.1 " + statusCode + " " + message + "\r\n\r\n";
		System.out.println(responseMsg );
		out.write(responseMsg.getBytes());
	}

	/**
	 * As mentioned before all HTTP Server handle 2 things
	 * <p>1. incoming Request with {@link BufferedReader} + {@link java.io.InputStreamReader} + {@link Socket#getInputStream()}</p>
	 * <p>2. outgoing Response</p>
	 *
	 * run() is a must implemented method from {@link Runnable#run()}
	 * other methods are not recommended to implement.
	 * */
	@Override
	public void run() {
		BufferedReader in = null;
		OutputStream out = null;

		try {
			in = new BufferedReader(new InputStreamReader( client.getInputStream()));
			out = client.getOutputStream();

			Request request = new Request(in);
			if(!request.parse()){
				respond(500, "Server Error: unable to parse.", out);
				return;
			}

			/** finding the method from methodHandlers Map<String, Handler>*/
			boolean foundHandler = false;
			Response response = new Response(out);
			Map<String, Handler> methodsHandler = handlers.get(request.getMethod());
			if(methodsHandler == null) {
				respond(405, "Method not supported", out);
				return;
			}

//			boolean foundPath = false;
			for(String handlerPath: methodsHandler.keySet()){
				if(handlerPath.equals(request.getPath())){
					methodsHandler.get(request.getPath()).handle(request, response); // TODO: we can split it up, similar to what Express does.
					response.send();
					foundHandler = true;
//					foundPath = true;
					break;
				}
			}
//			if (!foundPath ) {
//				respond(404, "Not Found.", out);
//				response.send();
//				return;
//			}
			if(!foundHandler) {
				if (methodsHandler.get("/*") != null) {

					/** an over simplified version. TODO: explore production grade HTTP Server */
					methodsHandler.get("/*").handle(request, response);
					response.send();
				} else {
					respond(404, "Not Found.", out);
				}
			}
		} catch ( IOException e){
			System.out.println(e.getMessage());
		}
	}
}
