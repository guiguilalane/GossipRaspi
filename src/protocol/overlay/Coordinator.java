package protocol.overlay;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import protocol.IRasp;
import protocol.Rasp;
import protocol.avg.Protocol;
import protocol.avg.AverageCalc;

//TODO: déployer le coordinateur sur la rasp raspi et déployer les autres rasp sur les raspberry correpondantes
public class Coordinator {
	
	private static final int NBRASP = 8;
	private static final String IPPREFIX = "192.168.50.";
	private static final String IPPREFIXTEST = "172.16.132.";

	private int alpha;
	private static Protocol protocol;
	
	Map<IRasp, List<IRasp>> overlay = new HashMap<IRasp, List<IRasp>>();
	
	public Coordinator() {
		protocol = new AverageCalc();
		this.alpha = NBRASP/2 + 1;
		List<Integer> connections = new ArrayList<Integer>();
		List<Integer> idDispo = new ArrayList<Integer>();
		List<Integer> notProcessed = new ArrayList<Integer>();
		List<IRasp> raspList = new ArrayList<IRasp>();
		IRasp ir = null;
		for(int i = 1; i <= NBRASP; ++i) {
			connections.add(0);
			notProcessed.add(i);
			//try to connect to rasp at 192.168.50.i:1234/raspi
			try {
				System.out.println("test 1");
				ir = (IRasp) Naming.lookup("//"+IPPREFIX+i+":12345/rasp"+i);
				System.out.println(ir);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			raspList.add(ir);
		}
		
		Random rand = new Random();
		
		for(int i = 1; i <= NBRASP && notProcessed.size() > 1; ++i) {
			notProcessed.remove(new Integer(i));
			idDispo = new ArrayList<Integer>(notProcessed);
			for(int j = connections.get(i-1); j < alpha && idDispo.size() > 0; ++j)  {
				int dispo = idDispo.get(rand.nextInt(idDispo.size()))-1;
				idDispo.remove(new Integer(dispo+1));
				//connection entre 2 rasp dans les deux sens
				try {
					raspList.get(i-1).addNeighbor(raspList.get(dispo));
					raspList.get(dispo).addNeighbor(raspList.get(i-1));

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connections.set(i-1, connections.get(i-1)+1);
				connections.set(dispo, connections.get(dispo)+1);
				if(connections.get(dispo) >= alpha) {
					notProcessed.remove(new Integer(dispo+1));
				}
			}
			
		}

		//TODO: ajouter dans la MAP
		for(IRasp rasp: raspList) {
			try {
				overlay.put(rasp, rasp.getNeighborhood());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(IRasp re: raspList) {
			System.out.println(re);
		}
		
	}
	
	public void launchJob() throws RemoteException {
		int raspindex = Integer.MAX_VALUE;
		Random rand = new Random();
		int nbIteration = 100;
		int i = 0;
		while(i < nbIteration) {
			raspindex = rand.nextInt(NBRASP)+1;
			Iterator<IRasp> itRasp = overlay.keySet().iterator();
			boolean find = false;
			IRasp r = null;
			while(itRasp.hasNext() && !find) {
				r = (IRasp) itRasp.next();
				if(r.getId() == raspindex) {
					find = true;
				}
			}
			if(find) {
				System.out.println("Moi la rasp : " + r.getId() + ", j'ai la valeur : " + r.doJob());
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			} else {
				//lever d'exception
				System.err.println("ça a PT sur l'index : " + raspindex);
			}
			i++;
		}
		for(IRasp r: overlay.keySet()) {
			System.out.println("Moi la rasp : " + r.getId() + ", j'ai été appélée : " + r.getCompteur() + " fois.");
		}
	}
	
	public static void main(String[] args) throws RemoteException {
		Coordinator o = new Coordinator();
		o.launchJob();
	}
	
	

}
