
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
	
	
	private List<SiteItf> children;

	
	public SiteImpl(String id, String father) throws RemoteException {
		super();
		
		this.id = id;
		this.children = new ArrayList<SiteItf>();
		this.fathers = new ArrayList<SiteItf>();
		
		this.register(id, father);
		this.loop();
	}
	
	protected void loop() throws RemoteException{
		while(true){
			String message;
			Console console = System.console();
	        message = console.readLine("Please enter a message or a command\n" +
	        		"\t[addFather host | childrenSize | fathersSize | exit] :\n");
	        if("exit".equals(message))  						exit();
	        else if("-h".equals(message))						System.out.println(SiteImpl.stringUsage());
	        else if("--help".equals(message))					System.out.println(SiteImpl.stringUsage());
	        else if(message.startsWith(FATHER_STRING))			addFather(message);
	        else if(message.startsWith(FATHERS_SIZE_STRING))	printFathersSize();
	        else if(message.startsWith(CHILDREN_SIZE_STRING))	printChildrenSize();
	        else 												this.sendMessage(message);
		}
	}

	
	protected void printFathersSize(){
		System.out.println("Number of fathers: "+this.fathers.size());
	}
	
	protected void printChildrenSize(){
		System.out.println("Number of children: "+this.children.size());
	}
	
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
		
		if ( args.length == 1)
			try {
				new SiteImpl(args[0], null);
			} catch (RemoteException e) {
				System.out.println("Remote exception thrown");
				e.printStackTrace();
			}
		else
			try {
				new SiteImpl(args[0], args[1]);
			} catch (RemoteException e) {
				System.out.println("Remote exception thrown");
				e.printStackTrace();
			}
		
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

}