package GA_Visualizer.SymbolMapping.GeneticAlgorithm;
import java.util.ArrayList;


public interface Crossover<T> {
	public void op(GAGenome<T> g0, GAGenome<T> g1, ArrayList<GAGenome<T>> kids);
}
