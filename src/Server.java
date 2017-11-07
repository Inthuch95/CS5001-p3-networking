import java.io.IOException;
import java.net.*;

public class Server {
	// listen for client connection requests on this server socket
	private ServerSocket ss;
	private SynchronizedCounter synCounter = new SynchronizedCounter();
	private final int MAX_CONNECTION = 50;

	public Server(String directory, int port) {
		try {
			ss = new ServerSocket(port);
			System.out.println("Server started ... listening on port " + port + " ...");
			int connCounter = synCounter.getCounter();
			while (connCounter < MAX_CONNECTION) {
				Socket conn = ss.accept();
				synCounter.increment();
				System.out.println("Server got new connection request from " + conn.getInetAddress());
				System.out.println("Number of connection " + synCounter);
				// create new handler for this connection
				ConnectionHandler ch = new ConnectionHandler(conn, directory, synCounter);
				// start handler thread
				ch.start();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Ooops " + ioe.getMessage());
		}
	}
}