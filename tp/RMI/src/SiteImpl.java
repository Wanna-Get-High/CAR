import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	private SiteItf father;
	private int id;
	private List<SiteItf> children;
	DataInputStream input;
	DataOutputStream output;
	
	public SiteImpl(int id, SiteItf father,String adress, int port) throws RemoteException {
		super();
		this.setInputOutput(adress,port);
		
		this.id = id;
		this.father = father;
		this.children = new ArrayList<SiteItf>();
	}
	
	private void setInputOutput(String adress, int port) {
		try {
			Socket server = new Socket(adress,port);
			this.input = new DataInputStream(server.getInputStream());
			this.output = new DataOutputStream(server.getOutputStream());
			
		} catch (IOException e) {
			System.err.println("could not connect to the socket: "+adress+" port: "+port);
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
	public void addSon(SiteImpl son) {
		this.children.add(son);
	}
	
	
	public static void main(String[] args) {
		
		
		
		SiteImpl root = new SiteImpl(0, null);
		SiteImpl son1 = new SiteImpl(1, root);
		SiteImpl son2 = new SiteImpl(2, root);
		root.addSon(son1);
		root.addSon(son2);
		SiteImpl son3 = new SiteImpl(3, son2);
		son1.addSon(new SiteImpl(5, son1));
		son2.addSon(son3);
		son3.addSon(new SiteImpl(4, son3));
		
		try {
			root.sendMessage("plop");
		} catch (Exception e) {
			System.out.println("fubu");
		}
		
	}
}
