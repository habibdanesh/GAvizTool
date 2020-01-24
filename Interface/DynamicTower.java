
package GA_Visualizer.Interface;

import GA_Visualizer.DataStructures.Cluster;
import GA_Visualizer.DataStructures.Generation;
import GA_Visualizer.DataStructures.Genome;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import GA_Visualizer.DataStructures.Population;
import static GA_Visualizer.Flags.GENOME_VIEW;
import static GA_Visualizer.Flags.GLOBAL_CLUSTER;
import static GA_Visualizer.Flags.LOCAL_CLUSTER;
import static GA_Visualizer.Flags.SYMBOL_VIEW;
import GA_Visualizer.ShapeCreator.Shape2D;
import com.jogamp.opengl.GL2;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class DynamicTower {
    
    private Population population;
    private static ArrayList<Vertex> customerCoords;
    private static Vertex centerNode;
    
    private int numSlabs;
    
    //Base Coords
    private static final double baseDownY = 0;
    private static final double baseUpY = 0.05;
    
    //Walls Coords
    private static final double wallWAndH = 1.2; //Width & Height
    private static final double wallDownY = baseUpY;
    private static final double wallUpY = wallDownY + wallWAndH;
    private static final double margin = 0.1 * wallWAndH;
    private static final double drawableLength = wallWAndH - (2*margin);
    
    //Roof Coords
    private static final double roofDownY = wallUpY;
    private static final double roofUpY = roofDownY + 0.05;
    
    //Boundary Coords
    private static final double boundDownY = wallDownY;
    private static final double boundUpY = wallUpY;
    
    private static final double slabHeight = roofUpY - baseDownY;
    
    private static int viewMode = SYMBOL_VIEW;
    private static int genomeColoringMode = LOCAL_CLUSTER;
    
    public DynamicTower(Population pop) {
        
        population = pop;
        numSlabs = population.numGenerations();
        
        //Init genomes
        for(Generation generation : population.getGenerations()) {
            for(Cluster cluster : generation.getClusters())
                if(!cluster.isEmpty()) initGenomes(cluster);
        }
    }
    
    
    public void draw(GL2 gl, boolean isActiveTower, int activeSlab, int activeWall, 
            int activeGenome) {
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        
        //Draw slabs
        for(int i=0; i<numSlabs; i++) {
            
            boolean isActiveSlab = false;
            if (i == activeSlab) isActiveSlab = true;
            
            //Draw walls
            Generation generation = population.getGeneration(i);
            gl.glPushName(i+1);
            
            gl.glPushMatrix();
            
            double xPos = (generation.numEmptyClusters()*wallWAndH) / 2;
            gl.glTranslated(xPos, 0, 0);
            
            for(int j=0; j<generation.numClusters(); j++) {
                
                boolean isActiveWall = false;
                if(j == activeWall) isActiveWall = true;
                
                Cluster cluster = generation.getCluster(j);
                if(!cluster.isEmpty()) {
                    drawBase(gl, isActiveTower, isActiveSlab);
                    drawWall(gl, j, isActiveTower, isActiveSlab, isActiveWall);
                    if(viewMode == SYMBOL_VIEW)
                        drawSymbols(cluster, gl);
                    else if(viewMode == GENOME_VIEW)
                        drawGenomes(cluster, gl, isActiveTower, isActiveSlab, isActiveWall, activeGenome);
                    drawRoof(gl, isActiveTower, isActiveSlab);
                    drawBoundary(gl, isActiveTower);
                    gl.glTranslated(wallWAndH, 0, 0);
                }
            }
            
            gl.glPopName();
            gl.glPopMatrix();
            gl.glTranslated(0, slabHeight, 0); //Move up
        }
        
        gl.glPopMatrix();
    }
    
    
    private void drawWall(GL2 gl, int index, boolean isActiveTower, 
            boolean isActiveSlab, boolean isActiveWall) {
        
        gl.glPushName(index+1);
        
        if(isActiveTower && isActiveSlab && isActiveWall) 
            gl.glColor3d(0.255, 0.41, 0.882);
        else
            gl.glColor3d(0.373, 0.620, 0.627);
        
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(0, wallDownY);
            gl.glVertex2d(wallWAndH, wallDownY);
            gl.glVertex2d(wallWAndH, wallUpY);
            gl.glVertex2d(0, wallUpY);
        gl.glEnd();
        
        gl.glPopName();
    }
    
    
    private void drawBase(GL2 gl, boolean isActiveTower, boolean isActiveSlab) {
        
        if (isActiveTower && isActiveSlab) 
            gl.glColor3d(1, 0, 0);
        else 
            gl.glColor3d(0.275, 0.510, 0.706);
        
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(0, baseDownY);
            gl.glVertex2d(wallWAndH, baseDownY);
            gl.glVertex2d(wallWAndH, baseUpY);
            gl.glVertex2d(0, baseUpY);
        gl.glEnd();
    }
    
    
    private void drawRoof(GL2 gl, boolean isActiveTower, boolean isActiveSlab) {
        
        if (isActiveTower && isActiveSlab) 
            gl.glColor3d(1, 0, 0);
        else 
            gl.glColor3d(0.275, 0.510, 0.706);
        
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex2d(0, roofDownY);
            gl.glVertex2d(wallWAndH, roofDownY);
            gl.glVertex2d(wallWAndH, roofUpY);
            gl.glVertex2d(0, roofUpY);
        gl.glEnd();
    }
    
    
    private void drawBoundary(GL2 gl, boolean isActiveTower) {
        
        gl.glLineWidth(1);
        
        if (isActiveTower) 
            gl.glColor3d(1, 0, 0);
        else 
            gl.glColor3d(0.690, 0.769, 0.871);
        
        //Left boundary
        gl.glBegin(GL2.GL_LINES);
            gl.glVertex3d(0, boundDownY, 0.001);
            gl.glVertex3d(0, boundUpY, 0.001);
        gl.glEnd();
        
        //Right boundary
        gl.glBegin(GL2.GL_LINES);
            gl.glVertex3d(wallWAndH, boundDownY, 0.001);
            gl.glVertex3d(wallWAndH, boundUpY, 0.001);
        gl.glEnd();
    }
    
    
    private void drawSymbols(Cluster cluster, GL2 gl) {
                
        Shape2D symbol = cluster.getSymbol();
        symbol.draw(gl);
    }
    
    
    private void drawGenomes(Cluster cluster, GL2 gl, boolean isActiveTower, 
            boolean isActiveSlab, boolean isActiveWall, int activeGenome) {
                
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();

        //Move to the origin of the drawable area
        gl.glTranslated(margin, margin, 0);

        for(int j=0; j<cluster.numGenomes(); j++) {

            Genome genome = cluster.getGenome(j);

            boolean isActiveGenome = false;
            if(isActiveTower && isActiveSlab && isActiveWall && j==activeGenome) 
                isActiveGenome = true;

            gl.glPushMatrix();
            Vertex origin = genome.getOrigin();

            gl.glTranslated(origin.x, origin.y, 0);

            double scaleRate = 0.006;
            gl.glScaled(scaleRate, scaleRate, scaleRate);

            gl.glTranslated(0, 0, 0.02);
            //genome.drawPhenotype(gl, isActiveGenome);
            genome.drawAsCircle(gl, isActiveGenome);
            gl.glPopMatrix();
        }

        gl.glPopMatrix();
    }
    
    
    public final void initGenomes(Cluster cluster) {
        
        //Initiate genome's graph
        for(Genome genome : cluster.getGenomes()) 
            genome.createGraph(customerCoords);

        //Set genomes color
        if(genomeColoringMode == LOCAL_CLUSTER) {
            cluster.sort();
            for(Genome genome : cluster.getGenomes()) {
                double f = genome.getAggregateFitness();
                double[] color = {1-f, 1-f, 1-f};
                genome.setColor(color);
            }
        }
        else if(genomeColoringMode == GLOBAL_CLUSTER) {
            //double f = cluster.calcAvgFitness();
            double f = cluster.getBestGenome().getAggregateFitness();
            double[] color = {1-f, 1-f, 1-f};
            for(Genome genome : cluster.getGenomes()) genome.setColor(color);
            cluster.sort();
        }

        //Set the origin
        if(cluster.numGenomes() == 1) {
            Genome genome = cluster.getGenome(0);
            double inRangeObj1 = drawableLength / 2;
            double inRangeObj2 = drawableLength / 2;
            Vertex origin = new Vertex(inRangeObj1, inRangeObj2);
            genome.setOrigin(origin);
        }
        else {
            for(Genome genome : cluster.getGenomes()) {

                double fitness1 = genome.getFitnessScore(0);
                double fitness2 = genome.getFitnessScore(1);

                double inRangeObj1 = fitness1 * drawableLength;
                double inRangeObj2 = fitness2 * drawableLength;

                Vertex origin = new Vertex(inRangeObj1, inRangeObj2);
                genome.setOrigin(origin);
            }
        }
    }
    
    
    public static void setCustomerCoords(ArrayList<Vertex> coords) {
        
        customerCoords = coords;
    }
    
    
    public static void setViewMode(int mode) {
        
        viewMode = mode;
    }
    
    
    public Vertex findCenterNode() {
        
        //Calculate the center-node
        centerNode = new Vertex();
        
        int numNodes = customerCoords.size();
        
        //Find the longest distance among the distances between each two point
        double longestDistance = 0;
        Vertex longestV1 = new Vertex();
        Vertex longestV2 = new Vertex();
        
        for(int i=0; i<numNodes-1; i++) {
            
            Vertex v1 = customerCoords.get(i);
            
            for(int j=i+1; j<numNodes; j++) {
                
                Vertex v2 = customerCoords.get(j);
                double distance = sqrt(pow((v2.x-v1.x), 2)+pow((v2.y-v1.y), 2));
                
                if(distance > longestDistance) {
                    longestDistance = distance;
                    longestV1 = v1;
                    longestV2 = v2;
                }
            }
        }
        
        //Mid-Point
        centerNode.x = (longestV1.x + longestV2.x) / 2.0;
        centerNode.y = (longestV1.y + longestV2.y) / 2.0;
        
        return centerNode;
    }
    
}
