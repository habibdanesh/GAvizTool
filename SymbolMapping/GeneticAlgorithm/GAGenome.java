package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Random;


@SuppressWarnings("rawtypes")
public class GAGenome<T> implements Cloneable, Comparable {

  protected static int ID;
  private int id;	
  private double _fitness;
  private boolean _evaluated;	
  private Evaluator<T> eval;
  private Mutator<T> mutr;			
  private Initializer<T> init;	
  private Crossover<T> sexcross;	
  private Dump<T> dump;	
  private Random randObj;
  private ArrayList<T> genes;
  
  public GAGenome() {}

  public GAGenome(Initializer<T> ini, Mutator<T> mut, Crossover<T> cross, Evaluator<T> eval) {
	  id = ID++;
	  genes = new ArrayList<T>();
	  
	  this.init = ini;
	  this.eval = eval;
	  this.mutr = mut;
	  this.sexcross = cross;
  }

  public GAGenome(GAGenome<T> other) {
	  id = ID++;
	  
	  _fitness = other.getFitness();
	  _evaluated = other.evaluated();
	  eval = other.getEvaluator();
	  mutr = other.getMutator();
	  init = other.getInitializer();
	  sexcross = other.getCrossOver();
	  dump = other.getDump();
	  
	  setRandObj(new Random());
	  
	  genes = new ArrayList<T>();
	  for (int i=0; i<other.size(); i++)
	  {
		  genes.add((T) other.get(i));
	  }
  }
  
  public void resetSize(int sz)
  {
	  genes.clear();
	  for (int i=0; i<sz; i++)
	  {
		  genes.add(null);
	  }	  
  }
  
  public ArrayList<T> getGenes()
  {
	  return genes;
  }
  
  
  public void copy(GAGenome<T> other, int start, int lgth, int dest)
  {
	  for (int i=start,j=dest; i<start+lgth; i++,j++) 
		  genes.set(j, other.get(i));
  }
  
  public int size() {
	  return genes.size();
  }
  
  public T get(int i) {
	  return genes.get(i);
  }
  
  public void add(T val) {
	  genes.add(val);
  }
  
  public void setGene(int index, T gene) {
      genes.set(index, gene);
  }

  @Override
  public Object clone() {
	  return new GAGenome<T>(this);
  }
  
  @Override
  public int compareTo(Object otherobj) {
        @SuppressWarnings("unchecked")
        GAGenome<T> other = (GAGenome<T>) otherobj;

        if (_fitness == other.getFitness())
                return 0;

        return (_fitness > other.getFitness()) ? 1 : -1;
  }
  
  public boolean evaluated() {
	  return _evaluated;
  }

  public int getId() {
	return id;  	  
  }
  
  void setRandomObj(Random obj)
	{
	setRandObj(obj);
	}


  public double evaluate(double val)
	{
	 _evaluated = true;
	 return _fitness = val;	
	}
  
  public double evaluate(boolean flag)
  {
   if(_evaluated == false || flag == true)
     {
     if(eval != null) 
    	 _fitness = eval.op(this);
     
     _evaluated = true;
     }
   
   return _fitness; 
  }

  public double getFitness() {
	  return _fitness;
  }
  
  public Evaluator<T> getEvaluator() {
		return eval;
		}

  public void setEvaluator(Evaluator<T> f) { 
		_evaluated = false; 
		eval = f; 
		}

  public void invalidate() {
		_evaluated = false;
		}

  public void initialize() {
		_evaluated=false; 
		init.op(this);
		}

  public Initializer<T> getInitializer() {
		return init;
		}

  public void initializer(Initializer<T> op){
		init = op;
		}

  public void mutate(double p) { 
	mutr.op(this, p); 
	}

  public void cross(GAGenome<T> partner, ArrayList<GAGenome<T>> kids) {
	sexcross.op(this, partner, kids);
	}

  public Mutator<T> getMutator() {
	return mutr;
	}

  public void setMutator(Mutator<T> op) {
	mutr = op;
	}

  public void setCrossOver(Crossover<T> f) { 
	  sexcross = f; 
	  }
  
  public Crossover<T> getCrossOver() { 
	  return sexcross; 
	  }
  
  public void dumpVal() {
	  dump.op(this);
  }
  
  public Dump<T> getDump() {
	  return dump;
  }
  
  public void setDump(Dump<T> d) {
	  dump = d;
  }

public Random getRandObj() {
	return randObj;
}

public void setRandObj(Random randObj) {
	this.randObj = randObj;
}

public boolean containsGene(T gene) {
      return genes.contains(gene);
}

public void fill(T gene) {
    for(int i=0; i<genes.size(); i++)
        genes.set(i, gene);
}

}
