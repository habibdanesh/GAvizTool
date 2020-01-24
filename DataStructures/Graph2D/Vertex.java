
package GA_Visualizer.DataStructures.Graph2D;

import com.jogamp.opengl.GL2;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Habib
 */



public class Vertex extends Point2D.Double implements Cloneable {
    
    private String name;
    private ArrayList<Integer> neighbors;
    
    
    public Vertex() {
        
        super();
        name = new String();
        neighbors = new ArrayList<>();
    }
    
    
    public Vertex(double x, double y) {
        
        super(x, y);
        name = new String();
        neighbors = new ArrayList<>();
    }
    
    
    public Vertex(Vertex other) {
        
        super();
        x = other.x;
        y = other.y;
        name = other.getName();
        
        neighbors = new ArrayList<>();
        if(other.numNeighbors() > 0) {
            for(int i=0; i<other.getNeighbors().size(); i++)
                neighbors.add(other.getNeighbors().get(i));
        }
    }
    
    
    @Override
    public Object clone() {
        
        return new Vertex(this);
    }
    
    
    public void setName(String name) {
        
        this.name = name;
    }
    
    
    public String getName() {
        
        return name;
    }
    
    
    public void addNeighbor(int other) {
        
        neighbors.add(other);
    }
    
    
    public ArrayList<Integer> getNeighbors() {
        
        return neighbors;
    }
    
    
    public int getNeighborAt(int i) {
        
        return neighbors.get(i);
    }
    
    
    public int numNeighbors() {
        
        return neighbors.size();
    }
    
    
    public void clearNeighbors() {
        
        neighbors.clear();
    }
    
    
    public boolean isNeighborWith(int other) {
        
        return neighbors.contains(other);
    }
    
    
    public int degree() {
        
        return neighbors.size();
    }
    
    
    public void draw(GL2 gl) {
        
        gl.glBegin(GL2.GL_POINTS);
            gl.glVertex2d(x, y);
        gl.glEnd();
    }
    
}
