package protocol.avg;

import java.util.List;

public class AverageCalc implements Protocol{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9099557095128956674L;

	@Override
	public synchronized double calcul(List<Double> listVal) {
		double sum = 0.0;
		double nbElem = 0;
		for(Double d: listVal) {
			sum += d;
			nbElem++;
		}
		return sum/nbElem;
	}
	
	
	

}
