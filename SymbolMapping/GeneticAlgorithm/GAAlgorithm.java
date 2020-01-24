package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

public class GAAlgorithm<T> {
	
	private GAPopulation<T> pop;
	protected double pcross;
	protected double mut;
	
	public GAAlgorithm(GAPopulation<T> pop)
	{
		this.pop = pop;
	}
	
	public GAPopulation<T> getPopulation()
	{
		return pop;
	}
	
	public int popsize()
	{
		return pop.size();
	}
	
	public void setCrossOver(double cross)
	{
		this.pcross = cross;
	}
	
	public double getCrossOver()
	{
		return pcross;
	}
	
	public void setMutation(double mut)
	{
		this.mut = mut;
	}
	
	public double getMutation()
	{
		return this.mut;
	}
}
