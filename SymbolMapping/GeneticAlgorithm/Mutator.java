package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

public interface Mutator<T> {
	public void op(GAGenome<T> g, double prob);
}
