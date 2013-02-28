package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * A Thread that will be run on a client request on the class {@link server.FTPServer}
 * 
 * @author Francois Lepan, Benjamin Van Ryseghem
 */
public class FTPServerThread extends Thread {
	
	private String name  = "plop";
	
	private String password = "plop";
	
	
	/** The buffer on which the client will ask the server */
	private BufferedReader clientMessage;
	
	/** The buffer on which the server will answer the client */
	private BufferedWriter serverMessage;
	
	/** A boolean that will be set to false only of the client said QUIT */
	private boolean isClientRequesting;
	
	/** The socket on which the client is connected */
	private Socket socket;
	
	/**
	 * Initialise the socket on which the client is connected
	 * 
	 * @param socket the socket on which the client is connected
	 */
	public FTPServerThread(Socket socket) {
		this.socket = socket;
		this.isClientRequesting = true;
	}
	
	/**
	 * Read the request of the client and process it
	 */
	public void run() {
		
		try {
			
			this.clientMessage = new BufferedReader (new InputStreamReader (socket.getInputStream()));
			this.serverMessage = new BufferedWriter (new OutputStreamWriter (socket.getOutputStream()));
			
			this.writeMessageFromServer("220 login required\n");
			
			while (isClientRequesting) {
				this.processRequest(this.clientMessage.readLine());
			}
			
			this.closeConnection();
			
		} catch (SocketException se) {
			System.err.println("FTPServerThread: Unable to close the connection from the socket: "+this.socket);
		} catch (IOException e) {
			System.err.println("FTPServerThread: Unable to read or write from the socket: "+this.socket);
		}
	}
	
	
	/**
	 * Process the request of the client
	 * 
	 * @param s the request of the client
	 */
	private void processRequest(String s) throws IOException, SocketException {
		
		if (s == null || s.isEmpty()) {
			// if connection fail
			this.processERROR("");
			System.exit(0);
		}
		
		String[] cmd = s.split(" ");
		
		if  (cmd[0].equals("USER")) 	 	{ processUSER(cmd); } 
		else if  (cmd[0].equals("PASS")) 	{ processPASS(cmd); } 
		else if  (cmd[0].equals("RETR")) 	{ processRETR(cmd); } 
		else if  (cmd[0].equals("STOR")) 	{ processSTOR(cmd); } 
		else if  (cmd[0].equals("LIST")) 	{ processLIST(cmd); } 
		else if  (cmd[0].equals("QUIT")) 	{ processQUIT(cmd); } 
		else 								{ processERROR(cmd[0]); }
	}
	
	/**
	 * Process the USER request.
	 * 
	 * @param s the request of the client
	 */
	private void processUSER (String[] s) throws IOException {
		System.out.println(this.printRequest(s));
		
		if (s[1].equals(this.name)) {
			this.writeMessageFromServer("331 password required\n");
		} else {
			this.writeMessageFromServer("430  the username: "+s[1]+" does not exist\n");
		}
	}
	
	
	/**
	 * Process the PASS request.
	 * 
	 * @param s the request of the client
	 */
	private void processPASS (String[] s) throws IOException {
		System.out.println(this.printRequest(s));
		
		if (s[1].equals(this.password)) {
			this.writeMessageFromServer("230  hello "+this.name +"\n");
			//this.writeMessageFromServer("150 plop \n");
			
		} else {
			this.writeMessageFromServer("430  the password is not correct\n");
		}
	}
	
	
	/**
	 * Process the RETR request.
	 * 
	 * @param s the request of the client
	 */
	private void processRETR (String[] s) throws IOException {
		System.out.println(this.printRequest(s));
		
		
	}
	
	/**
	 * Process the STOR request.
	 * 
	 * @param s the request of the client
	 */
	private void processSTOR (String[] s) throws IOException {
		System.out.println(this.printRequest(s));
	}
	
	/**
	 * Process the LIST request.
	 * 
	 * @param s the request of the client
	 */
	private void processLIST (String[] s) throws IOException {
		System.out.println(this.printRequest(s));
	}
	
	/**
	 * Process the QUIT request.
	 * 
	 * @param s the request of the client
	 */
	private void processQUIT (String[] s) throws IOException {
		this.isClientRequesting = false;
		System.out.println("processQUIT: "+this.printRequest(s));
	}
	
	/**
	 * Process the ERROR request.
	 * 
	 * @param s the request of the client
	 */
	private void processERROR (String s) throws IOException {
		this.writeMessageFromServer("502 command not implemented: "+s);
	}
	
	/**
	 * Close the connection of the socket
	 */
	private void closeConnection() throws IOException, SocketException {
			this.writeMessageFromServer("BYE BYE\n");
			this.isClientRequesting = false;
			this.socket.close();
	}
	
	private void writeMessageFromServer(String message) throws IOException {
		this.serverMessage.write(message);
		this.serverMessage.flush();
	}
	
	/**
	 * Get a string representation of the request.
	 * 
	 * @param tab the request of the client
	 */
	private String printRequest(String[] tab) {
		String s = "";
		
		for (int i = 0; i < tab.length; i++) {
			s += tab[i] + " ";
		}
		
		return s;
	}
//	
//	private void print(char[] buffer){
//		for (int i = 0; i < buffer.length; i++) {
//			System.out.print(buffer[i]);
//		}
//		System.out.println();
//	}
}
