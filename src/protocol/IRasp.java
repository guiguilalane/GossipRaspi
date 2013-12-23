package protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRasp  extends Remote{
	public List<IRasp> getNeighborhood() throws RemoteException ;
	public void addNeighbor(IRasp rasp) throws RemoteException  ;
	public int getNbCycle() throws RemoteException ;
	public int getCompteur() throws RemoteException ;
	public double doJob() throws RemoteException ;
	public void executeCycle() throws RemoteException ;
	public void receiveRequest(double value) throws RemoteException ;
	public int id() throws RemoteException ;
	public double getValue() throws RemoteException ;
	public String display() throws RemoteException ;
	public void init() throws RemoteException ;
}
