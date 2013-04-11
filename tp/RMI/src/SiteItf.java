import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for RMI objects.
 * @author Francois Lepan, Benjamin Van Ryseghem
 */
public interface SiteItf extends Remote {
	/**
	 * Method to check equality between objects
	 * @param other the other object
	 * @return true if both objects have the same hashCode
	 * @throws RemoteException
	 */
	public boolean equals(SiteItf other) throws RemoteException;
	
	/**
	 * Propagate a message through the node children
	 * @param from List of node the message has already be propagated by
	 * @param message the message to propagate
	 * @throws RemoteException
	 */
	public void sendMessage(List<SiteItf> from, String message) throws RemoteException;
	
	/**
	 * Add a son the the children list
	 * @param son a node
	 * @throws RemoteException
	 */
	public void addSon(SiteItf son) throws RemoteException;
	
	/**
	 * Remove a son from the children list. Does not add the father
	 * @param son a node
	 * @throws RemoteException
	 */
	public void removeSon(SiteItf son) throws RemoteException;
	
	/**
	 * Add a father to the fathers list. Contrary to addSon, this method have the responsibility to add the son also
	 * @param father a node
	 * @throws RemoteException
	 */
	public void addFather(SiteItf father) throws RemoteException;
	
	/**
	 * Add a collection of fathers to the fathers list
	 * @param fathers the list of fathers to add
	 * @throws RemoteException
	 */
	public void addAllFathers(List<SiteItf> fathers) throws RemoteException;
	
	/**
	 * Remove a father from the fathers list
	 * @param father
	 * @throws RemoteException
	 */
	public void removeFather(SiteItf father) throws RemoteException;
}
