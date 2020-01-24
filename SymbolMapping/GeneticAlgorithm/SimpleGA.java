package GA_Visualizer.SymbolMapping.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Random;


public class SimpleGA<T> {

    protected GASelectionScheme<T> selectsch;
    protected GAPopulation<T> pop;
    private GAPopulation<T> pop0;
    protected double pcross;
    protected double pmut;
    protected int elitismCount;
    private Random randObj;


    public SimpleGA(GAGenome<T> c, int psize, double crossx, double mut, int elitCount) {
        this.randObj = new Random();

        pop = new GAPopulation<T>(c, psize);
        pop0 = new GAPopulation<T>();
        pcross = crossx;
        pmut = mut; 
        elitismCount = elitCount;
    }

    public void setSelector(GASelectionScheme<T> scheme) {
        selectsch = scheme;
        selectsch.setPop(pop);
    }

    public GAPopulation<T> getPop() {
        return pop;
    }

    @SuppressWarnings("unchecked")
    public void step() {

        GAGenome<T> mom = null, dad = null;   

        pop0.clear();
        for(int i=0; i<pop.size()-1; i+=2) {	
            
            //Selecting 2 genomes
            try {
                mom = selectsch.select();  
                dad = selectsch.select();
            }
            catch(GAException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            
            //Crossover
            ArrayList<GAGenome<T>> kids =  new ArrayList<GAGenome<T>>(2);
            if(randObj.nextDouble() < pcross) {
                mom.cross(dad, kids);
                pop0.add(kids.get(0));
                pop0.add(kids.get(1));
            }
            else {
                GAGenome<T> kid0 = (GAGenome<T>) mom.clone();
                GAGenome<T> kid1 = (GAGenome<T>) dad.clone();
                kids.add(kid0);
                kids.add(kid1);

                pop0.add(kid0);
                pop0.add(kid1);
            }
            
            //Mutation
            kids.get(0).mutate(pmut);
            kids.get(1).mutate(pmut);
        }

        if(pop.size() % 2 != 0) {
            // do the remaining population member
            try {  
                mom = selectsch.select();  
                dad = selectsch.select();
            }
            catch(GAException ex) {
                ex.printStackTrace();
                System.exit(1);
            }

            ArrayList<GAGenome<T>> kids =  new ArrayList<GAGenome<T>>(2);		

            if(randObj.nextDouble() < pcross) {
                mom.cross(dad, kids);
                pop0.add(kids.get(0));
            }
            else {	      
                if(randObj.nextBoolean()) {
                    kids.add((GAGenome<T>) mom.clone());  
                    pop0.add(mom);
                }
                else {
                    kids.add((GAGenome<T>) dad.clone());  
                    pop0.add(dad);
                }
            }

            kids.get(0).mutate(pmut);
        }

        pop0.eval();	

        /*
        carry the elite individuals from the old population
        into the current population.  
        */
        pop0.sort();
        pop0.addEliteGenomes(pop.getEliteGenomes(elitismCount));
        pop0.sort();
        pop0.clip(pop.size());
        pop0.stats();

        // swap population
        GAPopulation<T> temp = pop;
        pop = pop0;
        pop0 = temp;

        selectsch.setPop(pop);

        pop.stats();
    }
}
