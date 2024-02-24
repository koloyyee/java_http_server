package co.loyyee;

import co.loyyee.enums.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static co.loyyee.enums.HttpMethod.*;

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
		Log.info("Listing on port: " + this.port);
		Socket client;
		/* keep the connection alive */
		while((client = socket.accept())!= null) {
			Log.write("Received connection from " + client.getRemoteSocketAddress().toString(), true);
			SocketHandler handler = new SocketHandler(client, handlers);
			Thread t = new Thread(handler);
			t.start();
		}
	}

	public void addHandler(HttpMethod method, String path, Handler handler) {
		Map<String, Handler> methodHandlers =  handlers.get(method.name());
		if (methodHandlers == null) {
			methodHandlers = new HashMap<>();
			handlers.put(method.name(), methodHandlers);
		}
		methodHandlers.put(path, handler);
	}
	public static void main(String[] args) {
		HttpServer server = new HttpServer(8080);
		try{

			server.addHandler(GET, "/hello", ((request, response) -> {
						String html = "It works, " + request.getParameter("name") + "";
						response.setResponseCode(200, "OK");
						response.addHeader("Content-Type", "text/html");
						response.addBody(html);
					})
			);
			server.addHandler(GET, "/*" , new FileHandler());
			server.addHandler(POST, "/login", new Handler() {
				public void handle(Request request, Response response) throws IOException {
					StringBuffer buf = new StringBuffer();
					InputStream in = request.getBody();
					int c;
					while ((c = in.read()) != -1) {
						buf.append((char) c);
					}
					String[] components = buf.toString().split("&");
					Map<String, String> urlParameters = new HashMap<String, String>();
					for (String component : components) {
						String[] pieces = component.split("=");
						urlParameters.put(pieces[0], pieces[1]);
					}
					String html = "<body>Welcome, " + urlParameters.get("username") + "</body>";

					response.setResponseCode(200, "OK");
					response.addHeader("Content-Type", "text/html");
					response.addBody(html);
				}
			});

			server.start();
		} catch	(IOException e ) {
			Log.write(e.getMessage(), true);
		} finally {
			System.out.println("Server shutting down.");
		}
	}
}
