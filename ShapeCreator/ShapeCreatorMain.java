
package GA_Visualizer.ShapeCreator;

import GA_Visualizer.Base_JOGL_Window;
import GA_Visualizer.DataStructures.Graph2D.Edge;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import static GA_Visualizer.Flags.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author Habib
 */



public class ShapeCreatorMain extends Base_JOGL_Window {
    
    private static final int width = 1280;
    private static final int height = 720;
    
    private int cWidth;
    private int cHeight;
    
    private static boolean displayWinSize = true;
    
    private Shape2D shape;
    private int activeVertex;
    private int v1;
    
    Stack<Integer> undoTrash;
    Stack<Integer> redoTrash;
    Stack<Vertex> vertexRedoTrash;
    Stack<Integer> v1RedoTrash;
    Stack<Edge> edgeRedoTrash;

    private int gridMode;
    
    private JLabel numVerticesLbl;
    private JLabel numEdgesLbl;
    private JButton newBtn;
    private JButton v1Btn;
    private JButton edgeBtn;
    private JButton undoBtn;
    private JButton redoBtn;
    private JButton resetBtn;
    private JButton saveBtn;
    private JTextField shapeNameText;
    private JButton nameBtn;
            

    public ShapeCreatorMain() {
        
        super("Shape Creator", width, height);
        
        cWidth = width;
        cHeight = height;
        setResizable(false);
        
        v1 = -1;
        activeVertex = 0;
        shape = new Shape2D();
        shape.setActiveVertex(activeVertex);
        
        undoTrash = new Stack<>();
        redoTrash = new Stack<>();
        vertexRedoTrash = new Stack<>();
        v1RedoTrash = new Stack<>();
        edgeRedoTrash = new Stack<>();
        
        gridMode = GRID_10x10;
        
        
        JPanel cPanel = createControlPanel();
        getContentPane().add(cPanel, BorderLayout.EAST);
        
        undoBtn.setEnabled(false);
        redoBtn.setEnabled(false);
        v1Btn.setEnabled(false);
        edgeBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    @Override
    public void draw(GL2 gl) {
    
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        gl.glTranslated(-(cWidth/2), -(cHeight/2), 0);
        
        drawGrid(gl);
        
        gl.glPointSize(10);
        shape.drawVertices(gl, v1);
        
        gl.glLineWidth(1);
        shape.drawEdges(gl);
        
        numVerticesLbl.setText("Vertices: " + shape.numVertices());
        numEdgesLbl.setText("Edges: " + shape.numEdges());
        canvas.requestFocus();
    }

    
    @Override
    public void keyHandle(char key) {
    
        switch (key) {
            
            case 'q': //Quit
                System.exit(0);
                break;

            case 'm': //Next vertex
                activeVertex++;
                if(activeVertex>0 && activeVertex<shape.numVertices())
                    shape.setActiveVertex(activeVertex);
                else
                    activeVertex--;
                break;

            case 'n': //Previous vertex
                activeVertex--;
                if(activeVertex>=0 && activeVertex<shape.numVertices())
                    shape.setActiveVertex(activeVertex);
                else
                    activeVertex++;
                break;

            case 'a': //Move left
                shape.moveActiveVertexLeft();
                break;

            case 'd': //Move right
                shape.moveActiveVertexRight();
                break;

            case 's': //Move down
                shape.moveActiveVertexDown();
                break;

            case 'w': //Move up
                shape.moveActiveVertexUp();
                break;
        }
    }

    
    @Override
    public void clickHandle(MouseEvent e) {
    
        if(SwingUtilities.isLeftMouseButton(e)) {
            
            Vertex v = new Vertex();
            v.x = e.getX();
            v.y = cHeight-e.getY();
            shape.addVertex(v);

            undoTrash.push(DRAW_VERTEX);
            undoBtn.setEnabled(true);
            v1Btn.setEnabled(true);

            canvas.repaint();
        }
    }

    
    @Override
    public void init(GLAutoDrawable drawable) {
    
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glClearColor(0.690f, 0.769f, 0.871f, 1);
        gl.glViewport(0, 0, width, height);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-width/2, width/2, -height/2, height/2, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    
        if(displayWinSize) {
            System.out.println("Width:" + width + " , " + "Height:" + height);
            displayWinSize = false;
        }
        
        GL2 gl = drawable.getGL().getGL2();
        
        cWidth = width;
        cHeight = height;
        
        gl.glClearColor(0.690f, 0.769f, 0.871f, 1);
        gl.glViewport(0, 0, width, height);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-width/2, width/2, -height/2, height/2, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
    
    
    private JPanel createControlPanel() {
        
        JPanel mainPnl;
        
        int vertGap = 30;
        
        //Up-Panel
        numVerticesLbl = new JLabel("Vertices: " + shape.numVertices());
        numEdgesLbl = new JLabel("Edges: " + shape.numEdges());
        JPanel numsPnl = new JPanel(new BorderLayout());
        numsPnl.add(numVerticesLbl, BorderLayout.NORTH);
        numsPnl.add(numEdgesLbl, BorderLayout.CENTER);
        numsPnl.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        newBtn = new JButton("New");
        newBtn.addActionListener((ActionEvent e) -> {
            v1 = -1;
            activeVertex = 0;
            shape = new Shape2D();
            shape.setActiveVertex(activeVertex);
            
            undoTrash.clear();
            redoTrash.clear();
            vertexRedoTrash.clear();
            v1RedoTrash.clear();
            edgeRedoTrash.clear();

            edgeBtn.setEnabled(false);
	    v1Btn.setEnabled(false);
            undoBtn.setEnabled(false);
            redoBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            shapeNameText.setEnabled(true);
            nameBtn.setEnabled(true);
            
            canvas.repaint();
        });
        
        JRadioButton grid5Btn = new JRadioButton("5x5");
        grid5Btn.addActionListener((ActionEvent e) -> {
            gridMode = GRID_5x5;
            canvas.repaint();
        });
        
        JRadioButton grid10Btn = new JRadioButton("10x10");
        grid10Btn.addActionListener((ActionEvent e) -> {
            gridMode = GRID_10x10;
            canvas.repaint();
        });
        grid10Btn.setSelected(true);
        
        JRadioButton grid20Btn = new JRadioButton("20x20");
        grid20Btn.addActionListener((ActionEvent e) -> {
            gridMode = GRID_20x20;
            canvas.repaint();
        });
        
        JRadioButton grid40Btn = new JRadioButton("40x40");
        grid40Btn.addActionListener((ActionEvent e) -> {
            gridMode = GRID_40x40;
            canvas.repaint();
        });
        
        JRadioButton gridDisableBtn = new JRadioButton("Disable");
        gridDisableBtn.addActionListener((ActionEvent e) -> {
            gridMode = GRID_DISABLE;
            canvas.repaint();
        });
        
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(grid5Btn);
        radioGroup.add(grid10Btn);
        radioGroup.add(grid20Btn);
        radioGroup.add(grid40Btn);
        radioGroup.add(gridDisableBtn);
        
        JPanel gridPnl = new JPanel(new GridLayout(5, 1));
        gridPnl.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Grid"));
        gridPnl.add(grid5Btn);
        gridPnl.add(grid10Btn);
        gridPnl.add(grid20Btn);
        gridPnl.add(grid40Btn);
        gridPnl.add(gridDisableBtn);
        
        JPanel upPnl = new JPanel(new BorderLayout(0, vertGap));
        upPnl.add(numsPnl, BorderLayout.NORTH);
        upPnl.add(newBtn, BorderLayout.CENTER);
        upPnl.add(gridPnl, BorderLayout.SOUTH);
        
        //Center-Panel
        v1Btn = new JButton("Set V1");
        v1Btn.addActionListener((ActionEvent e) -> {
            v1 = activeVertex;
            undoTrash.push(SET_V1);
            v1Btn.setEnabled(false);
            
            if(shape.numVertices() > 1) edgeBtn.setEnabled(true);
            
            canvas.repaint();
        });
        
        edgeBtn = new JButton("Set Edge");
        edgeBtn.addActionListener((ActionEvent e) -> {
            shape.addEdge(v1, activeVertex);
            undoTrash.push(SET_EDGE);
            v1 = -1;
            
            edgeBtn.setEnabled(false);
            v1Btn.setEnabled(true);
            
            canvas.repaint();
        });
        
        JPanel edgePnl = new JPanel(new GridLayout(2, 1));
        edgePnl.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Add Edge"));
        edgePnl.add(v1Btn);
        edgePnl.add(edgeBtn);
        
        undoBtn = new JButton("Undo");
        undoBtn.addActionListener((ActionEvent e) -> {
            int lastOperation = undoTrash.pop();
            
            switch(lastOperation) {
                
                case DRAW_VERTEX:
                    redoTrash.add(DRAW_VERTEX);
                    vertexRedoTrash.add(shape.getLatestVertex());
                    
                    if(shape.numVertices()>1 && activeVertex+1==shape.numVertices())
                            activeVertex--;
                    
                    shape.setActiveVertex(activeVertex);
                    shape.removeLatestVertex();
                    break;
                    
                case SET_V1:
                    redoTrash.push(SET_V1);
                    v1RedoTrash.push(v1);
                    
                    v1 = -1;
                    
                    v1Btn.setEnabled(true);
                    edgeBtn.setEnabled(false);
                    break;
                    
                case SET_EDGE:
                    redoTrash.push(SET_EDGE);
                    edgeRedoTrash.push(shape.getLatestEdge());
                    shape.removeLatestEdge();
                    
                    v1Btn.setEnabled(false);
                    edgeBtn.setEnabled(true);
                    break;
            }
            
            redoBtn.setEnabled(true);
            if(shape.numVertices() == 0) undoBtn.setEnabled(false);
            
            canvas.repaint();
        });
        
        redoBtn = new JButton("Redo");
        redoBtn.addActionListener((ActionEvent e) -> {
            int lastUndo = redoTrash.pop();
           
            switch(lastUndo) {
               
                case DRAW_VERTEX:
                    undoTrash.push(DRAW_VERTEX);
                    shape.addVertex(vertexRedoTrash.pop());
                    break;
                   
                case SET_V1:
                    undoTrash.push(SET_V1);
                    v1 = v1RedoTrash.pop();
                   
                    v1Btn.setEnabled(false);
                    if(shape.numVertices() > 1) edgeBtn.setEnabled(true);
                    break;
                   
                case SET_EDGE:
                    undoTrash.push(SET_EDGE);
                    shape.addEdge(edgeRedoTrash.pop());
                   
                    v1Btn.setEnabled(true);
                    edgeBtn.setEnabled(false);
                    break;
            }
            
            undoBtn.setEnabled(true);
            if(redoTrash.isEmpty()) redoBtn.setEnabled(false);
            
            canvas.repaint();
        });
        
        JPanel undoRedoPnl = new JPanel(new GridLayout(2, 1));
        undoRedoPnl.add(undoBtn);
        undoRedoPnl.add(redoBtn);
        
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener((ActionEvent e) -> {
            v1 = -1;
            activeVertex = 0;
            shape = new Shape2D();
            shape.setActiveVertex(activeVertex);
            
            undoTrash.clear();
            redoTrash.clear();
            vertexRedoTrash.clear();
            v1RedoTrash.clear();
            edgeRedoTrash.clear();

            edgeBtn.setEnabled(false);
	    v1Btn.setEnabled(false);
            undoBtn.setEnabled(false);
            redoBtn.setEnabled(false);
            
            canvas.repaint();
        });
        
        JPanel centerPnl = new JPanel(new BorderLayout(0, vertGap));
        centerPnl.add(edgePnl, BorderLayout.NORTH);
        centerPnl.add(undoRedoPnl, BorderLayout.CENTER);
        centerPnl.add(resetBtn, BorderLayout.SOUTH);
        
        //Down-Panel
        shapeNameText = new JTextField(10);
        shapeNameText.setToolTipText("Name of Output File");
        
        nameBtn = new JButton("Set Name");
        nameBtn.addActionListener((ActionEvent e) -> {
            nameBtn.setEnabled(false);
            saveBtn.setEnabled(true);
            shapeNameText.setEnabled(false);
        });
        
        JPanel namePnl = new JPanel(new BorderLayout());
        namePnl.add(shapeNameText, BorderLayout.NORTH);
        namePnl.add(nameBtn, BorderLayout.CENTER);
        
        saveBtn = new JButton("Save");
        saveBtn.addActionListener((ActionEvent e) -> {
            String name1 = shapeNameText.getText();
            try {
                saveShape(name1);
            }
            catch (IOException ex) {
                Logger.getLogger(ShapeCreatorMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            saveBtn.setEnabled(false);
            v1Btn.setEnabled(false);
            edgeBtn.setEnabled(false);
            undoBtn.setEnabled(false);
            redoBtn.setEnabled(false);
            resetBtn.setEnabled(false);
        });
        
        JPanel savePnl = new JPanel(new BorderLayout());
        savePnl.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Save to File"));
        savePnl.add(namePnl, BorderLayout.NORTH);
        savePnl.add(new JPanel(), BorderLayout.CENTER);
        savePnl.add(saveBtn, BorderLayout.SOUTH);
        
        JButton quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        JPanel downPnl = new JPanel(new BorderLayout(0, vertGap));
        downPnl.add(savePnl, BorderLayout.NORTH);
        downPnl.add(quitBtn, BorderLayout.CENTER);
        downPnl.add(new JPanel(), BorderLayout.SOUTH);
        
        //Main-Panel
        mainPnl = new JPanel(new BorderLayout(0, vertGap));
        mainPnl.add(upPnl, BorderLayout.NORTH);
        mainPnl.add(centerPnl, BorderLayout.CENTER);
        mainPnl.add(downPnl, BorderLayout.SOUTH);
        
        return mainPnl;
    }
    
    
    private void drawGrid(GL2 gl) {
        
        gl.glColor3d(1, 1, 1);
	gl.glLineWidth(0.01f);
        
        switch(gridMode) {
            
            case GRID_5x5: {
                double w = cWidth / 5;
                double h = cHeight / 5;

                for(int i=1; i<5; i++) {
                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(0, i*h);
                        gl.glVertex2d(cWidth, i*h);
                    gl.glEnd();

                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(i*w, 0);
                        gl.glVertex2d(i*w, cHeight);
                    gl.glEnd();				
                }
                
                break;
            }
                
            case GRID_10x10: {
                double w = cWidth / 10;
                double h = cHeight / 10;

                for(int i=1; i<10; i++) {
                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(0, i*h);
                        gl.glVertex2d(cWidth, i*h);
                    gl.glEnd();

                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(i*w, 0);
                        gl.glVertex2d(i*w, cHeight);
                    gl.glEnd();				
                }
                
                break;
            }
            
            case GRID_20x20: {
                double w = cWidth / 20;
                double h = cHeight / 20;

                for(int i=1; i<20; i++) {
                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(0, i*h);
                        gl.glVertex2d(cWidth, i*h);
                    gl.glEnd();

                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(i*w, 0);
                        gl.glVertex2d(i*w, cHeight);
                    gl.glEnd();				
                }
                
                break;
            }
            
            case GRID_40x40: {
                double w = cWidth / 40;
                double h = cHeight / 40;

                for(int i=1; i<40; i++) {
                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(0, i*h);
                        gl.glVertex2d(cWidth, i*h);
                    gl.glEnd();

                    gl.glBegin(GL2.GL_LINES);
                        gl.glVertex2d(i*w, 0);
                        gl.glVertex2d(i*w, cHeight);
                    gl.glEnd();				
                }
                
                break;
            }
        }
    }
    
    
    private void saveShape(String name) throws IOException {
        
        File file = new File("shapes/" + name);
        file.createNewFile();
        
        try (FileWriter writer = new FileWriter(file)) {
            
            writer.write("Shape: " + name + "\n\n");
            writer.write("No. of Vertices: ");
            writer.write(shape.numVertices() + "\n");
            
            for(int i=0; i<shape.numVertices(); i++) {
                Vertex v = shape.getVertexAt(i);
                
                if(i < 10) writer.write("v" + i + ":   ");
                else writer.write("v" + i + ":  ");
                
                writer.write((int)v.x + " , " + (int)v.y + "\n");
            }
            
            writer.write("\n");
            writer.write("No. of Edges: ");
            writer.write(shape.numEdges()+ "\n");
            
            for(int i=0; i<shape.numEdges(); i++) {
                Edge e = shape.getEdgeAt(i);
                
                if(i < 10) writer.write("e" + i + ":   ");
                else writer.write("e" + i + ":  ");
                
                writer.write("v" + e.getV1Index() + " , " + "v" + e.getV2Index());
                writer.write("\n");
            }
            
            writer.flush();
        }
    }
    
    
    public static void main(String[] args) {
        
        ShapeCreatorMain shapeCreator = new ShapeCreatorMain();
    }
      
}
