
package GA_Visualizer.DataStructures.Graph2D;

import com.jogamp.opengl.GL2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Habib
 */



public class Graph implements Cloneable {
    
    private ArrayList<Vertex> vertexList;
    private ArrayList<Edge> edgeList;
    private ArrayList<Edge> originalEdgeList;
    private int[][] adjacencyMat;
    private ConcurrentHashMap<Integer, Integer> mapping;
    private ArrayList<Graph> morphedRange;
    private ArrayList<Graph> isomorphicRange;
    
    
    public Graph() {
        
        vertexList = new ArrayList<>();
        edgeList = new ArrayList<>();
        originalEdgeList = new ArrayList<>();
        mapping = new ConcurrentHashMap<>();
        morphedRange = new ArrayList<>();
        isomorphicRange = new ArrayList<>();
    }
    
    
    public Graph(Graph other) {
        
        vertexList = new ArrayList<>();
        edgeList = new ArrayList<>();
        originalEdgeList = new ArrayList<>();
        mapping = new ConcurrentHashMap<>();
        morphedRange = new ArrayList<>();
        isomorphicRange = new ArrayList<>();
        
        for(Vertex v : other.getVertexList())
            vertexList.add((Vertex) v.clone());
        
        for(Edge e : other.getEdgeList())
            edgeList.add((Edge) e.clone());
        
        for(Edge e : other.getOriginalEdgeList())
            originalEdgeList.add((Edge) e.clone());
        
        if(other.getAdjacencyMat() != null)
            setAdjacencyMat(other.getAdjacencyMat());
        
        if(other.getMapping() != null)
            setMapping(other.getMapping());
    }
    
    
    public Graph(ArrayList<Vertex> vList, ArrayList<Edge> eList) {
        
        vertexList = new ArrayList<>();
        edgeList = new ArrayList<>();
        originalEdgeList = new ArrayList<>();
        mapping = new ConcurrentHashMap<>();
        morphedRange = new ArrayList<>();
        isomorphicRange = new ArrayList<>();
        
        for(Vertex v : vList)
            vertexList.add((Vertex) v.clone());
        
        for(Edge e : eList)
            edgeList.add((Edge) e.clone());
    }
    
    
    public Graph(ArrayList<Edge> eList) {
        
        vertexList = new ArrayList<>();
        edgeList = new ArrayList<>();
        originalEdgeList = new ArrayList<>();
        mapping = new ConcurrentHashMap<>();
        morphedRange = new ArrayList<>();
        isomorphicRange = new ArrayList<>();
        
        for(Edge e : eList) edgeList.add((Edge) e.clone());
    }
    
    
    @Override
    public Object clone() {
        
        return new Graph(this);
    }
    
    
    public void addVertex(Vertex newVertex) {
        
        vertexList.add(newVertex);
    }
    
    
    public void removeVertex(int index) {
        
        vertexList.remove(index);
    }
    
    
    public Vertex getVertexAt(int index) {
        
        return vertexList.get(index);
    }
    
    
    public Vertex getMappedVertexAt(int index) {
        
        if(mapping.get(index) == null)
            return null;
        else
            return vertexList.get(mapping.get(index));
    }
    
    
    public Vertex getVertex(String vName) {
        
        Vertex vertex = null;
        
        for(Vertex v : vertexList) {
            
            if(v.getName().equals(vName)) {
                
                vertex = v;
                break;
            }
        }
        
        return vertex;
    }
    
    
    public int getVertexIndex(String vName) {
        
        int index = -1;
        
        for(int i=0; i<vertexList.size(); i++) {
            
            Vertex v = vertexList.get(i);
            if(v.getName().equals(vName)) {
                
                index = i;
                break;
            }
        }
        
        return index;
    }
    
    
    public void addEdge(Edge newEdge) {
        
        edgeList.add(newEdge);
    }
    
    
    public void addEdge(int v1Index, int v2Index) {
        
        Vertex v1 = vertexList.get(v1Index);
        Vertex v2 = vertexList.get(v2Index);
        Edge newEdge = new Edge(v1, v2);
        newEdge.setV1Index(v1Index);
        newEdge.setV2Index(v2Index);
        edgeList.add(newEdge);
    }
    
    
    public Edge getEdgeAt(int index) {
        
        return edgeList.get(index);
    }
    
    
    public void removeEdge(int index) {
        
        edgeList.remove(index);
    }
    
    
    public int numVertices() {
        
        return vertexList.size();
    }
    
    
    public int numEdges() {
        
        return edgeList.size();
    }
    
    
    public void updateEdges() {
        
        for(Edge e : edgeList) {
            Vertex v1 = getVertexAt(e.getV1Index());
            Vertex v2 = getVertexAt(e.getV2Index());
            e.setLine(v1, v2);
        }
    }
    
    
    public ArrayList<Vertex> getVertexList() {
        
        return vertexList;
    }
    
    
    public ArrayList<Edge> getEdgeList() {
        
        return edgeList;
    }
    
    
    public void clearEdgeList() {
        
        edgeList.clear();
    }
    
    
    public ArrayList<Edge> getOriginalEdgeList() {
        
        return originalEdgeList;
    }
    
    
    public int[][] getAdjacencyMat() {
        
        return adjacencyMat;
    }
    
    
    public final void setAdjacencyMat(int[][] mat) {
        
        adjacencyMat = new int[numVertices()][numVertices()];
        for(int i=0; i<mat.length; i++) {
            for(int j=0; j<mat[i].length; j++)
                adjacencyMat[i][j] = mat[i][j];
        }
    }
    
    
    public ConcurrentHashMap<Integer, Integer> getMapping() {
        
        return mapping;
    }
    
    
    public final void setMapping(ConcurrentHashMap<Integer, Integer> mapping) {
        
        this.mapping.clear();
        this.mapping.putAll(mapping);
    }
    
    
    public void setMapping(ArrayList<Integer> vList) {
        
        mapping.clear();
        for(int i=0; i<vList.size(); i++)
            mapping.put(i, vList.get(i));
    }
    
    
    public ArrayList<Graph> getMorphedRange() {
        
        return morphedRange;
    }
    
    
    public ArrayList<Graph> getIsomorphicRange() {
        
        return isomorphicRange;
    }
    
    
    public void shuffleVertexList() {
        
        Collections.shuffle(vertexList);
    }
    
    
    public void fillAdjacencyMat() {
        
        if(vertexList.size() > 0) {
            
            int numVertices = vertexList.size();
            int numEdges = edgeList.size();
            adjacencyMat = new int[numVertices][numVertices];
            
            for(int i=0; i<numEdges; i++) {
                
                Edge e = edgeList.get(i);
                int v1 = e.getV1Index();
                int v2 = e.getV2Index();
                adjacencyMat[v1][v2] = 1;
                adjacencyMat[v2][v1] = 1;
            }
        }
        else
            System.err.println("Error: Cannot fill the adjacency matrix, "
                    + "vertex list is empty!");
    }
    
    
    public void printAdjacencyMat() {
        
        for(int i=0; i<adjacencyMat.length; i++) {
            
            for(int j=0; j<adjacencyMat[i].length; j++) {
                
                System.out.print(adjacencyMat[i][j] + " ");
            }
            
            System.out.println();
        }
    }
    
    
    public void printEdgeList() {
        
        System.out.print("Edge List:   ");
        
        for(Edge e : edgeList)
            System.out.print(e.getV1Index() + "," + e.getV2Index() + "  ");
    }
    
    
    public void resetEdgeList(ArrayList<Vertex> vList, 
            ArrayList<Integer> eList) {
        
        for(Edge e : edgeList)
            originalEdgeList.add((Edge) e.clone());
        
        edgeList.clear();
        
        for(int i=0; i<eList.size()-1; i++) {
            
            Vertex v1 = vList.get(eList.get(i));
            Vertex v2 = vList.get(eList.get(i+1));
            
            Edge newEdge = new Edge(v1, v2);
            newEdge.setV1Index(eList.get(i));
            newEdge.setV2Index(eList.get(i+1));
            edgeList.add(newEdge);
        }
    }
    
    
    public void resetEdgeList(ArrayList<Integer> eList) {
        
        for(Edge e : edgeList)
            originalEdgeList.add((Edge) e.clone());
        
        edgeList.clear();
        
        for(Vertex v : vertexList) v.clearNeighbors();
        
        for(int i=0; i<eList.size()-1; i++) {
            
            if(eList.get(i)==0 || eList.get(i+1)==0) continue; //Skipping depot
            
            if(!mapping.containsKey(eList.get(i))) continue;
            
            Vertex v1 = vertexList.get(mapping.get(eList.get(i)));
            Vertex v2 = vertexList.get(mapping.get(eList.get(i+1)));
            v1.addNeighbor(mapping.get(eList.get(i+1)));
            v2.addNeighbor(mapping.get(eList.get(i)));
            
            Edge newEdge = new Edge(v1, v2);
            newEdge.setV1Index(mapping.get(eList.get(i)));
            newEdge.setV2Index(mapping.get(eList.get(i+1)));
            edgeList.add(newEdge);
        }
        
        fillAdjacencyMat();
    }
    
    
    public void resetEdgeList() {
        
        for(Edge e : edgeList)
            originalEdgeList.add((Edge) e.clone());
        
        ArrayList<Edge> newEdgeList = new ArrayList<>();
        for(Vertex v : vertexList) v.clearNeighbors();
        
        //Update edgeList based on the mapping
        for(Edge edge : edgeList) {
            
            Vertex v1 = vertexList.get(mapping.get(edge.getV1Index()));
            Vertex v2 = vertexList.get(mapping.get(edge.getV2Index()));
            v1.addNeighbor(mapping.get(edge.getV2Index()));
            v2.addNeighbor(mapping.get(edge.getV1Index()));
            
            Edge newEdge = new Edge(v1, v2);
            newEdge.setV1Index(mapping.get(edge.getV1Index()));
            newEdge.setV2Index(mapping.get(edge.getV2Index()));
            newEdgeList.add(newEdge);
        }
        
        edgeList.clear();
        for(Edge edge : newEdgeList) edgeList.add(edge);
    }
    
    
    public int findNumCrosses() {
        
        int numCrosses = 0;
        
        for(int i=0; i<edgeList.size()-1; i++) {
            
            Edge e1 = edgeList.get(i);
            for(int j=i+1; j<edgeList.size(); j++) {
                Edge e2 = edgeList.get(j);
                if(e1.intersectsLine(e2)
                        && !e1.getP1().equals(e2.getP1())
                        && !e1.getP1().equals(e2.getP2())
                        && !e1.getP2().equals(e2.getP1())
                        && !e1.getP2().equals(e2.getP2())) 
                    numCrosses++;
            }
        }
        
        return numCrosses;
    }
    
    
    public int compareUsingBFS(Graph other) {
        
        int connectionScore = 0;
        
        for(Edge l1 : this.getEdgeList()) {
            
            boolean found = false;
            for(Edge l2 : other.getEdgeList()) {
                
                String l1v1Name = getVertexAt(l1.getV1Index()).getName();
                String l1v2Name = getVertexAt(l1.getV2Index()).getName();
                String l2v1Name = getVertexAt(l2.getV1Index()).getName();
                String l2v2Name = getVertexAt(l2.getV2Index()).getName();
                
                if((l1v1Name.equals(l2v1Name) || l1v1Name.equals(l2v2Name)) 
                        && 
                        (l1v2Name.equals(l2v1Name) || l1v2Name.equals(l2v2Name))) {

                    found = true;
                    break;
                }
            }
            
            if(found)
                connectionScore++;
        }
        
        return connectionScore;
    }
    
    
    public int compareUsingAdjMatrix(Graph other) {
        
        int score = 0;
        
        this.fillAdjacencyMat();
        other.fillAdjacencyMat();
        int[][] adjacencyMat1 = this.getAdjacencyMat();
        int[][] adjacencyMat2 = other.getAdjacencyMat();
        int numVertices = numVertices();
        
        for(int i=0; i<numVertices; i++) {
            
            for(int j=0; j<numVertices; j++) {
                
                if(adjacencyMat1[i][j] == adjacencyMat2[i][j]) score++;
            }
        }
        
        return score;
    }
    
    
    public double compareUsingHausdorffDistance(Graph other) {
        
        double totalDistance = 0; //Distance between the two graphs
        
        for(int i=0; i<numVertices(); i++) {
            
            double greatest = 0; //Distance between the two vertex
            
            Vertex vA = this.getVertexAt(i);; //Vertex in the mapped graph
            Vertex vB = other.getVertexAt(i); //Vertex in the reference graph
            
            for(int j=0; j<vA.numNeighbors(); j++) {
                
                Vertex nA = this.getVertexAt(vA.getNeighborAt(j));
                double shortest = Double.POSITIVE_INFINITY;
                
                for(int k=0; k<vB.numNeighbors(); k++) {
                    
                    Vertex nB = other.getVertexAt(vB.getNeighborAt(k));
                    double d = nA.distance(nB);
                    
                    if(d < shortest) shortest = d;
                }
                
                if(shortest > greatest) greatest = shortest;
            }
            
            totalDistance += greatest;
        }
        
        return totalDistance;
    }
    
    
    public double compareUsingEuclideanDistance(Graph other) {
        
        double totalDistance = 0;
        
        for(int i=0; i<numVertices(); i++) {
            
            Vertex vA;
            if(this.getMappedVertexAt(i) != null)
                vA = this.getMappedVertexAt(i);
            else
                vA = this.getVertexAt(i);
            
            Vertex vB = other.getVertexAt(i);
            
            totalDistance += vA.distance(vB);
        }
        
        return totalDistance;
    }
    
    
    //This method generates an isomorphic graph by random
    public Graph randomIsomorphicMapping() {
        
        Graph isoGraph = (Graph) this.clone();
        
        //Init random mapping
        ArrayList<Integer> randMapping = new ArrayList<>();
        
        for(int i=0; i<numVertices(); i++)
            randMapping.add(i);
        
        Collections.shuffle(randMapping);
        isoGraph.setMapping(randMapping);
        isoGraph.resetEdgeList();
        
        while(!isIsomorphic(isoGraph)) {
            Collections.shuffle(randMapping);
            isoGraph.setMapping(randMapping);
            isoGraph.resetEdgeList();
        }
        
        return isoGraph;
    }
    
    
    /*
    This method check whether two graphs are isomorphic by comparing their set
    of vertex degrees
    */
    public boolean isIsomorphic(Graph other) {
        
        boolean state = false;
        
        int[] degrees1 = new int[this.numVertices()];
        int[] degrees2 = new int[other.numVertices()];
        
        for(int i=0; i<numVertices(); i++) {
            degrees1[i] = this.getVertexAt(i).degree();
            degrees2[i] = other.getVertexAt(i).degree();
        }
        
        int counter = 0;
        for(int i=0; i<numVertices(); i++) {
            
            int d1 = degrees1[i];
            for(int j=0; j<numVertices(); j++) {
                
                int d2 = degrees2[j];
                if(d1 == d2) {
                    counter++;
                    degrees2[j] = -1;
                    break;
                }
            }
        }
        
        if(counter == numVertices()) state = true;
        
        return state;
    }
    
    
    public void generateMorphedRange(int rangeWidth) {
        
        Graph firstGraph = randomIsomorphicMapping();
        morphedRange.add(firstGraph);
        
        int numVertices = firstGraph.numVertices();
        
        //Generate a range of morphed graphs, from first graph towards last graph
        for(int i=1; i<rangeWidth-1; i++) {
            
            Graph morphedGraph = (Graph) firstGraph.clone();
            
            for(int j=0; j<numVertices; j++) {
                
                Vertex vFirst = firstGraph.getMappedVertexAt(j);
                Vertex vLast = getVertexAt(j);

                Vertex vMorphed = morphedGraph.getMappedVertexAt(j);
                vMorphed.x += ((vLast.x-vFirst.x) / (rangeWidth-1)) * i;
                vMorphed.y += ((vLast.y-vFirst.y) / (rangeWidth-1)) * i;
            }
            
            morphedGraph.updateEdges();
            morphedRange.add(morphedGraph);
        }
        
        morphedRange.add((Graph) this.clone());
    }
    
    
    public void generateIsomorphicRange(int rangeWidth) {
        
        for(int i=0; i<rangeWidth; i++) {
            
            Graph isomorphicGraph = randomIsomorphicMapping();
            //Graph isomorphicGraph = (Graph) this.clone();
            
            //Init random mapping
            ArrayList<Integer> randMapping = new ArrayList<>();

            for(int j=0; j<numVertices(); j++)
                randMapping.add(j);
            
            Collections.shuffle(randMapping);
            isomorphicGraph.setMapping(randMapping);
            isomorphicGraph.resetEdgeList();
        
            isomorphicRange.add(isomorphicGraph);
        }
    }
    
    
    public void printDegrees() {
        
        System.out.println("\nDegrees:");
        for(Vertex v : vertexList) 
            System.out.println(v.getName() + ": " + v.degree());
        System.out.println("\n");
    }
    
    
    public void draw(GL2 gl) {
        
        gl.glColor4d(0.855, 0.439, 0.839, 1);
        for(Edge e : originalEdgeList) e.draw(gl);
        
        gl.glTranslated(0, 0, 0.001);
        gl.glColor3d(0, 0, 0.502);
        for(Edge e : edgeList) e.draw(gl);
    }
    
    
    public void drawPhenotype(GL2 gl) { //test
        
        gl.glTranslated(0, 0, 0.001);
        for(Edge e : edgeList) e.draw(gl);
    }
    
}
