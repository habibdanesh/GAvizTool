
package GA_Visualizer.DataStructures;

import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class Population implements Cloneable {
    
    private ArrayList<Generation> generations;
    
    
    public Population() {
        
        generations = new ArrayList<>();
    }
    
    
    public Population(Population other) throws CloneNotSupportedException {
        
        generations = new ArrayList<>();
        
        for(Generation gen : other.getGenerations())
            generations.add((Generation) gen.clone());
    }
    
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        
        return new Population(this);
    }
    
    
    public void addGeneration(Generation gen) throws CloneNotSupportedException {
        
        generations.add((Generation) gen.clone());
    }
    
    
    public Generation getGeneration(int index) {
        
        return generations.get(index);
    }
    
    
    public ArrayList<Generation> getGenerations() {
        
        return generations;
    }
    
    
    public int numGenerations() {
        
        return generations.size();
    }
    
    
    public void print() {
        
        for(int i=0; i<generations.size(); i++) {
            Generation generation = generations.get(i);
            System.out.println("Generation " + i + ":\n");
            generation.print();
            System.out.println("\n");
        }
    }
    
}










