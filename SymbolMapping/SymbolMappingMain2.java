
package GA_Visualizer.SymbolMapping;

import GA_Visualizer.DataStructures.Cluster;
import GA_Visualizer.DataStructures.Generation;
import GA_Visualizer.DataStructures.Genome;
import GA_Visualizer.DataStructures.Graph2D.Edge;
import GA_Visualizer.DataStructures.Graph2D.Graph;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import GA_Visualizer.DataStructures.Population;
import GA_Visualizer.ShapeCreator.Shape2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Habib
 */



public class SymbolMappingMain2 {
    
    private ArrayList<Population> populations;
    private static ArrayList<Shape2D> refShapes; //Pre-defined reference shapes
    private final int rangeWidth = 5000; //Width of the range of shapes
    
    //Define an array to store symbols of all clusters in each generation
    private ArrayList<ArrayList<Shape2D>> symbols;
    
    
    public SymbolMappingMain2() {
        
        populations = new ArrayList<>();
        refShapes = new ArrayList<>();
        symbols = new ArrayList<>();
    }
    
    
    public ArrayList<Population> getPopulations() {
        
        return populations;
    }
    
    
    public void operate(ArrayList<Cluster> clusters, int numPopulations, 
            int numGenerations) throws CloneNotSupportedException {
        
        extractPopulations(clusters, numPopulations, numGenerations);
        
        System.out.print("Generating a range of " + rangeWidth + " shapes");
        System.out.println(" for each reference shape..");
        //Go through each reference shape
        for(Shape2D shape : refShapes) shape.generateMorphedRange(rangeWidth);
        
        //Assign a symbol to each cluster in each generation
        assignSymbols(clusters.size(), numGenerations);
    }
    
    
    private void assignSymbols(int numClusters, int numGenerations) {
        
        //Go through each population
        for(Population pop : populations) {
            
            //Go through each global cluster
            for(int i=0; i<numClusters; i++) {
                
                Cluster globalCluster = new Cluster();
                
                //Get a range of morphed shapes
                ArrayList<Shape2D> shapeRange = new ArrayList<>();
                for(Graph shape : refShapes.get(i).getMorphedRange())
                    shapeRange.add((Shape2D) shape.clone());
                
                /*
                Calculate the fitness of each morphed shape in the range based
                on its similarity to the reference shape
                */
                double[] distances = new double[rangeWidth];
                for(int j=0; j<rangeWidth; j++) {
                    Shape2D shape = shapeRange.get(j);
                    distances[j] = shape.compareUsingEuclideanDistance(refShapes.get(i));
                }
                
                double maxDistance = 0;
                double minDistance = Double.MAX_VALUE;
                for(double d : distances) {
                    if(d > maxDistance) maxDistance = d;
                    if(d < minDistance) minDistance = d;
                }
                
                double distanceRange = maxDistance - minDistance;
                
                //Set normalized fitness
                for(int j=0; j<rangeWidth; j++) {
                    Shape2D shape = shapeRange.get(j);
                    double d = distances[j];
                    double f = (maxDistance - d) / distanceRange;
                    shape.setFitness(f);
                }
                
                //Take best genome of the cluster in each generation
                for(int j=0; j<numGenerations; j++) {
                    if(!pop.getGeneration(j).getCluster(i).isEmpty()) {
                        Cluster localCluster = pop.getGeneration(j).getCluster(i);
                        localCluster.sort();
                        globalCluster.addGenome(localCluster.getBestGenome());
                    }
                }
                
                //Set a symbol for the cluster in each generation
                globalCluster.sort();
                for(Genome genome : globalCluster.getGenomes()) {
                    
                    double genomeFitness = genome.getAggregateFitness();
                    
                    /*
                    Go through each shape in the range and find the most suitable
                    symbol based the fitness closeness
                    */
                    Shape2D symbol;
                    double minDifference = Double.MAX_VALUE;
                    int minDistancePos = 0;
                    for(int j=0; j<rangeWidth; j++) {
                        
                        Shape2D shape = shapeRange.get(j);
                        double shapeFitness = shape.getFitness();
                        double diff = Math.pow((genomeFitness-shapeFitness), 2);
                        if(diff < minDifference) {
                            minDifference = diff;
                            minDistancePos = j;
                        }
                    }
                    
                    //Set the symbol of the local cluster
                    symbol = (Shape2D) shapeRange.get(minDistancePos).clone();
                    int genIndex = genome.generationIndex();
                    pop.getGeneration(genIndex).getCluster(i).setSymbol(symbol);
                    
                    //test
                    //System.out.print("Genome: " + genome.getAggregateFitness() + "    ");
                    //System.out.println("Symbol: " + symbol.getFitness());
                }
                //System.out.println(""); //test
            }
        }
    }
    
    
    private void extractPopulations(ArrayList<Cluster> clusteredDataset, 
            int numPopulations, int numGenerations) 
            throws CloneNotSupportedException {
        
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
    
}










