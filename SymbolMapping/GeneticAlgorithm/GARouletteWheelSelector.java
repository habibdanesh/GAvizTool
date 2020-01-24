package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

public class GARouletteWheelSelector<T> extends GASelectionScheme<T> {

	  private double psum[];
	  
	  public GARouletteWheelSelector(GAPopulation<T> pop){
		  super(pop);
	  }
	  
          @Override
	  public void setPop(GAPopulation<T> pop)
	  {
		  this.pop = pop;		  
		  this.update();
	  }
	  
	  private void update() 
	  {
	       int n = pop.size();
	       psum = new double[n];

		   if(pop.fitmax() == pop.fitmin())
			{
		        for(int i=0; i<pop.size(); i++)
		        	psum[i] = (double)(i+1)/(double)pop.size();	// equal likelihoods
		    }
		   else if((pop.fitmax() > 0 && pop.fitmin() >= 0) ||
				   (pop.fitmax() <= 0 && pop.fitmin() < 0))
			{
				psum[0] = pop.get(0).evaluate(false);			
				for(int i=1; i<pop.size(); i++)
				  psum[i] = pop.get(i).evaluate(false) + psum[i-1];
				
				for(int i=0; i<pop.size(); i++)
				  psum[i] /= psum[pop.size()-1];
		    }
	  }
	  
          @Override
	  public GAGenome<T> select() throws GAException
	  {
		  double cutoff = randObj.nextDouble();
			// perform a binary search
		  int lower = 0; 
		  int upper = pop.size()-1;
		  while(upper >= lower)	{			  
			    int i = lower + (upper-lower)/2;
			    if (!(i >= 0 && i < pop.size()))
			    	throw new GAException(" i is"+i+", but popsize is "+pop.size());
			    if(psum[i] > cutoff)
			    	upper = i-1;
			    else
			    	lower = i+1;
		  		}
		 
		  if (lower >= pop.size())
			  lower = pop.size() -1;
		  return pop.get(lower);

	  }		  	 
 }
