
import java.io.Console;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * I am the implementation of the remote interface.
 * I am both the RMI server and client.
 * 
 * Code snippets:
 * 
 * 	- instance creation:
 * 		SiteImpl("//localhost/root", null); //create a root node
 * 		SiteImpl("//localhost/son", "//localhost/root"); //create a son node. Its father is "//localhost/root/"
 * 	- main messages:
 * 		node.sendMessage("Hello World");
 * 		node.addFather("//localhost/root");
 * 		node.addSon(node1);
 * 
 * 
 * @author Benjamin Van Ryseghem, François Lepan
 *
 */
public class SiteImpl extends UnicastRemoteObject implements SiteItf {
	
	/** The command string to add a father to this */
	protected final String FATHER_STRING = "addFather";
	
	/** The command string to display the number of fathers of this*/
	protected final String FATHERS_SIZE_STRING = "fathersSize";
	
	/** The command string to display the number of children of this*/
	protected final String CHILDREN_SIZE_STRING = "childrenSize";
	
	/** auto generated serial */
	private static final long serialVersionUID = 9118194714523855257L;
	
	/** The list of fathers */
	private List<SiteItf> fathers;
	
	/** The id used for binding */
	private String id;
	
	/**
	 * List of the current instance children 
	 */
	private List<SiteItf> children;

	/**
	 * Main constructor
	 * @param id the identifier used to create the binding
	 * @param father the binding name of the father node
	 * @throws RemoteException
	 */
	public SiteImpl(String id, String father) throws RemoteException {
		super();
		
		this.id = id;
		this.children = new ArrayList<SiteItf>();
		this.fathers = new ArrayList<SiteItf>();
		
		this.register(id, father);
		this.loop();
	}
	
	/**
	 * Infinite loop. Reading from the stdin, and propagating the messages through the children.
	 * This method handle commands:
	 * 	- addFather: adds a father to the current node
	 *  - childrenSize: displays the number of children
	 *  - fathersSize:  displays the number of fathers
	 *  - exit: exits
	 * @throws RemoteException
	 */
	protected void loop() throws RemoteException{
		while(true){
			String message;
			Console console = System.console();
	        message = console.readLine("Please enter a message or a command\n" +
	        		"\t[addFather host | childrenSize | fathersSize | exit] :\n");
	        if("exit".equals(message))  						exit();
	        else if(message.startsWith(FATHER_STRING))			addFather(message);
	        else if(message.startsWith(FATHERS_SIZE_STRING))	printFathersSize();
	        else if(message.startsWith(CHILDREN_SIZE_STRING))	printChildrenSize();
	        else 												this.sendMessage(message);
		}
	}

	/**
	 * Print the number of fathers
	 */
	protected void printFathersSize(){
		System.out.println("Number of fathers: "+this.fathers.size());
	}
	
	/**
	 * Print the number of children.
	 */
	protected void printChildrenSize(){
		System.out.println("Number of children: "+this.children.size());
	}
	
	/**
	 * Add a father to the node from a command
	 * @param message message is the full command
	 * @throws RemoteException
	 */
	protected void addFather(String message) throws RemoteException{
		
		String father = message.substring(FATHER_STRING.length()+1);
		
		try{
			this.addFather((SiteItf)( Naming.lookup(father) ));
		}
		catch(NotBoundException ex){
			System.out.println("This father is not in the registry table");
		}
		catch(MalformedURLException ex){
			System.out.println("The URL provided as father is not a valid URL");
		}
		System.out.println("Father "+father+ " added");
	}
	
	/**
	 * Exists. First the method add all the fathers of the node to its children. Second remove the current node from its son's fathers list. Third remove the current node to its fathers. Fourth and last, destroy the current node binding.
	 * @throws RemoteException
	 */
	protected void exit() throws RemoteException {
			
		for(SiteItf son : this.children){
			son.addAllFathers(this.fathers);
			son.removeFather(this);
		}
		
		for(SiteItf father : this.fathers){
			father.removeSon(this);		
		}
		
		try {
			Naming.unbind(this.id);
		} 
		catch(NotBoundException ex){
			System.out.println("This id is not in the registry table");
			System.exit(1);
		}
		catch(MalformedURLException ex){
			System.out.println("The URL provided as id is not a valid URL");
			System.exit(1);
		}
		System.out.println("Bye bye");
		System.exit(0);
	}
	
