package protocol.overlay;

import java.rmi.RemoteException;
import java.util.List;

import protocol.IRasp;

public class RunnableRasp implements Runnable, IRasp{
	
	private IRasp rasp;
	
	public RunnableRasp(IRasp rasp) {
		super();
		this.rasp = rasp;
	}

	@Override
	public List<IRasp> getNeighborhood() throws RemoteException {
		return this.rasp.getNeighborhood();
	}

	@Override
	public void addNeighbor(IRasp rasp) throws RemoteException {
		this.rasp.addNeighbor(rasp);		
	}

	@Override
	public int getNbCycle() throws RemoteException {
		return this.rasp.getNbCycle();
	}

	@Override
	public int getCompteur() throws RemoteException {
		return this.rasp.getCompteur();
	}

	@Override
	public double doJob() throws RemoteException {
		return this.rasp.doJob();
	}

	@Override
	public void executeCycle() throws RemoteException {
		this.rasp.executeCycle();		
	}

	@Override
	public void receiveRequest(double value) throws RemoteException {
		this.rasp.receiveRequest(value);
	}

	@Override
	public double getValue() throws RemoteException {
		return this.rasp.getValue();
	}

	@Override
	public int id() throws RemoteException {
		return this.rasp.id();
	}

	@Override
	public void run() {
		try {
			this.doJob();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IRasp getIRasp() {
		return this.rasp;
	}

	@Override
	public String display() throws RemoteException {
		return this.rasp.display();
	}

	@Override
	public void init() throws RemoteException {
		this.rasp.init();
	}
	
	

}
