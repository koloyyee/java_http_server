package co.loyyee;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
 *
 * 1. Create a socket, listen to port
 *
 * */
public class HttpServer{
	private static Logger log = Logger.getLogger("co.loyye.server");
	private int port;
	public HttpServer() {
		this.port = 8888;
	}

	public HttpServer(int port) {
		this.port = port;
	}

	/**
	 * The start() method is kickoff a Socket to listen to income requests.
	 * we will be using java core library Logger, to log out the event.
	 * */
	public void start() throws IOException {
		ServerSocket socket = new ServerSocket(this.port);
		log.fine("everything is fine with " + this.port );

		Socket client;
		/** keep the connection alive */
		while((client = socket.accept())!= null) {
			log.info("Received connection from " + client.getRemoteSocketAddress().toString());
			SocketHandler handler = new SocketHandler(client, handlers);
			Thread t = new Thread(handler);
			t.start();
		}

	}
	public static void main(String[] args) {
		HttpServer s = new HttpServer();
		try {
			s.start();
		} catch (IOException e) {
			log.warning(e.getMessage());
		}
	}
}
