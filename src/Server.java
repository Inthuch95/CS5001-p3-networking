import java.io.IOException;
import java.net.*;

public class Server {
	// listen for client connection requests on this server socket
	private ServerSocket ss;

	public Server(String directory, int port) {
		try {
			ss = new ServerSocket(port);
			System.out.println("Server started ... listening on port " + port + " ...");
			while (true) {
				Socket conn = ss.accept();
				System.out.println("Server got new connection request from " + conn.getInetAddress());
				// create new handler for this connection
				ConnectionHandler ch = new ConnectionHandler(conn, directory);
				// start handler thread
				ch.start();
			}
		} catch (IOException ioe) {
			System.out.println("Ooops " + ioe.getMessage());
		}
	}
}