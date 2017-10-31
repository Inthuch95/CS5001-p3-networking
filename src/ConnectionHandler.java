

import java.io.*;
import java.net.Socket;

public class ConnectionHandler extends Thread {

	private Socket conn;       // socket representing TCP/IP connection to Client
	private InputStream is;    // get data from client on this input stream	
	private OutputStream os;   // can send data back to the client on this output stream
	BufferedReader br;         // use buffered reader to read client data
	private String directory;

	public ConnectionHandler(Socket conn, String directory) {
		this.directory = directory;
		this.conn = conn;
		try{
			is = conn.getInputStream();     // get data from client on this input stream
			os = conn.getOutputStream();  // to send data back to the client on this stream
			br = new BufferedReader(new InputStreamReader(is)); // use buffered reader to read client data
		} catch (IOException ioe){
			System.out.println("ConnectionHandler: " + ioe.getMessage());
		}
	}

	public void run() { // run method is invoked when the Thread's start method (ch.start(); in Server class) is invoked  
		System.out.println("new ConnectionHandler thread started .... ");
		try {
			printRequest();
		} catch  (Exception e){ // exit cleanly for any Exception (including IOException, ClientDisconnectedException)
			System.out.println("ConnectionHandler:run " + e.getMessage());
			cleanup();     // cleanup and exit
		}
	}


	private void printRequest() throws IOException {
		while(true) {
			String line = br.readLine(); // get data from client over socket
			System.out.println("ConnectionHandler: " + line); // assuming no exception, print out line received from client
			String header = "";
			String filename = line.split(" ")[1];
			//GET
			if (line.split(" ")[0].contains("GET")) {
				byte[] response = getResponseText(directory, filename);
				os.write(response);
			} else if (line.split(" ")[0].contains("HEAD")) {
				byte[] response = getHeader(directory, filename);
				os.write(response);
			}
			conn.close();
		}
	}
	
	private byte[] getResponseText(String directory, String filename) {
		ByteArrayOutputStream outStream = null;
		String response = "";
		byte[] content = null;
		try {
			String path = directory + filename;
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
			
		}
		return outStream.toByteArray();
	}
	
	private byte[] getContent(String path) {
		String content = "";
		System.out.println(path);
		if (path.contains(".html")) {
			try {
		        BufferedReader in = new BufferedReader(new FileReader(path));
		        String str;
		        while ((str = in.readLine()) != null) {
		            content +=str;
		        }
		        in.close();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		return content.getBytes();
	}
	
	private byte[] getHeader(String directory, String filename) {
		ByteArrayOutputStream outStream = null;
		String response = "";
		byte[] content = null;
		try {
			String path = directory + filename;
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
			
		}
		return outStream.toByteArray();
	}

	private void cleanup(){
		System.out.println("ConnectionHandler: ... cleaning up and exiting ... " );
		try{
			br.close();
			is.close();
			conn.close();
		} catch (IOException ioe){
			System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
		}
	}



}