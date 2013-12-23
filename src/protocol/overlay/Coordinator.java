package protocol.overlay;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import protocol.IRasp;

public class Coordinator {

	private static final int NBRASP = 8;
	private static final String IPPREFIX = "192.168.50.";

	private int alpha;

	HashSet<RunnableRasp> overlay = new HashSet<RunnableRasp>();

	public Coordinator() {
		this.alpha = NBRASP/2 + 1;
		List<Integer> connections = new ArrayList<Integer>();
		List<Integer> idDispo = new ArrayList<Integer>();
		List<Integer> notProcessed = new ArrayList<Integer>();
		List<RunnableRasp> raspList = new ArrayList<RunnableRasp>();
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
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
			raspList.add(new RunnableRasp(ir));
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
					raspList.get(i-1).addNeighbor(raspList.get(dispo).getIRasp());
					raspList.get(dispo).addNeighbor(raspList.get(i-1).getIRasp());

				} catch (RemoteException e) {
					e.printStackTrace();
				}
				connections.set(i-1, connections.get(i-1)+1);
				connections.set(dispo, connections.get(dispo)+1);
				if(connections.get(dispo) >= alpha) {
					notProcessed.remove(new Integer(dispo+1));
				}
			}

		}

		for(RunnableRasp rasp: raspList) {
			overlay.add(rasp);
		}

	}

	public void launchJob() throws RemoteException {
		/*lance les job des rasps
		 * 
		 * */

		ExecutorService teps = 
				Executors.newFixedThreadPool(NBRASP);
		
		for(RunnableRasp rasp: overlay) {
			teps.execute(rasp);
		}


		teps.shutdown();
		//techniquement tout les jobs sont terminés
		
		for(RunnableRasp rasp: overlay) {
			System.out.println("Moi la rasp : " + rasp.id() + ", j'ai la valeur : " + rasp.doJob() + " et j'ai ete appelee : " + rasp.getCompteur() + " fois!!!");
		}

	}

	public static void main(String[] args) throws RemoteException {
		Coordinator o = new Coordinator();
		o.launchJob();
	}



}
