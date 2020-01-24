package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

public interface Evaluator<T> {
    
	public double op(GAGenome<T> g);
}
