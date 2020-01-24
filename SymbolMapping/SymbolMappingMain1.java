
package GA_Visualizer.SymbolMapping;

import GA_Visualizer.DataStructures.Cluster;
import GA_Visualizer.DataStructures.Generation;
import GA_Visualizer.DataStructures.Genome;
import GA_Visualizer.DataStructures.Graph2D.Edge;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import GA_Visualizer.DataStructures.Population;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.Crossover;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.Dump;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.Evaluator;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.GAGenome;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.Initializer;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.Mutator;
import GA_Visualizer.ShapeCreator.Shape2D;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.GATournamentSelector;
import GA_Visualizer.SymbolMapping.GeneticAlgorithm.SimpleGA;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Habib
 */



public class SymbolMappingMain1 {
    
    private static final int maxGenerations = 500;
    private static ArrayList<Shape2D> refShapes; //Pre-defined reference shapes
    
    
    public SymbolMappingMain1() {
        
        refShapes = new ArrayList<>();
    }
    
    
    public void operate(ArrayList<Cluster> clusters, int numNodes) 
            throws CloneNotSupportedException {
        
        //Go through each cluster
        int clusterCounter = 0;
        for(Cluster cluster : clusters) {

            clusterCounter++;
            System.out.println("Cluster " + clusterCounter);
            ArrayList<Shape2D> symbols = new ArrayList<>();
            
            //If the cluster is empty, skip the iteration
            if(cluster.isEmpty()) {
                System.out.println();
                continue;
            }
            
            //Take the best genome to represent the cluster
            cluster.sort();
            Genome repGenome = cluster.getBestGenome();
            
            System.out.print("Best Genome: ");
            repGenome.print();
            System.out.println();

            //Go through each pre-defined shape
            int shapeCounter = 0;
            for(Shape2D refShape : refShapes) {
                
                shapeCounter++;
                System.out.print(refShape.getName() + ":  ");
                
                /*
                Find the best mapping from representative genome
                to reference shape
                */
                GAGenome<Integer> genome = new GAGenome<>(new GAInitializer1(numNodes), 
                    new GAMutator1(), new GACrossOver1(), 
                        new GAEvaluator1(repGenome, refShape));
                
                genome.setDump(new GADump1());

                SimpleGA<Integer> ga = new SimpleGA<>(genome, 100, 0.9f, 0.001f, 20);
                ga.setSelector(new GATournamentSelector<>(ga.getPop()));

                int itrCounter = 0;
                while(itrCounter<maxGenerations && ga.getPop().fitmax()!=1.0) {                
                    ga.step();
                    itrCounter++;
                }

                Shape2D symbol = (Shape2D) refShape.clone();
                GAGenome<Integer> best = ga.getPop().best();
                ConcurrentHashMap<Integer, Integer> mapping;
                mapping = new ConcurrentHashMap<>();

                for(int j=0; j<best.size(); j++) {
                    mapping.put(best.get(j), j);
                    if(best.get(j) == 0) symbol.setDepotIndex(j);
                }
                
                symbol.setMapping(mapping);
                symbol.resetEdgeList(repGenome.getGenes());

                //Print stats
                System.out.print("Fitness=" + symbol.fitnessByHausdorff(refShape) + "  ");
                //System.out.print("(Max Found:" + ga.getPop().fitmax() + ")" + "    ");
                System.out.print("#Crosses=" + symbol.findNumCrosses() + "    ");
                System.out.println("Iterations=" + itrCounter);

                symbols.add(symbol);
                
                if(ga.getPop().fitmax() == 1.0) break;
            }

            //Set cluster symbol
            int symbolIndex = 0;
            Shape2D bestSymbol = symbols.get(symbolIndex);
            for(int j=1; j<symbols.size(); j++) {

                Shape2D s = symbols.get(j);
                if(s.getFitness() > bestSymbol.getFitness()) {
                    bestSymbol = s;
                    symbolIndex = j;
                }
            }

            cluster.setSymbol(bestSymbol);
            refShapes.remove(symbolIndex);
            System.out.println("Symbol:  " + bestSymbol.getName() + "\n");
        }
        
        System.out.println("\n\n");
    }
    
    
    public void readReferenceShapes(String fileName) throws Exception {
        
        try(Scanner scanner = new Scanner(new File(fileName))) {
            
            String line;
            
            line = scanner.nextLine();
            while(line.length()<13 || !line.substring(0, 13).equals("No. of Shapes"))
                line = scanner.nextLine();
            int numShapes = Integer.parseInt(line.substring(15));
            
            // Read shapes
            for(int i=0; i<numShapes; i++) {
                
                Shape2D tempShape = new Shape2D();
                
                line = "";
                
                while(line.length()<5 || !line.substring(0, 5).equals("Shape"))
                    line = scanner.nextLine();
                tempShape.setName(line.substring(7));
                
                while(line.length()<15 || !line.substring(0, 15).equals("No. of Vertices"))
                    line = scanner.nextLine();
                int numVertices = Integer.parseInt(line.substring(17));
                
                double maxX = Double.MIN_VALUE;
                double maxY = Double.MIN_VALUE;
                
                // Read vertices
                for(int j=0; j<numVertices; j++) {
                    
                    String vName = scanner.next();
                    
                    double x = scanner.nextDouble();
                    scanner.next(",");
                    double y = scanner.nextDouble();
                    
                    if(x > maxX) maxX = x;
                    if(y > maxY) maxY = y;
                    
                    Vertex v = new Vertex(x, y);
                    v.setName(vName.substring(0, vName.length()-1));
                    tempShape.addVertex(v);
                }
                
                //Normalize coords
                for(Vertex v : tempShape.getVertexList()) {
                    v.x /= (double) maxX;
                    v.y /= (double) maxY;
                }
                
                line = "";
                while(line.length()<12 || !line.substring(0, 12).equals("No. of Edges"))
                    line = scanner.nextLine();
                int numEdges = Integer.parseInt(line.substring(14, line.length()));
                
                // Read edges
                for(int j=0; j<numEdges; j++) {
                    
                    Edge tempEdge = new Edge();
                    
                    scanner.next(); //Skip edge name
                    
                    String v1Name = scanner.next();
                    int v1 = tempShape.getVertexIndex(v1Name);
                    
                    if(v1 != -1) {
                        
                        tempEdge.setV1Index(v1);
                        tempEdge.x1 = tempShape.getVertexAt(v1).x;
                        tempEdge.y1 = tempShape.getVertexAt(v1).y;
                    }
                    
                    scanner.next(",");
                    
                    String v2Name = scanner.next();
                    int v2 = tempShape.getVertexIndex(v2Name);
                    
                    if(v2 != -1) {
                        
                        tempEdge.setV2Index(v2);
                        tempEdge.x2 = tempShape.getVertexAt(v2).x;
                        tempEdge.y2 = tempShape.getVertexAt(v2).y;
                    }
                    
                    //Add vertex connections
                    if(v1!=-1 && v2!=-1) {
                        
                        tempShape.getVertexAt(v1).addNeighbor(v2);
                        tempShape.getVertexAt(v2).addNeighbor(v1);
                    }
                    
                    tempShape.addEdge(tempEdge);
                }
                
                refShapes.add((Shape2D) tempShape.clone());
            }
        }
    }
    
    
    public ArrayList<Population> extractPopulations(ArrayList<Cluster> clusteredDataset, 
            int numPopulations, int numGenerations) 
            throws CloneNotSupportedException {
        
        ArrayList<Population> populations = new ArrayList<>();
        
        int numClusters = clusteredDataset.size();
        
        for(int i=0; i<numPopulations; i++) {
            
            Population pop = new Population();
            
            for(int j=0; j<numGenerations; j++) {
                
                Generation generation = new Generation();
                
                for(int k=0; k<numClusters; k++) {
                    
                    Cluster cluster = new Cluster();
                    generation.addCluster((Cluster) cluster.clone());
                }
                
                pop.addGeneration((Generation) generation.clone());
            }
            
            populations.add((Population) pop.clone());
        }
        
        /*
        Take each genome in the dataset and put it in its place based on the
        population, generation and cluster index
        */
        
        for(int i=0; i<numClusters; i++) {
            
            Cluster sourceCluster = clusteredDataset.get(i);
            
            for(int j=0; j<sourceCluster.numGenomes(); j++) {
                
                Genome g = sourceCluster.getGenome(j);
                
                Cluster targetCluster = populations.get(g.populationIndex()).
                        getGeneration(g.generationIndex()).
                        getCluster(g.clusterIndex());
                
                targetCluster.addGenome((Genome) g.clone());
            }
        }
        
        //Set the best genome and symbol of each cluster
        for(Population pop : populations) {
            for(Generation generation : pop.getGenerations()) {
                for(int i=0; i<generation.numClusters(); i++) {
                    
                    Cluster cluster = generation.getCluster(i);
                    
                    if(!cluster.isEmpty()) {
                        
                        cluster.sort();
                        Genome bestGenome = cluster.getBestGenome();
                        
                        if(!clusteredDataset.get(i).isEmpty()) {
                            Shape2D symbol = (Shape2D) clusteredDataset.get(i).getSymbol().clone();
                            symbol.resetEdgeList(bestGenome.getGenes());
                            cluster.setSymbol(symbol);
                        }
                    }
                }
            }
        }
        
        return populations;
    }
    
}



