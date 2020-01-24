package GA_Visualizer.SymbolMapping.GeneticAlgorithm;
import java.util.ArrayList;
import java.util.Collections;

public class GAPopulation<T> 
 {
	private ArrayList<GAGenome<T>> indiv = new ArrayList<GAGenome<T>>();
	private double fitSum, fitAvg;
	private double fitMax, fitMin;
        private GAGenome<T> best;
	
	public GAPopulation() {}
	
	public GAPopulation(GAGenome<T> c, int psize) {
		init(c, psize);
	}
	
	public void init(GAGenome<T> c, int psize)  {
		
		   fitSum = 0;
		   fitAvg = 0;
		   fitMin = 999;
		   fitMax = -999;
		   indiv.clear();
		   
		   for (int i=0; i<psize; i++)
			{
			@SuppressWarnings("unchecked")
			GAGenome<T> cc = (GAGenome<T>) c.clone();
			cc.initialize();
			indiv.add(cc);
			}
	}
	
	public void clip(int sz) 
	{
            int numGenomesToRemove = sz - indiv.size();
            for(int i=0; i<numGenomesToRemove; i++)
                indiv.remove(i);
	}
	
	public void add(GAGenome<T> c) {
		indiv.add(c);
	}
	
	public void eval() {
		
		fitSum = 0;
		fitAvg = 0;
		fitMin = 999999;
		fitMax = -999999;
		
		for (int i=0; i<indiv.size(); i++) 
		  {
			double fit = indiv.get(i).evaluate(false);
			fitSum += fit;
			if (fit > fitMax) {
                            fitMax = fit;
                            best = (GAGenome<T>) indiv.get(i).clone();
                        }
			if (fit < fitMin) fitMin = fit;
		  }
		
		fitAvg = fitSum / indiv.size();		
	}
	
	public void dump() {
		
	}
	
	public void clear() {
		indiv.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void sort() {
		Collections.sort(indiv);
	}
	
	public void stats() {
		fitSum = 0;
		fitAvg = 0;
		fitMin = 999999;
		fitMax = -999999;
		for (int i=0; i<indiv.size(); i++) 
			{
			double fit = indiv.get(i).evaluate(false);
			fitSum += fit;
			if (fit > fitMax) {
                            fitMax = fit;
                            best = (GAGenome<T>) indiv.get(i).clone();
                        }
			if (fit < fitMin) fitMin = fit;
			}
		fitAvg = fitSum / indiv.size();
	}
	
	public GAGenome<T> get(int i) {
		return indiv.get(i);
	}
	
	public int size() {
		return indiv.size();
	}
	
	public double fitsum() { 
		return fitSum;
		}

	public double fitave() { 
		return fitAvg;
		}

	public double fitmax() { 
		return fitMax;
		}

	public double fitmin() { 
		return fitMin;
		}

	public GAGenome<T> best() {
		//return indiv.get(indiv.size()-1);
                return best;
	  	}
        
        public ArrayList<GAGenome<T>> getEliteGenomes(int numElites) {
            ArrayList<GAGenome<T>> eliteList = new ArrayList<>();
            for(int i=1; i<=numElites; i++)
                eliteList.add((GAGenome<T>) indiv.get(indiv.size()-i));
            return eliteList;
        }
        
        public void addEliteGenomes(ArrayList<GAGenome<T>> eliteList) {
            for(GAGenome<T> g : eliteList)
                indiv.add((GAGenome<T>) g);
        }
}
