import java.rmi.Remote;
import java.rmi.RemoteException;


public interface SiteItf extends Remote {
	public void sendMessage(String Message) throws RemoteException;
	public void addSon(SiteItf son) throws RemoteException;
	public void removeSon(SiteItf son) throws RemoteException;
	public void setFather(SiteItf father) throws RemoteException;
}
