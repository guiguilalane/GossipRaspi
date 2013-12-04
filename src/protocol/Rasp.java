package protocol;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import protocol.avg.AverageCalc;
import protocol.avg.Protocol;


public class Rasp extends UnicastRemoteObject implements IRasp {
	
/**
	 * 
	 */
	private static final long serialVersionUID = -6324900570781582947L;
	//	private List<IRasp> neighborhood = new ArrayList<IRasp>();
	//list of remote Object Rasp
	private List<IRasp> remoteNeighborhood= new ArrayList<IRasp>();
	private double myValue;
	private Protocol protocol;
	private int id;
	private int compteur;
	private static final String IPPREFIX = "192.168.50.";
	private static final String IPPREFIXTEST = "172.16.132.";

	
	//instancié par l'overlay
	public Rasp(int id, Protocol p) throws RemoteException {
		super();
		this.id = id;
		this.myValue = LinearDistribution.randomValue();
		this.protocol = p;
		this.compteur = 0;
		
	
		/** Enregistrement du raspi sur le port 12345 **/
	
//		IRasp voisin= Naming.lookup("")
	}
	
	public double getValue() {
		return myValue;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCompteur() {
		return compteur;
	}

	public void addNeighbor(IRasp rasp) {
//		neighborhood.add(rasp);
//			IRasp remR= (IRasp) Naming.lookup("//"+IPPREFIX+rasp.getId()+":1234"+rasp.getId()+"/rasp"+rasp.getId());
			remoteNeighborhood.add(rasp);
		
	}
	
	public List<IRasp> getNeighborhood() {
		return this.remoteNeighborhood;
	}
	
	public String toString() {
		String s = "\n" + this.id + " neighborhood :";
		for (IRasp r : this.remoteNeighborhood) {
			try {
				s +=  r.getId() + "; ";
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		return s;
	}
	
	public double doJob() throws RemoteException {
		compteur++;
		List<Double> listVal = new ArrayList<Double>();
		for(IRasp r: remoteNeighborhood) {
			listVal.add(r.getValue());
		}
		listVal.add(this.getValue());
		myValue = protocol.calcul(listVal);
		return myValue;
	}
	
	public void registerMe() {
		try {
			LocateRegistry.createRegistry(12345);
			Naming.rebind("//"+IPPREFIX+this.id+":12345/rasp"+this.id,this );
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//lancer le serveur de la Rasp
	public static void main(String[] args) {
		Protocol p = new AverageCalc();
		Rasp r = null;
		try {
			System.setProperty("java.rmi.server.hostName", InetAddress.getLocalHost().getHostAddress());
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			r = new Rasp(Integer.valueOf(InetAddress.getLocalHost().getHostAddress().split("\\.")[3]), p);
//			r = new Rasp(29, p);
			System.out.println(r.getId());
			r.registerMe();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
