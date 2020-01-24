
package GA_Visualizer.DataStructures;

import GA_Visualizer.ShapeCreator.Shape2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Habib
 */



public class Cluster implements Cloneable {
    
    private ArrayList<Genome> genomes;
    private Shape2D symbol;
    private double avgFitness;
    
    
    public Cluster() {
        
        genomes = new ArrayList<>();
        symbol = new Shape2D();
        avgFitness = 0;
    }
    
    
    public Cluster(Cluster other) {
        
        genomes = new ArrayList<>();
        
        for(Genome genome : other.getGenomes())
            genomes.add((Genome) genome.clone());
        
        symbol = (Shape2D) other.getSymbol().clone();
        
        avgFitness = other.avgFitness;
    }
    
    
    @Override
    public Object clone() {
        
        return new Cluster(this);
    }
    
    
    public void addGenome(Genome newGenome) {
        
        genomes.add(newGenome);
    }
    
    
    public Genome getGenome(int index) {
        
        return genomes.get(index);
    }
    
    
    public ArrayList<Genome> getGenomes() {
        
        return genomes;
    }
    
    
    public int numGenomes() {
        
        return genomes.size();
    }
    
    
    public boolean isEmpty() {
        
        return genomes.isEmpty();
    }
    
    
    public Genome getBestGenome() {
        
        return genomes.get(numGenomes()-1);
    }
    
    
    public Genome getWorstGenome() {
        
        return genomes.get(0);
    }
    
    
    public void sort() {
        
        if(!isEmpty()) {
            setGenomesAggregateFitness();
            Collections.sort(genomes);
        }
    }
    
    
    public Shape2D getSymbol() {
        
        return symbol;
    }
    
    
    public void setSymbol(Shape2D symbol) {
        
        this.symbol = symbol;
    }
    
    
    public double getAvgFitness() {
        
        return avgFitness;
    }
    
    
    public double calcAvgFitness() {
        
        double totFitness = 0;
        for(Genome g : genomes) totFitness += g.getAggregateFitness();
        avgFitness = totFitness / numGenomes();
        
        return avgFitness;
    }
    
    
    public void setGenomesAggregateFitness() {
        
        if(numGenomes() == 1) {
            Genome genome = genomes.get(0);
            genome.getFitnessScores().clear();
            genome.addFitnessScore(1);
            genome.addFitnessScore(1);
            genome.setAggregateFitness(1);
        }
        else {
            //Find min & max for each objective
            double minObj1 = Double.MAX_VALUE;
            double maxObj1 = 0;
            int minObj2 = Integer.MAX_VALUE;
            int maxObj2 = 0;

            for(Genome genome : genomes) {
                double totalDistance = genome.getTotalDistance();
                int numRoutes = genome.getNumRoutes();

                if(totalDistance < minObj1) minObj1 = totalDistance;
                if(totalDistance > maxObj1) maxObj1 = totalDistance;
                if(numRoutes < minObj2) minObj2 = numRoutes;
                if(numRoutes > maxObj2) maxObj2 = numRoutes;
            }

            //Calculate range
            double rangeObj1 = maxObj1 - minObj1;
            int rangeObj2 = maxObj2 - minObj2;

            //Calculate the the aggregate fitness
            for(Genome genome : genomes) {
                double totalDistance = genome.getTotalDistance();
                double numRoutes = (double) genome.getNumRoutes();
                
                double fitnessObj1 = 1;
                double fitnessObj2 = 1;
                
                if(rangeObj1 > 0) fitnessObj1 = (maxObj1-totalDistance) / rangeObj1;
                if(rangeObj2 > 0) fitnessObj2 = (maxObj2-numRoutes) / (double)rangeObj2;
                double aggregateFitness = (fitnessObj1+fitnessObj2) / 2.0;
                
                genome.getFitnessScores().clear();
                genome.addFitnessScore(fitnessObj1);
                genome.addFitnessScore(fitnessObj2);
                genome.setAggregateFitness(aggregateFitness);
            }
        }
    }
    
    
    public void print() {
        
        for(int i=0; i<genomes.size(); i++) {
            Genome genome = genomes.get(i);
            System.out.print("Genome " + i + ":  ");
            genome.print();
            System.out.println();
        }
    }
    
}
