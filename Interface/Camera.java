
package GA_Visualizer.Interface;

import static GA_Visualizer.Flags.*;

/**
 *
 * @author Habib
 */



public class Camera {
    
    private double posX;
    private double posY;
    private double posZ;
    private double atX;
    private double atY;
    private double atZ;
    private double upX;
    private double upY;
    private double upZ;
    
    private static final double effectiveViewInitPosZ = 3;
    private static final double moveValue = 0.2;
    
    public Camera() {
        
        posX = 0;
        posY = 4;
        posZ = 14;
        atX = 0;
        atY = 0;
        atZ = -10000;
        upX = 0;
        upY = 1;
        upZ = 0;
    }
    
    
    public void setPosX(double x) {
        
        posX = x;
    }
    
    
    public void setPosZ(double z) {
        
        posZ = z;
    }
    
    
    public double posX() {
        
        return posX;
    }
    
    
    public double posY() {
        
        return posY;
    }
    
    
    public double posZ() {
        
        return posZ;
    }
    
    
    public double atX() {
        
        return atX;
    }
    
    
    public double atY() {
        
        return atY;
    }
    
    
    public double atZ() {
        
        return atZ;
    }
    
    
    public double upX() {
        
        return upX;
    }
    
    
    public double upY() {
        
        return upY;
    }
    
    
    public double upZ() {
        
        return upZ;
    }
    
    public void moveRight() {
        
        posX += moveValue;
    }
    
    
    public void moveLeft() {
        
        posX -= moveValue;
    }
    
    
    public void moveUp() {
        
        posY += moveValue;
    }
    
    
    public void moveDown() {
        
        posY -= moveValue;
    }
    
    
    public void moveForward() {
        
        posZ -= moveValue;
    }
    
    
    public void moveBackward() {
        
        posZ += moveValue;
    }
    
    
    public static double effectiveViewInitPosZ() {
        
        return effectiveViewInitPosZ;
    }
    
    
    public void reset(int viewMode) {
        
        if(viewMode != EFFECTIVE_VIEW) {
	    posY = 4;
	    posZ = 14;
	    atX = 0;
	    atY = 0;
	    atZ = -1000;
	    upX = 0;
	    upY = 1;
	    upZ = 0;
	}
	else { 
            //Effective view
            posX = 2;
	    posY = 0;
	    posZ = effectiveViewInitPosZ;
	    atX = 0;
	    atY = 0;
	    atZ = -10000;
	    upX = 0;
	    upY = 1;
	    upZ = 0;
	}
    }
    
}







