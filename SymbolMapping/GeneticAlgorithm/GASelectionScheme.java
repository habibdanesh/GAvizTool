package GA_Visualizer.SymbolMapping.GeneticAlgorithm;
import java.util.Random;


public abstract class GASelectionScheme<T> {
  protected Random randObj;
  protected GAPopulation<T> pop;

  public GASelectionScheme(GAPopulation<T> pop) { 
	  randObj = new Random();
	  this.pop = pop;
  }
  
  public abstract GAGenome<T> select()  throws GAException; 

  public abstract void setPop(GAPopulation<T> pop);
 }