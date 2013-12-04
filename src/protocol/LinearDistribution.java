package protocol;

import java.util.Random;

public class LinearDistribution {
	
	private static final long seed = 123456789;
	private static Random r = new Random(seed);
	
	public synchronized static double randomValue() {
		return r.nextDouble()*100;
	}

}
