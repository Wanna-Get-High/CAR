
import java.io.Console;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9118194714523855257L;
	
	private SiteItf father;
	private String id;
	private List<SiteItf> children;

	
	public SiteImpl(String id, String father) throws RemoteException {
		super();
		
		this.id = id;
		this.children = new ArrayList<SiteItf>();
		
		this.register(id, father);
		this.loop();
	}
	
	protected void loop() throws RemoteException{
		while(true){
			String username;
			Console console = System.console();
	        username = console.readLine("Please enter user name : ");   
	        this.sendMessage("You entered : " + username);
		}
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
		
		if (father == null){
			this.father = null;
			return;
		}
		
		try{
			this.father = (SiteItf)( Naming.lookup(father));
			this.father.addSon(this);
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
	
	@Override
	public void sendMessage(String message) throws RemoteException {
		System.out.println("id: "+this.id+" mess: "+message);

		for (SiteItf son : this.children) {
			son.sendMessage(message);
		}
	}

	@Override
	public void addSon(SiteItf son) throws RemoteException {
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
}
