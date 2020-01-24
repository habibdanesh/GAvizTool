
package GA_Visualizer.Interface;

import GA_Visualizer.DataStructures.Cluster;
import GA_Visualizer.DataStructures.Genome;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import GA_Visualizer.DataStructures.Population;
import static GA_Visualizer.Flags.*;
import com.jogamp.opengl.GL2;
import static java.lang.Math.ceil;
import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class Scene {
    
    private int numTowers;
    private static double towersDistance = 5;
    private static final double towersMoveVal = 0.2;
    private double rotationAngle;
    private static final double rotationVal = 0.5;
    private static final double surfaceRadius = 1000;
    private static final double surfaceY = 0;
    private double startPointX;
    private int viewMode;
    
    //Genomes distance in effective view
    private static final double effectiveViewGenomeDistance = 15;
    
    private ArrayList<DynamicTower> dynamicTowers;
    private ArrayList<Population> populations;
    
    
    public Scene(ArrayList<Population> populations) throws CloneNotSupportedException {
        
        this.populations = populations;
        
        numTowers = populations.size();
        
        rotationAngle = 0;
        startPointX = (double) -(ceil(sqrt(numTowers)) + 1);
        viewMode = SYMBOL_VIEW;
        
        dynamicTowers = new ArrayList<>();
        for(int i=0; i<numTowers; i++)
            dynamicTowers.add(new DynamicTower(populations.get(i)));
    }
    
    
    public double startPointX() {
        
        return startPointX;
    }
    
    
    public void setViewMode(int mode) {
        
        viewMode = mode;
        DynamicTower.setViewMode(mode);
    }
    
    
    public DynamicTower getTower(int index) {
        
        return dynamicTowers.get(index);
    }
    
    
    public Population getPopulation(int index) {
        
        return populations.get(index);
    }
    
    
    public void draw(GL2 gl, int viewMode, int activeTower, int activeSlab, 
            int activeWall, int activeGenome) {
        
        if(viewMode != EFFECTIVE_VIEW) {
            
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glRotated(rotationAngle, 0, 1, 0);
            gl.glTranslated(startPointX, 0, 0);
            
            //Calculate length of the rows and columns (square drawing area)
            int sideLength = (int) ceil(sqrt(numTowers));
            int towerCounter = 0;
            
            for(int i=0; i<sideLength; i++) {
                
                if(towerCounter == numTowers) break;
                gl.glPushMatrix();
                
                for(int j=0; j<sideLength; j++) {
                    
                    if(towerCounter == numTowers) break;
                    gl.glLoadName(towerCounter+1);
                    //PentagonTower tower = towers.get(towerCounter);
                    DynamicTower tower = dynamicTowers.get(towerCounter);
                    
                    if(j > FIRST_TOWER) gl.glTranslated(towersDistance, 0, 0);
                    
                    if(towerCounter == activeTower)
                        tower.draw(gl, true, activeSlab, activeWall, activeGenome);
                    else
                        tower.draw(gl, false, activeSlab, activeWall, activeGenome);
                    
                    towerCounter++;
                }
                
                gl.glPopMatrix();
                gl.glTranslated(0, 0, -towersDistance);
            }
            
            gl.glLoadName(0);
            drawSurface(gl);
            
            gl.glPopMatrix();
        }
        else { //Effective view
            
            Cluster cluster = populations.get(activeTower).getGeneration(activeSlab)
                    .getCluster(activeWall);
            
            Vertex center = dynamicTowers.get(activeTower).findCenterNode();
            double posZ = -1;
                
            for(int i=0; i<cluster.numGenomes(); i++) {
                
                Genome genome = cluster.getGenome(i);
                boolean isActiveGenome = false;
                if(i == activeGenome) isActiveGenome = true;
                        
                gl.glMatrixMode(GL2.GL_MODELVIEW);
                gl.glPushMatrix();
                gl.glTranslated(0, 0, posZ);
                gl.glTranslated(-(center.x/15.0), -(center.y/20), 0);
                
                genome.drawPhenotype(gl, isActiveGenome);
                
                posZ -= effectiveViewGenomeDistance;
                gl.glPopMatrix();
            }
        }
    }
    
    
    public void drawSurface(GL2 gl) {
        
        gl.glColor3d(0.439, 0.502, 0.565);
        gl.glBegin(GL2.GL_POLYGON);
            gl.glVertex3d(-surfaceRadius, surfaceY, surfaceRadius);
            gl.glVertex3d(surfaceRadius, surfaceY, surfaceRadius);
            gl.glVertex3d(surfaceRadius, surfaceY, -surfaceRadius);
            gl.glVertex3d(-surfaceRadius, surfaceY, -surfaceRadius);
        gl.glEnd();
    }
    
    
    public void rotateLeft() {
        
        rotationAngle -= rotationVal;
    }
    
    
    public void rotateRight() {
        
        rotationAngle += rotationVal;
    }
    
    
    public void reset() {
        
        rotationAngle = 0;
        towersDistance = 5;
    }
    
    
    public void incTowersDistance() {
        
        towersDistance += towersMoveVal;
    }
    
    
    public void decTowersDistance() {
        
        towersDistance -= towersMoveVal;
    }
    
    
    public int getNumGenomesOfCluster(int activeTower, int activeSlab, 
            int activeWall) {
        
        return populations.get(activeTower).getGeneration(activeSlab).
                getCluster(activeWall).numGenomes();
    }
    
}