	/**
	 * Register the freshly created node to its father, and to the registry.
	 * @param id current node binding
	 * @param father father node binding
	 * @throws RemoteException
	 */
	protected void register(String id, String father) throws RemoteException {
		System.setSecurityManager(null);
		try{
			Naming.rebind(id, this);
		}
		catch(MalformedURLException ex){
			System.out.println("The URL provided as id is not a valid URL");
			System.exit(1);
		}
		
		if (father == null) return;

		try{
			this.addFather((SiteItf)( Naming.lookup(father) ));
		}
		catch(NotBoundException ex){
			System.out.println("This father is not in the registry table");
			System.exit(1);
		}
		catch(MalformedURLException ex){
			System.out.println("The URL provided as father is not a valid URL");
			System.exit(1);
		}
	}
	
	/**
	 * Send a message. This method is used to send the first message of the whole propagation wave.
	 * @param message the message to propagate
	 * @throws RemoteException
	 */
	public void sendMessage(String message) throws RemoteException {
		
		System.out.println("id: "+this.id+" mess: "+message);

		for (SiteItf son : this.children) {
			ArrayList<SiteItf> from = new ArrayList<SiteItf>();
			from.add(this);
			son.sendMessage(from, message);
		}
	}
	
	@Override
	public void sendMessage(List<SiteItf> from, String message) throws RemoteException {
			
		System.out.println("id: "+this.id+" mess: "+message);
		
		from.add(this);
		for (SiteItf son : this.children) {
			if(! from.contains(son)){		
				son.sendMessage(from, message);
			}
		}
	}

	@Override
	public void addSon(SiteItf son) throws RemoteException {
		if(this.children.contains(son)) return;
		if(this.equals(son)) return;
		
		this.children.add(son);
	}
	
	@Override
	public void addFather(SiteItf father) throws RemoteException {
		if(father == null) return;
		if(this.fathers.contains(father)) return;
		if(this.equals(father)) return;
		
		this.fathers.add(father);
		father.addSon(this);
	}

	@Override
	public void removeSon(SiteItf son) throws RemoteException {
		this.children.remove(son);
	}

	@Override
	public void addAllFathers(List<SiteItf> fathers) throws RemoteException {
		for(SiteItf father : fathers){
			this.addFather(father);
		}
	}

	@Override
	public void removeFather(SiteItf father) throws RemoteException {
		this.fathers.remove(father);
	}

	@Override
	public boolean equals(SiteItf other) throws RemoteException {
		return this.hashCode() == other.hashCode();
	}

	
	
	/**
	 * Create a string about how to use this class
	 * @return the string about how to use the class SiteImpl
	 */
	public static String stringUsage(){
		return "SiteImpl id [father]\n" +
				"\tid:\turl of the current node (including the hostname).\n" +
				"\t\tEx: \"//localhost/example\"\n" +
				"\tfather:\turl of my father node (including the hostname). If this node does not have a father, do not provide this argument.\n" +
				"\t\tEx: \"//fatherhost/father\"";
	}
	
	
	
	
	public static void main(String[] args) {
		if(args.length < 1 || args.length > 2) {
			System.out.println("Invalid number of arguments. "+args.length+ " arguments provided when only 1 or 2 are expected");
			System.out.println(SiteImpl.stringUsage());
			System.exit(1);
		}
		
		if ( args.length == 1){
			if("-h".equals(args[0]) || "--help".equals(args[0])){
				System.out.println(SiteImpl.stringUsage());
				System.exit(0);
			}
			
			try {
				new SiteImpl(args[0], null);
			} catch (RemoteException e) {
				System.out.println("Remote exception thrown");
				e.printStackTrace();
			}
		}
		else {
			try {
				new SiteImpl(args[0], args[1]);
			} catch (RemoteException e) {
				System.out.println("Remote exception thrown");
				e.printStackTrace();
			}
		}
	}
	
}