class GAInitializer1 implements Initializer<Integer> {
    
    protected static int size;
    private final static Random rand = new Random();
    
    
    public GAInitializer1(int size) {
        
        this.size = size;
    }
	
    
    @Override
    public void op(GAGenome<Integer> g) {
        
        for(int i=0; i<size; i++) {
            
            int randGene = rand.nextInt(size);
            
            while(g.containsGene(randGene))
                randGene = rand.nextInt(size);
            
            g.add(randGene);
        }
    }
    
}



class GAMutator1 implements Mutator<Integer> {
    
    private final static Random rand = new Random();
    
    
    @Override
    public void op(GAGenome<Integer> g, double prob) {
        
        // Swap mutation
        for (int i=0; i<g.size(); i++) {
            
            if (rand.nextDouble() < prob) {
                
                // Get new gene position
                int newGenePos = (int)(rand.nextDouble() * g.size());
                
                // Get genes to swap
                int gene1 = g.get(newGenePos);
                int gene2 = g.get(i);
                
                // Swap genes
                g.setGene(i, gene1);
                g.setGene(newGenePos, gene2);
            }	
        }			
    }
    
}



class GACrossOver1 implements Crossover<Integer> {
    
    private final static Random rand = new Random();

    
    @Override
    public void op(GAGenome<Integer> mom, GAGenome<Integer> dad,
                                    ArrayList<GAGenome<Integer>> kids) {
        
        @SuppressWarnings("unchecked")
        GAGenome<Integer> sis =  (GAGenome<Integer>) mom.clone();
        @SuppressWarnings("unchecked")
        GAGenome<Integer> bro =  (GAGenome<Integer>) dad.clone();
        
        ArrayList<GAGenome<Integer>> offspring = new ArrayList<>();
        offspring.add(sis);
        offspring.add(bro);
        
        for(int i=0; i<offspring.size(); i++) {
            
            offspring.get(i).fill(-1);

            // Get subset of parent chromosomes
            int subsetPos1 = (int)(rand.nextDouble() * mom.size());
            int subsetPos2 = (int)(rand.nextDouble() * dad.size());

            // make the smaller the start and the larger the end
            final int startSubset = Math.min(subsetPos1, subsetPos2);
            final int endSubset = Math.max(subsetPos1, subsetPos2);

            // Loop and add the sub genes from mom to child
            for (int j = startSubset; j < endSubset; j++)
                offspring.get(i).setGene(j, mom.get(j));

            // Loop through dad's genes
            for (int j = 0; j < dad.size(); j++) {
                
                int dadGene = j + endSubset;
                if (dadGene >= dad.size()) {
                    
                    dadGene -= dad.size();
                }

                // If offspring doesn't have the gene add it
                if (offspring.get(i).containsGene(dad.get(dadGene)) == false) {
                    
                    // Loop to find a spare position in the kid's genes
                    for (int k = 0; k < offspring.get(i).size(); k++) {
                        
                        // Spare position found, add gene
                        if (offspring.get(i).get(k) == -1) {
                            offspring.get(i).setGene(k, dad.get(dadGene));
                            break;
                        }
                    }
                }
            }
        }
        
        offspring.get(0).invalidate();
        offspring.get(1).invalidate();
        kids.add(offspring.get(0));
        kids.add(offspring.get(1));
    }
    
}



