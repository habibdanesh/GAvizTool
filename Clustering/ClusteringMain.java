
package GA_Visualizer.Clustering;

import GA_Visualizer.DataStructures.Cluster;
import GA_Visualizer.DataStructures.Genome;
import static GA_Visualizer.Flags.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Habib
 */



public class ClusteringMain {
    
    private ArrayList<Genome> dataset;
    private ArrayList<Cluster> clusteredDataset;
    private int numNodes;
    private int numPopulations;
    private int numGenerations;
    private int numGenomes;
    private int numObjectives;
    private int maxGenomeLength;
    private KMeans km;
    
    
    public ClusteringMain(int numClusters) {
        
        dataset = new ArrayList<>();
        clusteredDataset = new ArrayList<>();
        km = new KMeans(numClusters);
    }
    
    
    public int numNodes() {
        
        return numNodes;
    }
    
    
    public int numPopulations() {
        
        return numPopulations;
    }
    
    
    public int numGenerations() {
        
        return numGenerations;
    }
    
    
    public int numGenomes() {
        
        return numGenomes;
    }
    
    
    public int numObjectives() {
        
        return numObjectives;
    }
    
    
    public void readDataset(String fileName) throws Exception {
        
        try(Scanner scanner = new Scanner(new File(fileName))) {
            
            String line = scanner.nextLine();
            String strToFind;
            
            strToFind = "No. of Customers";
            while(line.length()<strToFind.length() 
                    || 
                    !line.substring(0, strToFind.length()).equals(strToFind))
                line = scanner.nextLine();
            numNodes = Integer.parseInt(line.substring(18)) + 1; //+1 for depot
            
            strToFind = "No. of Populations";
            while(line.length()<strToFind.length() 
                    || 
                    !line.substring(0, strToFind.length()).equals(strToFind))
                line = scanner.nextLine();
            numPopulations = Integer.parseInt(line.substring(20));
            
            strToFind = "No. of Generations";
            while(line.length()<strToFind.length() 
                    || 
                    !line.substring(0, strToFind.length()).equals(strToFind))
                line = scanner.nextLine();
            numGenerations = Integer.parseInt(line.substring(20));
            
            strToFind = "No. of Individuals";
            while(line.length()<strToFind.length() 
                    || 
                    !line.substring(0, strToFind.length()).equals(strToFind))
                line = scanner.nextLine();
            numGenomes = Integer.parseInt(line.substring(20));
            
            strToFind = "No. of Objective Functions";
            while(line.length()<strToFind.length() 
                    || 
                    !line.substring(0, strToFind.length()).equals(strToFind))
                line = scanner.nextLine();
            numObjectives = Integer.parseInt(line.substring(28));
            
            for(int i=0; i<numPopulations; i++) {
                
                strToFind = "Population";
                while(line.length()<strToFind.length() 
                        || 
                        !line.substring(0, strToFind.length()).equals(strToFind))
                    line = scanner.nextLine();
                
                for(int j=0; j<numGenerations; j++) {
                    
                    strToFind = "Generation";
                    while(line.length()<strToFind.length() 
                            || 
                            !line.substring(0, strToFind.length()).equals(strToFind))
                        line = scanner.nextLine();
                    
                    for(int k=0; k<numGenomes; k++) {
                        
                        strToFind = "Chromosome";
                        while(line.length()<strToFind.length() 
                                || 
                                !line.substring(0, strToFind.length()).equals(strToFind))
                            line = scanner.nextLine();
                        
                        Genome genome = new Genome();
                        genome.setPopulationIndex(i);
                        genome.setGenerationIndex(j);
                        
                        String str = scanner.next();
                        do {
                            while(!str.equals("route:"))
                                str = scanner.next();
                            
                            do {
                                
                                int gene = scanner.nextInt();
                                if(gene != numNodes) genome.addGene(gene);
                                
                            } while(scanner.hasNextInt());
                            
                            str = scanner.next();
                            
                        } while(line.length()>=3 && str.equals("bus"));
                        
                        genome.setTotalDistance(scanner.nextDouble());
                        scanner.next();
                        genome.setNumRoutes(scanner.nextInt());
                        
                        if(genome.numGenes() > maxGenomeLength)
                            maxGenomeLength = genome.numGenes();
                        
                        dataset.add((Genome) genome.clone());
                    }
                }
            }
        }
    }
    
    
    public ArrayList<Cluster> operate(int clustAlgo) {
        
        switch(clustAlgo) {
            
            case KMEANS:
                clusteredDataset = km.clusterDataSet(dataset, numNodes, maxGenomeLength);
                break;
            
            case SOM:
                clusteredDataset = operateOverWeb();
                break;
        }
        
        
        
        return clusteredDataset;
    }
    
    
    public ArrayList<Cluster> operateOverWeb() {
        
        ArrayList<Integer> rawIntDataset = new ArrayList();
        for(int i=0; i<dataset.size(); i++) {
            for(int j=0; j<dataset.get(i).numGenes(); j++)
                rawIntDataset.add(dataset.get(i).getGene(j));
        }
        
        Gson gson = new Gson();
        String gsonObj = gson.toJson(rawIntDataset);
        
        System.out.println(rawIntDataset);
   
        try {
            URL url = new URL("http://0.0.0.0:8090/postjson");
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(gsonObj);
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (in.readLine() != null) {
            }
            System.out.println("\nWeb Service Invoked Successfully..");
            in.close();
        } catch (Exception e) {
            System.out.println("\nError while web Service");
            System.out.println(e);
        }
        
        return clusteredDataset;
    }
    
    
    public void printDataset() {
        
        for(int i=0; i<dataset.size(); i++) {
            
            Genome genome = dataset.get(i);
            System.out.print("Genome " + i + ":  ");
            genome.print();
            System.out.println();
        }
    }
    
    
    public void printClusteredDataset() {
        
        if(clusteredDataset.size() > 0) {
            
            for(int i=0; i<clusteredDataset.size(); i++) {
                
                Cluster cluster  = clusteredDataset.get(i);
                System.out.println("Cluster " + i + ":");
                cluster.print();
                System.out.println();
            }
        }
        else {
            System.out.println("Error: Clustering has not been performed!");
        }
        
    }
    
}






