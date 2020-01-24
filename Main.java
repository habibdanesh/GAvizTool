
package GA_Visualizer;

import GA_Visualizer.Clustering.ClusteringMain;
import GA_Visualizer.DataStructures.Cluster;
import GA_Visualizer.DataStructures.Population;
import static GA_Visualizer.Flags.*;
import GA_Visualizer.Interface.InterfaceMain;
import GA_Visualizer.SymbolMapping.SymbolMappingMain1;
import GA_Visualizer.SymbolMapping.SymbolMappingMain2;
import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class Main {
    
    private static final int numClusters = 5;
    
    
    public static void main(String[] args) throws Exception {
        
        //Clustering
        System.out.println("** Start Clustering **");
        ClusteringMain clustering = new ClusteringMain(numClusters);
        //clustering.readDataset(args[1]);
        clustering.readDataset("input/25RC101/25RC101Out2020");
        
        ArrayList<Cluster> clusters = clustering.operate(SOM);
        
        
        //Symbol Mapping
        System.out.println("** Start Symbol Mapping **");
        

//************* Symbol mapping by morphing *************************************
        SymbolMappingMain2 symbolMapping = new SymbolMappingMain2();
        //symbolMapping.readReferenceShapes(args[2]);
        symbolMapping.readReferenceShapes("input/25RC101/shapes");
        symbolMapping.operate(clusters, clustering.numPopulations(), 
                clustering.numGenerations());
        ArrayList<Population> populations = symbolMapping.getPopulations();
//******************************************************************************
        
        
        //Running the main visualization interface
        System.out.println("** Start Visualization **");
        //InterfaceMain visInterface = new InterfaceMain(populations,  
          //      args[0], clustering.numNodes());
          
        InterfaceMain visInterface = new InterfaceMain(populations,  
                "input/25RC101/25RC101", clustering.numNodes());
    }
    
}
