
package GA_Visualizer;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.awt.GLCanvas;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

/**
 *
 * @author Habib
 */



public abstract class Base_JOGL_Window extends JFrame 
        implements GLEventListener, KeyListener, MouseListener {
    
    protected GLCanvas canvas;
    
    
    public Base_JOGL_Window(String title, int width, int height) {
        
        super(title);
        
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setDoubleBuffered(true);
        
        canvas = new GLCanvas(capabilities);
        canvas.addGLEventListener(this);
        canvas.addMouseListener(this);
        canvas.addKeyListener(this);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(canvas, BorderLayout.CENTER);
        
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        canvas.requestFocusInWindow();
    }
    
    
    @Override
    public void display(GLAutoDrawable drawable) {
        
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        
        draw(gl);
        
        gl.glFlush();
    }
    
    
    public abstract void draw(GL2 gl);
    
    
    @Override
    public void dispose(GLAutoDrawable drawable) {}
    
    
    @Override
    public void keyTyped(KeyEvent e) {}

    
    @Override
    public void keyPressed(KeyEvent e) {
        
        keyHandle(e.getKeyChar());
        canvas.repaint();
    }
    
    
    public abstract void keyHandle(char key);
    

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}
    
    
    public abstract void clickHandle(MouseEvent e);
    

    @Override
    public void mousePressed(MouseEvent e) {
    
        clickHandle(e);
        canvas.repaint();
    }
    

    @Override
    public void mouseReleased(MouseEvent e) {}
    

    @Override
    public void mouseEntered(MouseEvent e) {}
    

    @Override
    public void mouseExited(MouseEvent e) {}
    
}
