package protocol;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import protocol.avg.AverageCalc;
import protocol.avg.Protocol;


public class Rasp implements IRasp {
	
	private List<IRasp> neighborhood = new ArrayList<IRasp>();
	private List<IRasp> remoteNeighborhood= new ArrayList<IRasp>();
	private double myValue;
	private Protocol protocol;
	private int id;
	private int compteur;
	private static final String IPPREFIX = "192.168.50.";
	
	//instancié par l'overlay
	public Rasp(int id, Protocol p) {
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

	public void addNeighbor(Rasp rasp) {
		neighborhood.add(rasp);
		try {
			IRasp remR= (IRasp) Naming.lookup("//"+IPPREFIX+rasp.getId()+":1234"+rasp.getId()+"/rasp"+rasp.getId());
			remoteNeighborhood.add(remR);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public List<IRasp> getNeighborhood() {
		return this.neighborhood;
	}
	
	public String toString() {
		String s = "\n" + this.id + " neighborhood :";
		for (IRasp r : this.neighborhood) {
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
			System.out.println(InetAddress.getLocalHost().getHostAddress());
			r = new Rasp(Integer.valueOf(InetAddress.getLocalHost().getHostAddress().split("\\.")[3]), p);
			System.out.println(r.getId());
			r.registerMe();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
