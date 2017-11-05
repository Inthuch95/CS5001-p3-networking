
public class WebServerMain {

	public static void main(String[] args) {
		String directory = args[0];
		int port = Integer.parseInt(args[1]);
		new Server(directory, port);
	}

}
