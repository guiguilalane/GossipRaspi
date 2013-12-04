package protocol.avg;

import java.io.Serializable;
import java.util.List;

public interface Protocol extends Serializable{
	
	public double calcul(List<Double> listVal);
}
