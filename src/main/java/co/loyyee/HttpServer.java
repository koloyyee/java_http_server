package co.loyyee;

import co.loyyee.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import static co.loyyee.enums.HeaderKey.ContentType;
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
 * <h4> Step 5.</h4>
 * {@link FileHandler} - FileHandler will handle all different request with responding html file.
 * */
public class HttpServer{
	/**
	 * This is a Map Method(String) with Map Path(String) with associated Method Handler({@link Handler}).
	 * GET, POST, HEAD
	 * */
	private Map<String, Map<String, Handler> > handlers;
	final private int port;
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
		try (ServerSocket socket = new ServerSocket(this.port)) {
			Log.info("Listing on port: " + this.port);
			Socket client;
			/* keep the connection alive */
			while((client = socket.accept())!= null) {
				Log.write("Received connection from " + client.getRemoteSocketAddress().toString(), true);
				SocketHandler handler = new SocketHandler(client, handlers);
				Thread t = new Thread(handler);
				t.start();
			}
		} catch (SocketTimeoutException e) {
			Log.write(e.getMessage(), true);
		}

	}

	public void addHandler(HttpMethod method, String path, Handler handler) {
		Map<String, Handler> methodHandlers = handlers.computeIfAbsent(method.name(), k -> new HashMap<>());
		methodHandlers.put(path, handler);
		// old way
//		Map<String, Handler> methodHandlers =  handlers.get(method.name());
//		if (methodHandlers == null) {
//			methodHandlers = new HashMap<>();
//			handlers.put(method.name(), methodHandlers);
//		}
//		methodHandlers.put(path, handler);
	}
	public static void main(String[] args) {
		HttpServer server = new HttpServer(8080);
		try{

			server.addHandler(GET, "/hello", ((request, response) -> {
						String html = "It works, " + request.getParameter("name");
						response.setResponseCode(200, "OK");
						response.addHeader(ContentType, "text/html");
						response.addBody(html);
					})
			);
			server.addHandler(GET, "/*" , new FileHandler());

			server.addHandler(POST, "/login", ((request, response) -> {
				StringBuilder stringBuilder = new StringBuilder();
				BufferedReader bufR = new BufferedReader(new InputStreamReader(request.getBody()));
				String line;
				while( (line = bufR.readLine()) != null){
					stringBuilder.append(line);
				}
				String[] components = stringBuilder.toString().split("&");
				Map<String, String> urlParam = new HashMap<>();
				for(String part : components) {
					String[] pieces = part.split("=")	;
					urlParam.put(pieces[0], pieces[1]);
				}
				String html = "<body>Welcome, " + urlParam.get("username") + "</body>";
				response.setResponseCode(200, "OK");
				response.addHeader(ContentType, "text/html");
				response.addBody(html);
			}));

			server.start();
		} catch	(IOException e ) {
			Log.write(e.getMessage(), true);
		} finally {
			System.out.println("Server shutting down.");
		}
	}
}
