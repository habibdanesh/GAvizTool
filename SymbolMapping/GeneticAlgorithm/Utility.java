package GA_Visualizer.SymbolMapping.GeneticAlgorithm;
import java.lang.reflect.Array;


public class Utility {

	public  static <E> E[] getArray(Class<E> clazz, int size) {
	    @SuppressWarnings("unchecked")
	    E[] arr = (E[]) Array.newInstance(clazz, size);

	    return arr;
	}
	
	public static void main(String args[])
	{
		GAGenome<Vec3> genome[];
		Class<GAGenome<Vec3>> cc = null;
		genome = Utility.getArray(cc, 3);
		System.out.println("genome has a length of "+genome.length);
	}
}
