
package GA_Visualizer.DataStructures;

import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class Generation implements Cloneable {
    
    private ArrayList<Cluster> clusters;
    
    
    public Generation() {
        
        clusters = new ArrayList<>();
    }
    
    
    public Generation(Generation other) {
        
        clusters = new ArrayList<>();
        
        for(Cluster c : other.getClusters())
            clusters.add((Cluster) c.clone());
    }
    
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        
        return new Generation(this);
    }
    
    
    public void addCluster(Cluster newCluster) {
        
        clusters.add(newCluster);
    }
    
    
    public Cluster getCluster(int index) {
        return clusters.get(index);
    }
    
    
    public ArrayList<Cluster> getClusters() {
        
        return clusters;
    }
    
    
    public int numClusters() {
        
        return clusters.size();
    }
    
    
    public int numEmptyClusters() {
        
        int n = 0;
        
        for(Cluster cluster : clusters) {
            if(cluster.isEmpty()) n++;
        }
        
        return n;
    }
    
    public int numFilledClusters() {
        
        int n = numClusters();
        
        for(Cluster cluster : clusters) {
            if(cluster.isEmpty()) n--;
        }
        
        return n;
    }
    
    
    public void print() {
        
        for(int i=0; i<clusters.size(); i++) {
            Cluster cluster  = clusters.get(i);
            System.out.println("Cluster " + i + ":");
            cluster.print();
            System.out.println();
        }
    }
    
}
