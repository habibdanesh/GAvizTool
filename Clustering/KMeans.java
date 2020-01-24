
package GA_Visualizer.Clustering;

import GA_Visualizer.DataStructures.Cluster;
import GA_Visualizer.DataStructures.Genome;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Habib
 */



public class KMeans {
    
    private int K;
    private int maxLength;
    private int numNodes;
    private Cluster[] clusters;
    private Cluster[] prevClusters;
    private Centroid[] centroids;
    private Centroid[] prevCentroids;
    
    
    
    public KMeans(int numClusters) {
        
        K = numClusters;
        clusters = new Cluster[K];
        centroids = new Centroid[K];
    }
    
    public ArrayList<Cluster> clusterDataSet(ArrayList<Genome> dataset, int numCustomers, 
            int maxLength) {
        
        ArrayList<Cluster> clusterList = new ArrayList<>();
        
        numNodes = numCustomers + 1; //+1 depot
        this.maxLength = maxLength;
        
        initialize();
        
        boolean areClustersStable = false;
        boolean areCentroidsStable = false;
        int itrCounter = 0;
        
        do {
            itrCounter++;
            
            if(itrCounter > 1) {
                //Make a copy of the latest dataset
                prevClusters = new Cluster[K];
                System.arraycopy(clusters, 0, prevClusters, 0, K);
                clusters = new Cluster[K];
                for(int i=0; i<K; i++) clusters[i] = new Cluster();
                
                //Make a copy of the latest centroids
                prevCentroids = new Centroid[K];
                System.arraycopy(centroids, 0, prevCentroids, 0, K);
            }
            
            //Go through each genome and assign it to a cluster
            for(Genome g : dataset) {
                int clusterIndex = findClosestCentroid(g);
                clusters[clusterIndex].addGenome((Genome) g.clone());
                g.setClusterIndex(clusterIndex);
                centroids[clusterIndex].increaseNumGenomes();
            }
            
            //Update the centroids position
            moveCentroids();
            
            //Check the convergence
            if(itrCounter > 1) {
                areClustersStable = checkClustersStability();
                areCentroidsStable = checkCentroidsStability();
            }
            
        } while(!areClustersStable || !areCentroidsStable);
        
        for(int i=0; i<K; i++)
            clusterList.add((Cluster) clusters[i].clone());
        
        int emptyClusters = 0;
        for(Cluster cluster : clusterList) {
            if(cluster.isEmpty()) emptyClusters++;
        }
        
        System.out.print("KMeans Done:   ");
        System.out.print("Iterations=" + itrCounter);
        System.out.println("\n");
        
        return clusterList;
    }
    
    
    public void initialize() {
        
        for(int i=0; i<K; i++) {
            clusters[i] = new Cluster();
            centroids[i] = new Centroid(maxLength, numNodes);
        }
        
    }
    
    
    public int findClosestCentroid(Genome g) {
        
        int centroidIndex = 0;
        double minDistance = Double.POSITIVE_INFINITY;
        
        ArrayList<Integer> genomeGenes = new ArrayList<>();
        for(Integer gene : g.getGenes())
            genomeGenes.add(gene);
        
        //Go through each centroid and calculate the distance
        for(int i=0; i<K; i++) {
            
            ArrayList<Integer> centroidGenes = centroids[i].getGenes();
            
            if(genomeGenes.size() < maxLength) {
                //Extend the genome length, so length(g)=length(centroid)
                int extensionLength = maxLength - g.numGenes();
                for(int j=0; j<extensionLength; j++)
                    genomeGenes.add(0);
            }
            
            double d = 0; //Total distance between the genes
            
            //Go through each gene
            for(int j=0; j<maxLength; j++) {
                int gene1 = centroidGenes.get(j);
                int gene2 = genomeGenes.get(j);
                d += Math.pow(gene1-gene2, 2);
            }
            
            if(d < minDistance) {
                minDistance = d;
                centroidIndex = i;
            }
        }
        
        
        return centroidIndex;
    }
    
    
    public void moveCentroids() {
        
        //Go through clusters
        for(int i=0; i<K; i++) {
            
            //Sum up all the genomes assigned to this centroid
            int[] sum = new int[maxLength];
            int[] avg = new int[maxLength];
            int numGenomes = clusters[i].numGenomes();
            
            //If cluster is not empty
            if(numGenomes > 0) {
                
                //Go through genomes
                for(int j=0; j<numGenomes; j++) {

                    Genome genome = clusters[i].getGenome(j);

                    for(int k=0; k<genome.numGenes(); k++)
                        sum[k] += genome.getGene(k);
                }

                //Divide the sums by numGenomes to get the average
                for(int j=0; j<maxLength; j++)
                    avg[j] = sum[j] / numGenomes;

                centroids[i].resetGenes(avg);
            }
            else {
                //Re-initialize the centroid of empty cluster
                centroids[i] = new Centroid(maxLength, numNodes);
            }
        }
    }
    
    
    public boolean checkClustersStability() {
        
        boolean stable = false;
        int clusterCounter = 0;
        
        for(int i=0; i<K; i++) {
            
            int genomeCounter = 0;
            ArrayList<Genome> genomes = clusters[i].getGenomes();
            ArrayList<Genome> prevGenomes = prevClusters[i].getGenomes();
            
            if(genomes.size() == prevGenomes.size()) {
                
                for(Genome g : genomes) {
                    
                    for(Genome prevG : prevGenomes) {
                        
                        if(g.equalsTo(prevG)) {
                            genomeCounter++;
                            break;
                        }
                    }
                }
            }
            
            if(genomeCounter == genomes.size()) clusterCounter++;
        }
        
        if(clusterCounter == K)
            stable = true;
        
        return stable;
    }
    
    
    public boolean checkCentroidsStability() {
        
        boolean stable = false;
        int centroidCounter = 0;
        
        for(int i=0; i<K; i++) {
            
            int geneCounter = 0;
            ArrayList<Integer> centroidGenes = centroids[i].getGenes();
            ArrayList<Integer> prevCentroidGenes = prevCentroids[i].getGenes();
            
            for(Integer gene : centroidGenes) {
                if(prevCentroidGenes.contains(gene)) geneCounter++;
            }
            
            if(geneCounter == maxLength) centroidCounter++;
        }
        
        if(centroidCounter == K)
            stable = true;
        
        return stable;
    }
    
}



class Centroid {
    
    private int numGenomes;
    private int length;
    private int upperBound;
    private ArrayList<Integer> genes;
    
    
    public Centroid(int length, int upperBound) {
        
        this.length = length;
        this.upperBound = upperBound;
        genes = new ArrayList<>(length);
        initialize();
    }
    
    
    public ArrayList<Integer> getGenes() {
        
        return genes;
    }
    
    
    public void resetGenes(int[] geneList) {
        
        genes.clear();
        for(int i=0; i<length; i++)
            genes.add(geneList[i]);
    }
    
    
    public final void initialize() {
        
        final Random rand = new Random();
        
        for(int i=0; i<length; i++) {
            int randGene = rand.nextInt(upperBound);
            
            //Only 0 (depot) can be repeated
            while(genes.contains(randGene) && randGene!=0)
                randGene = rand.nextInt(upperBound);
            
            genes.add(randGene);
        }
    }
    
    
    public void increaseNumGenomes() {
        
        numGenomes++;
    }
    
    
    public boolean isEmpty() {
        
        return (numGenomes <= 0);
    }
    
}







