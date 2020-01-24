
package GA_Visualizer.DataStructures;

import GA_Visualizer.DataStructures.Graph2D.Graph;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import static java.lang.Math.*;
import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class Genome implements Cloneable, Comparable<Genome> {
    
    private ArrayList<Integer> genes;
    private ArrayList<Double> fitnessScores;
    private double aggregateFitness;
    private Graph graph;
    private int numRoutes;
    private double totalDistance;
    private int populationIndex;
    private int generationIndex;
    private int clusterIndex;
    private Vertex origin; //Origin of drawing
    private boolean eligibleToDraw;
    private double[] color = {0, 0, 0};
    
    
    public Genome() {
        
        genes = new ArrayList<>();
        fitnessScores = new ArrayList<>();
        graph = new Graph();
        eligibleToDraw = true;
    }
    
    
    public Genome(Genome other) {
        
        genes = new ArrayList<>();
        for(Integer g : other.getGenes()) genes.add(g);
        
        fitnessScores = new ArrayList<>();
        for(double f : other.getFitnessScores()) fitnessScores.add(f);
        
        graph = (Graph) other.getGraph().clone();
        
        aggregateFitness = other.getAggregateFitness();
        numRoutes = other.getNumRoutes();
        totalDistance = other.getTotalDistance();
        populationIndex = other.populationIndex();
        generationIndex = other.generationIndex();
        clusterIndex = other.clusterIndex();
        
        if(other.getOrigin() != null)
            origin = (Vertex) other.getOrigin().clone();
        
        eligibleToDraw = other.isEligibleToDraw();
        
        setColor(other.color);
    }
    
    
    @Override
    public Object clone() {
        
        return new Genome(this);
    }
    
    
    @Override
    public int compareTo(Genome other) {
        
        if(aggregateFitness == other.getAggregateFitness())
            return 0;
        
        return (aggregateFitness > other.getAggregateFitness()) ? 1 : -1;
    }
    
    
    public boolean equalsTo(Genome other) {
        
        boolean isEqual = false;
        int geneCounter = 0;
        
        int numGenes = this.numGenes();
        int otherNumGenes = other.numGenes();
        
        if(numGenes == otherNumGenes) {
            
            for(int i=0; i<numGenes; i++) {
                if(getGene(i) == other.getGene(i)) geneCounter++;
            }
            
            if(geneCounter == numGenes) isEqual = true;
        }
        
        return isEqual;
    }
    
    
    public void addGene(int gene) {
        
        genes.add(gene);
    }
    
    
    public void setGene(int index, int gene) {
        
        genes.set(index, gene);
    }
    
    
    public int getGene(int index) {
        
        return genes.get(index);
    }
    
    
    public ArrayList<Integer> getGenes() {
        
        return genes;
    }
    
    
    public int numGenes() {
        
        return genes.size();
    }
    
    
    public double getFitnessScore(int index) {
        
        return fitnessScores.get(index);
    }
    
    
    public ArrayList<Double> getFitnessScores() {
        
        return fitnessScores;
    }
    
    
    public void addFitnessScore(double score) {
        
        fitnessScores.add(score);
    }
    
    
    public Graph getGraph() {
        
        return graph;
    }
    
    
    public void createGraph(ArrayList<Vertex> vList) {
        
        graph.resetEdgeList(vList, genes);
    }
    
    
    public double getAggregateFitness() {
        
        return aggregateFitness;
    }
    
    
    public void setAggregateFitness(double f) {
        
        aggregateFitness = f;
    }
    
    
    public int numObjectives() {
        
        return fitnessScores.size();
    }
    
    
    public void setNumRoutes(int n) {
        
        numRoutes = n;
    }
    
    
    public int getNumRoutes() {
        
        return numRoutes;
    }
    
    
    public void setTotalDistance(double d) {
        
        totalDistance = d;
    }
    
    
    public double getTotalDistance() {
        
        return totalDistance;
    }
    
    
    public void setPopulationIndex(int index) {
        
        populationIndex = index;
    }
    
    
    public int populationIndex() {
        
        return populationIndex;
    }
    
    
    public void setGenerationIndex(int index) {
        
        generationIndex = index;
    }
    
    
    public int generationIndex() {
        
        return generationIndex;
    }
    
    
    public void setClusterIndex(int index) {
        
        clusterIndex = index;
    }
    
    
    public int clusterIndex() {
        
        return clusterIndex;
    }
    
    
    public void setOrigin(Vertex origin) {
        
        this.origin = (Vertex) origin.clone();
    }
    
    
    public Vertex getOrigin() {
        
        return origin;
    }
    
    
    public double calcOriginDistance(Genome other) {
        
        double d;
        
        double x1 = origin.x;
        double y1 = origin.y;
        double x2 = other.getOrigin().x;
        double y2 = other.getOrigin().y;
        
        d = sqrt(pow(x2-x1, 2) + pow(y2-y1, 2));
        
        return d;
    }
    
    
    public boolean isEligibleToDraw() {
        
        return eligibleToDraw;
    }
    
    
    public void setEligibleToDraw(boolean state) {
        
        eligibleToDraw = state;
    }
    
    
    public double[] getColor() {
        
        return color;
    }
    
    
    public final void setColor(double[] c) {
        
        System.arraycopy(c, 0, color, 0, 3);
    }
    
    
    public void print() {
        
        for(int g : genes)
            System.out.print(g + " ");
        
        System.out.print("    ");
        System.out.print("#Routes:" + numRoutes);
        System.out.print("   ");
        System.out.print("Distance:" + totalDistance);
    }
    
    
    public void drawPhenotype(GL2 gl, boolean isActiveGenome) {
        
        if(isActiveGenome) 
            gl.glColor3d(0.502, 0, 0.502);
        else 
            gl.glColor3d(0.373, 0.620, 0.627);
            
        graph.drawPhenotype(gl);
    }
    
    
    public void drawAsCircle(GL2 gl, boolean isActiveGenome) {
        
        if(isActiveGenome) 
            gl.glColor3d(0.502, 0, 0.502);
        else 
            //gl.glColor3d(0, 0, 0.502);
            gl.glColor3dv(color, 0);
        /*
        gl.glPointSize(10);
        gl.glBegin(GL2.GL_POINTS);
            gl.glVertex2d(0, 0);
        gl.glEnd();*/
        
        /*gl.glBegin(GL2.GL_LINE_LOOP);
        for(int i=0; i<=300; i++){
            double angle = 2 * Math.PI * i / 300;
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            gl.glVertex3d(x, y, 0.01);
        }
        gl.glEnd();*/
        
        GLU glu = GLU.createGLU(gl);
        GLUquadric quad = glu.gluNewQuadric();
        glu.gluDisk(quad, 4, 6, 20, 2);
    }
    
}
