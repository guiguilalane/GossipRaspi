package protocol;

import java.util.Random;

public class LinearDistribution {
	
	private static Random r;
	
	public synchronized static double randomValue(int otherSeed) {
		r = new Random(otherSeed);
		return r.nextDouble()*100;
	}

}
