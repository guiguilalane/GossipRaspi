package protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRasp  extends Remote{
	public List<IRasp> getNeighborhood() throws RemoteException ;
	public void addNeighbor(Rasp rasp) throws RemoteException  ;
	public int getCompteur() throws RemoteException ;
	public double doJob() throws RemoteException ;
	public String toString() ;
	public int getId() throws RemoteException ;
	public double getValue() throws RemoteException ;
}
