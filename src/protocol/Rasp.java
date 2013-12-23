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
import java.util.Random;

import protocol.avg.AverageCalc;
import protocol.avg.Protocol;


public class Rasp extends UnicastRemoteObject implements IRasp{
	
/**
	 * 
	 */
	private static final long serialVersionUID = -6324900570781582947L;
	private List<IRasp> remoteNeighborhood= new ArrayList<IRasp>();
	private double myValue;
	private Protocol protocol;
	private int id;
	private int nbCycle;
	private int compteur;
	private static final String IPPREFIX = "192.168.50.";

	
	//instancié par l'overlay
	public Rasp(int id, Protocol p, int nbCycle) throws RemoteException {
		super();
		this.id = id;
		this.protocol = p;
		this.nbCycle = nbCycle;
		this.init();
	}
	
	private void init() {
		this.compteur = 0;
		this.myValue = LinearDistribution.randomValue(this.hashCode());
		System.out.println("J'ai la valeur : " + this.myValue);	
	}
	
	@Override
	public int getCompteur() throws RemoteException {
		return compteur;
	}
	
	public double getValue() {
		return this.myValue;
	}
	
	@Override
	public int getNbCycle() throws RemoteException {
		return this.nbCycle;
	}
	
	public int id() {
		return this.id;
	}
	
	public void addNeighbor(IRasp rasp) {
		this.remoteNeighborhood.add(rasp);
		
	}
	
	public List<IRasp> getNeighborhood() {
		return this.remoteNeighborhood;
	}
	
	public String toString() {
		String s = "\n" + this.id + " neighborhood :";
		for (IRasp r : this.remoteNeighborhood) {
			try {
				s +=  r.id() + "; ";
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		return s;
	}
	
	public double doJob() throws RemoteException {
		/*exécute un cycle
			envoyer ma valeur à une rasp parmi mes voisines
			récupérer ça valeur
			mettre à jour ma valeur
		*/
		
		for(int i = 0; i < nbCycle; ++i) {
			executeCycle();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return myValue;
	}
	
	private IRasp getOneNeighbor() {
		IRasp r = null;
		Random rand = new Random();
		r = remoteNeighborhood.get(rand.nextInt(remoteNeighborhood.size()));
		return r;
	}
	
	@Override
	public void executeCycle() throws RemoteException {
		/*exécute un cycle : 
			- envoyer ma valeur à une rasp parmi mes voisines
			- récupérer ça valeur
			- mettre à jour ma valeur
		 */
		List<Double> listVal = new ArrayList<Double>();
		listVal.add(this.myValue);
		
		//echange de valeur start
		listVal.add(this.exchange());
		//echange de valeur stop
		
		this.myValue = protocol.calcul(listVal);
		
	}

	@Override
	public void receiveRequest(double value) throws RemoteException {
		List<Double> listVal = new ArrayList<Double>();
		listVal.add(this.myValue);
		listVal.add(value);
		this.compteur++;
		this.myValue = protocol.calcul(listVal);
	}
	
	@Override
	public String display() throws RemoteException {
		return this.toString();
	}
	
	private synchronized double exchange() throws RemoteException {
		IRasp r = getOneNeighbor();
		r.receiveRequest(this.myValue);
		return r.getValue();
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
			r = new Rasp(Integer.valueOf(InetAddress.getLocalHost().getHostAddress().split("\\.")[3]), p, Integer.valueOf(args[0]));
			System.out.println(r.id());
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
