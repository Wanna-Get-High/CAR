package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * The main class of the FTP server.
 * 
 * Will create a socket on a port and wait for clients requests.
 * 
 * @author Francois Lepan, Benjamin Van Ryseghem
 */
public class FTPServer {
	
	/** The socket that will be initialized to accept client requests */
	ServerSocket server;
	
	/** The port on which the socket will be connected to */
	int port;

	public FTPServer(int port) {
		this.port = port;
	}
	
	/**
	 * Create a new FTPServerThread that will process the request of the client.
	 */
	public void processRequest () {
		try {
			new FTPServerThread(this.server.accept()).start();
		} catch (IOException e) {
			System.err.println("FTPServer: Unable to accept the connection with the ServerSocket: "+this.server);
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the server socket on the port passed in parameter of the constructor
	 */
	public void initSocket() {
		try {
			this.server = new ServerSocket(this.port);
		} catch (IOException ioe) { 
			System.err.println("FTPServer: Unable to connect to the port: "+this.port+" with ServerSocket");
		}
	}
	
	/**
	 * Initialize the server socket on the port passed in parameter of the constructor and <b>
	 * process request of clients that want to connect to it with the class {@link server.FTPServerThread}
	 */
	public void run() {
		this.initSocket();
		while (true) {
			this.processRequest();
		}
	}
	
	
	public static void main(String[] args) {
		new FTPServer(2121).run();
	}
}

