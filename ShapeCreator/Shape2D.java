
package GA_Visualizer.ShapeCreator;

import GA_Visualizer.DataStructures.Graph2D.Edge;
import GA_Visualizer.DataStructures.Graph2D.Graph;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import com.jogamp.opengl.GL2;
import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class Shape2D extends Graph implements Cloneable {
    
    private double fitness;
    private int depotIndex = -1;
    private String name;
    private int id;
    private int activeVertex;
    
    private static final double vertexMoveValue = 1.5;
    
    
    public Shape2D() {
        
        super();
    }
    
    
    public Shape2D(Shape2D other) {
        
        super(other);
        
        fitness = other.getFitness();
        depotIndex = other.getDepotIndex();
        name = other.getName();
        id = other.getID();
        activeVertex = other.activeVertex();
    }
    
    
    @Override
    public Object clone() {
        
        return new Shape2D(this);
    }
    
    
    public double getFitness() {
        
        return fitness;
    }
    
    
    public void setFitness(double f) {
        
        fitness = f;
    }
    
    
    public void setDepotIndex(int index) {
        
        depotIndex = index;
    }
    
    
    public int getDepotIndex() {
        
        return depotIndex;
    }
    
    
    public String getName() {
        
        return name;
    }
    
    
    public void setName(String name) {
        
        this.name = name;
    }
    
    
    public void setID(int id) {
        
        this.id = id;
    }
    
    
    public int getID() {
        
        return id;
    }
    
    
    public void setActiveVertex(int v) {
        
        activeVertex = v;
    }
    
    
    public Vertex getLatestVertex() {
        
        return getVertexAt(numVertices()-1);
    }
    
    
    public void removeLatestVertex() {
        
        removeVertex(numVertices()-1);
    }
    
    
    public Edge getLatestEdge() {
        
        return getEdgeAt(numEdges()-1);
    }
    
    
    public void removeLatestEdge() {
        
        removeEdge(numEdges()-1);
    }
    
    
    public int activeVertex() {
        
        return activeVertex;
    }
    
    
    public void moveActiveVertexLeft() {
        
        getVertexAt(activeVertex).x -= vertexMoveValue;
    }
    
    
    public void moveActiveVertexRight() {
        
        getVertexAt(activeVertex).x += vertexMoveValue;
    }
    
    
    public void moveActiveVertexDown() {
        
        getVertexAt(activeVertex).y -= vertexMoveValue;
    }
    
    
    public void moveActiveVertexUp() {
        
        getVertexAt(activeVertex).y += vertexMoveValue;
    }
    
    
    public void printNeighbors() {
        
        for(int i=0; i<numVertices(); i++) {
            
            Vertex v = getVertexAt(i);
            System.out.print("v" + i + ":    ");
            ArrayList<Integer> neighbors = v.getNeighbors();
            
            for(int j=0; j<v.numNeighbors(); j++) {
                
                Vertex n = getVertexAt(neighbors.get(j));
                System.out.print(n.getName() + " ");
            }
            
            System.out.println();
        }
        
        System.out.println();
    }
    
    
    /* 
    This method calculates the fitness based on number of edge crossing 
    */
    public double fitnessByCrossing() {
        
        int numCrosses = findNumCrosses();
        if(numCrosses == 0) 
            fitness = 1.0f;
        else if(numCrosses == 1) 
            fitness = 0.9f;
        else
            fitness = 1.0f / (double)numCrosses;
        
        return fitness;
    }
    
    
    /* 
    This methos calculates the fitness based on the number of edge crossing
    and also vertex connectivity score by performing a brute force search, going
    through all the connections in the mapped shape and check whether there exists
    the same connection in the reference shape
    */
    public double fitnessByBFSAndCrossing(Shape2D refShape) {
        
        double bfsScore, crossingScore;
        
        int connectionScore = compareUsingBFS(refShape);
        
        if(connectionScore != 0)
            bfsScore = (double) connectionScore / (double) this.numEdges();
        else 
            bfsScore = 0;
        
        crossingScore = fitnessByCrossing();
        
        fitness = (0.9f * bfsScore) + (0.1f * crossingScore);
        
        return fitness;
    }
    
    
    /*
    This method calculates the fitness based on the adjacency matrix comparison
    combined with #crossings
    */
    public double fitnessByAdjMatrixAndCrossing(Shape2D refShape) {
        
        double adjMatScore, crossingScore;
        
        int connectionScore = compareUsingAdjMatrix(refShape);
        adjMatScore = (double)connectionScore / (Math.pow(numVertices(), 2));
        
        crossingScore = fitnessByCrossing();
        
        fitness = (0.9f * adjMatScore) + (0.1f * crossingScore);
        
        return fitness;
    }
    
    
    /*
    This method calculates the fitness based on the Hausdorff distance between
    the two shapes
    */
    public double fitnessByHausdorff(Shape2D refShape) {
        
        
        
        double hausdorffScore = compareUsingHausdorffDistance(refShape);
        
        if(hausdorffScore == 0) 
            fitness = 1.0f;
        else if(hausdorffScore>0 && hausdorffScore<= 1) 
            fitness = 0.99f;
        else 
            fitness = 1.0f / (double)hausdorffScore;
        
        return fitness;
    }
    
    
    public void drawVertices(GL2 gl, int v1) {
        
        for(int i=0; i<numVertices(); i++) {
            
            Vertex v = getVertexAt(i);
            
            if(i == v1)
                gl.glColor3d(0, 1, 0);
            else if(i == activeVertex)
                gl.glColor3d(0, 0, 0);
            else
                gl.glColor3d(0.863, 0.078, 0.235);
            
            v.draw(gl);
        }
    }
    
    
    public void drawEdges(GL2 gl) {
        
        gl.glColor3d(0.294, 0, 0.510);
        for(Edge e : getEdgeList()) {
            Vertex v1 = getVertexAt(e.getV1Index());
            Vertex v2 = getVertexAt(e.getV2Index());
            e.x1 = v1.x;
            e.y1 = v1.y;
            e.x2 = v2.x;
            e.y2 = v2.y;
            e.draw(gl);
        }
    }
    
}




