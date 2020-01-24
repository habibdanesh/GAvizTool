package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

public class GATournamentSelector<T> extends GARouletteWheelSelector<T> 
{
	  public GATournamentSelector(GAPopulation<T> pop){
		  super(pop);
	  }
	
          @Override
	  public GAGenome<T> select() throws GAException
	  {
		  GAGenome<T> genome0 = super.select();
		  GAGenome<T> genome1 = super.select();

		  double fit0 = genome0.evaluate(false);
		  double fit1 = genome1.evaluate(false);

		  return (fit0 > fit1) ? genome0 : genome1;
	  }		  	 
}
