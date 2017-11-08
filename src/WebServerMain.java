
public class WebServerMain {

	public static void main(String[] args) {
		checkArgs(args);
		String directory = args[0];
		int port = Integer.parseInt(args[1]);
		new Server(directory, port);
	}

	private static void checkArgs(String[] args) {
		// check for no arguments
		if (args.length == 0) {
			System.out.println("Usage: java WebServerMain <document_root> <port>");
			System.exit(0);
		}
	}

}
