import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;

public class ConnectionHandler extends Thread {
	// conn - socket representing TCP/IP connection to Client
	// is - get data from client on this input stream
	// os - can send data back to the client on this output stream
	// br - use buffered reader to read client data
	private Socket conn;
	private InputStream is;
	private OutputStream os;
	BufferedReader br;
	private String directory;
	private SynchronizedCounter synCounter;
	private final int MAX_CONNECTION = 50;

	public ConnectionHandler(Socket conn, String directory, SynchronizedCounter synCounter) {
		this.directory = directory;
		this.conn = conn;
		this.synCounter = synCounter;
		try {
			// get data from client on this input stream
			is = conn.getInputStream();
			// to send data back to the client on this stream
			os = conn.getOutputStream();
			// use buffered reader to read client data
			br = new BufferedReader(new InputStreamReader(is));
		} catch (IOException ioe) {
			System.out.println("ConnectionHandler: " + ioe.getMessage());
		}
	}

	public void run() {
		// run method is invoked when the Thread's start method (ch.start(); in
		// Server class) is invoked
		System.out.println("new ConnectionHandler thread started .... ");
		try {
			handleRequest();
		} catch (Exception e) {
			// exit cleanly for any Exception (including IOException,
			// ClientDisconnectedException)
			System.out.println("ConnectionHandler:run " + e.getMessage());
		}
	}

	private void handleRequest() throws IOException {
		int connCounter = synCounter.getCounter();
		while (connCounter <= MAX_CONNECTION) {
			// get data from client over socket
			String line = br.readLine();
			// assuming no exception, print out line received from client
			System.out.println("\nConnectionHandler: " + line);
			String filename = line.split(" ")[1];
			if (line.split(" ")[0].contains("GET")) {
				// GET
				byte[] response = getResponseText(directory, filename);
				os.write(response);
			} else if (line.split(" ")[0].contains("HEAD")) {
				// HEAD
				byte[] response = getHeader(directory, filename);
				os.write(response);
			} else {
				// Not Implemented
				byte[] response = getNotImplementedResponse();
				os.write(response);
			}
			cleanup();
		}
	}

	private byte[] getResponseText(String directory, String filename) throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream outStream = null;
		String path = directory + filename;
		String response = "";
		byte[] content = null;
		try {
			// test file not found
			BufferedReader in = new BufferedReader(new FileReader(path));
			in.close();
			content = getContent(path);
			response += "HTTP/1.1 200 OK\r\n";
			response += "Server: Simple Java Http\r\n";
			if (filename.contains(".jpg")) {
				response += "Content-Type: image/jpeg\r\n";
			} else {
				response += "Content-Type: text/html\r\n";
			}
			response += "Content-Length: " + content.length + "\r\n\r\n";
			System.out.println(response);
			outStream = new ByteArrayOutputStream();
			outStream.write(response.getBytes("UTF-8"));
			outStream.write(content);

		} catch (Exception e) {
			content = getContent(path);
			response += "HTTP/1.1 404 Not Found\r\n";
			response += "Server: Simple Java Http\r\n";
			response += "Content-Type: text/html\r\n";
			response += "Content-Length: " + content.length + "\r\n\r\n";
			System.out.println(response);
			outStream = new ByteArrayOutputStream();
			outStream.write(response.getBytes("UTF-8"));
			outStream.write(content);
		}
		return outStream.toByteArray();
	}

	private byte[] getContent(String path) {
		String content = "";
		try {
			if (path.contains(".html")) {
				BufferedReader in = new BufferedReader(new FileReader(path));
				String str;
				while ((str = in.readLine()) != null) {
					content += str+ "\r\n";
				}
				in.close();
			} else if (path.contains(".jpg")) {
				File f = new File(path);
				BufferedImage o = ImageIO.read(f);
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				ImageIO.write(o, "jpg", b);
				byte[] img = b.toByteArray();
				return img;
			}
		} catch (IOException e) {
			content += "<h1>404 Not Found</h1>";
		}

		return content.getBytes();
	}

	private byte[] getHeader(String directory, String filename) throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream outStream = null;
		String path = directory + filename;
		String response = "";
		byte[] content = null;
		try {
			// test file not found
			BufferedReader in = new BufferedReader(new FileReader(path));
			in.close();
			content = getContent(path);
			response += "HTTP/1.1 200 OK\r\n";
			response += "Server: Simple Java Http\r\n";
			if (filename.contains(".jpg")) {
				response += "Content-Type: image/jpeg\r\n";
			} else {
				response += "Content-Type: text/html\r\n";
			}
			response += "Content-Length: " + content.length + "\r\n\r\n";
			System.out.println(response);
			outStream = new ByteArrayOutputStream();
			outStream.write(response.getBytes("UTF-8"));

		} catch (IOException e) {
			content = getContent(path);
			response += "HTTP/1.1 404 Not Found\r\n";
			response += "Server: Simple Java Http\r\n";
			response += "Content-Type: text/html\r\n";
			response += "Content-Length: " + content.length + "\r\n\r\n";
			System.out.println(response);
			outStream = new ByteArrayOutputStream();
			outStream.write(response.getBytes("UTF-8"));
			outStream.write(content);
		}
		return outStream.toByteArray();
	}

	private byte[] getNotImplementedResponse() throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream outStream = null;
		String content = "";
		String response = "";
		content += "<h1>501 Not Implemented</h1>";
		response += "HTTP/1.1 501 Not Implemented\r\n";
		response += "Server: Simple Java Http\r\n";
		response += "Content-Type: text/html\r\n";
		response += "Content-Length: " + content.length() + "\r\n\r\n";
		outStream = new ByteArrayOutputStream();
		outStream.write(response.getBytes("UTF-8"));
		outStream.write(content.getBytes());
		return outStream.toByteArray();
	}

	private void cleanup() {
		System.out.println("ConnectionHandler: ... cleaning up and exiting ... ");
		try {
			br.close();
			is.close();
			conn.close();
			synCounter.decrement();
		} catch (IOException ioe) {
			System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
		}
	}
}