import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface SiteItf extends Remote {
	
	public boolean equals(SiteItf other) throws RemoteException;
	public void sendMessage(List<SiteItf> from, String message) throws RemoteException;
	public void addSon(SiteItf son) throws RemoteException;
	public void removeSon(SiteItf son) throws RemoteException;
	public void addFather(SiteItf father) throws RemoteException;
	public void addAllFathers(List<SiteItf> fathers) throws RemoteException;
	public void removeFather(SiteItf father) throws RemoteException;
}
