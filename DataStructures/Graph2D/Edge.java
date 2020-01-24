
package GA_Visualizer.DataStructures.Graph2D;

import com.jogamp.opengl.GL2;
import java.awt.geom.Line2D;

/**
 *
 * @author Habib
 */

public class Edge extends Line2D.Double implements Cloneable {
    
    private int v1Index;
    private int v2Index;
    
    
    public Edge() {
        
        super();
    }
    
    
    public Edge(Edge other) {
        
        super();
        
        x1 = other.x1;
        x2 = other.x2;
        y1 = other.y1;
        y2 = other.y2;
        
        v1Index = other.v1Index;
        v2Index = other.v2Index;
    }
    
    
    public Edge(Vertex v1, Vertex v2) {
        
        super();
        
        setLine(v1, v2);
    }
    
    
    public Edge(int v1, int v2) {
        
        super();
        
        v1Index = v1;
        v2Index = v2;
    }
    
    
    @Override
    public Object clone() {
        
        return new Edge(this);
    }
    
    
    public void setV1Index(int v) {
        
        v1Index = v;
    }
    
    public int getV1Index() {
        
        return v1Index;
    }
    
    
    public void setV2Index(int v) {
        
        v2Index = v;
    }
    
    public int getV2Index() {
        
        return v2Index;
    }
    
    
    public void draw(GL2 gl) {
        
        gl.glBegin(GL2.GL_LINES);
            gl.glVertex3d(x1, y1, 0.01);
            gl.glVertex3d(x2, y2, 0.01);
        gl.glEnd();
    }
    
}
