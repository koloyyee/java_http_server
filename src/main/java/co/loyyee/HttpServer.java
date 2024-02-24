package co.loyyee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <h3>================Introduction=======</h3>
 * HTTP Server is what connects the server to the client, it is the cornerstone of the web,
 * As a developer I have a duty to learn how to create a server from scratch.
 *
 * <h3>================Goal================</h3>
 *
 * Here is the project to understand how to use Socket, create Request and Response
 * and how to parse the HTTP protocol.
 *
 * <h3>================Project Breakdown===========</h3>
 * <h4> Step 1.</h4>
 * {@link HttpServer} <p> Create a socket and listen to the HTTP Request </p>
 *
 * <h4> Step 2.</h4>
 * {@link Request} - Handling income HTTP Request with BufferedReader
 *
 * <h4> Step 3.</h4>
 * {@link Response} - Handling outgoing Http Response with PrintWriter
 *
 * <h4> Step 4.</h4>
 * {@link SocketHandler} - Setup handler for income and outgoing message
 *
 *
 * */
public class HttpServer{
	private static Logger log = Logger.getLogger("co.loyyee.server");
	/**
	 * This is a Map Method(String) with Map Path(String) with associated Method Handler({@link Handler}).
	 * GET, POST, HEAD
	 * */
	private Map<String, Map<String, Handler> > handlers;
	private int port;
	public HttpServer() {
		this.port = 8888;
	}

	public HttpServer(int port) {
		this.port = port;
		this.handlers = new HashMap<>();
	}

	/**
	 * The start() method is kickoff a Socket to listen to income requests.
	 * we will be using java core library Logger, to log out the event.
	 * */
	public void start() throws IOException {
		ServerSocket socket = new ServerSocket(this.port);
		log.info("Listening on port: " + this.port);
		Socket client;
		/** keep the connection alive */
		while((client = socket.accept())!= null) {
			log.info("Received connection from " + client.getRemoteSocketAddress().toString());
			SocketHandler handler = new SocketHandler(client, handlers);
			Thread t = new Thread(handler);
			t.start();
		}
	}

	public void addHandler(String method, String path, Handler handler) {
		Map<String, Handler> methodHandlers =  handlers.get(method);
		if (methodHandlers == null) {
			methodHandlers = new HashMap<>();
			handlers.put(method, methodHandlers);
		}
		methodHandlers.put(path, handler);
	}
	public static void main(String[] args) {
		HttpServer server = new HttpServer(8080);
		try{

			server.addHandler("GET", "/hello", new Handler() {
				@Override
				public void handle(Request request, Response response) throws IOException {
					String html = "It works, " + request.getParameter("name") + "";
					response.setResponseCode(200, "OK");
					response.addHeader("Content-Type", "text/html");
					response.addBody(html);
				}
			});
			server.addHandler("GET", "/*" , new FileHandler());
			server.start();
		} catch	(IOException e ) {
			System.out.println(e.getMessage());
		} finally {
			System.out.println("Server shutting down.");
		}
	}
}