class GAEvaluator1 implements Evaluator<Integer> {
    
    /*
    @attribute genome a representative chromosome from a VRP cluster
    @attribute mappedShape the result of mapping the genome to refShape
    */
    private Genome genome;
    private Shape2D mappedShape;
    private static Shape2D referenceShape;
    
    
    public GAEvaluator1(Genome repGenome, Shape2D refShape) 
            throws CloneNotSupportedException {
        
        genome = (Genome) repGenome.clone();
        mappedShape = (Shape2D) refShape.clone();
        referenceShape = (Shape2D) refShape.clone();
    }
    
    
    @Override
    public double op(GAGenome<Integer> g) {
        
        double fitness;
        
        ConcurrentHashMap<Integer, Integer> mapping = new ConcurrentHashMap<>();
        
        for(int i=0; i<g.size(); i++)
            mapping.put(g.get(i), i);
        
        mappedShape.setMapping(mapping);
        mappedShape.resetEdgeList(genome.getGenes());
        
        //fitness = mappedShape.fitnessByCrossing();
        //fitness = mappedShape.fitnessByBFSAndCrossing(referenceShape);
        //fitness = mappedShape.fitnessByAdjMatrixAndCrossing(referenceShape);
        fitness = mappedShape.fitnessByHausdorff(referenceShape);
        
        return fitness;
    }
    
}



class GADump1 implements Dump<Integer> {
    
    @Override
    public void op(GAGenome<Integer> g) {
        
        System.out.print("Genome:   ");
        
        for (int i=0; i<g.size(); i++)
            System.out.print(g.get(i) + " ");
    }
    
}
