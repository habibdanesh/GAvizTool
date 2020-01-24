
package GA_Visualizer.Interface;

import GA_Visualizer.Base_JOGL_Window;
import GA_Visualizer.DataStructures.Genome;
import GA_Visualizer.DataStructures.Graph2D.Vertex;
import GA_Visualizer.DataStructures.Population;
import static GA_Visualizer.Flags.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.sun.prism.impl.BufferUtil;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Habib
 */



public class InterfaceMain extends Base_JOGL_Window {
    
    private int width;
    private int height;
    
    private static final double fovy = 45;
    
    private ArrayList<Population> populations;
    private ArrayList<Vertex> customerCoords;
    private int numNodes;
    
    private Scene scene;
    private Camera cam;
    
    private int activeTower;
    private int activeSlab;
    private int activeWall;
    private int activeGenome;
    
    private int viewMode;
    
    private int cmd;
    private int mouseX;
    private int mouseY;
    
    private JPanel cPanel;
    
    private JLabel activePopLbl;
    private JLabel activeGenLbl;
    private JLabel activeClusterLbl;
    private JLabel activeGenomeLbl;
    private JTextArea genomeInfoText;
    
    private static boolean displayWinSize = true;

    
    public InterfaceMain(ArrayList<Population> pops, String fileName, int numNodes) 
            throws CloneNotSupportedException, FileNotFoundException {
        
        super("GA Visualizer", 1920, 1080);
        
        width = Toolkit.getDefaultToolkit().getScreenSize().width;
        height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setSize(width, height);
        
        this.numNodes = numNodes;
        
        populations = new ArrayList<>();
        customerCoords = new ArrayList<>();
        cam = new Camera();
        
        activeTower = 0;
        activeSlab = 0;
        activeWall = 0;
        
        viewMode = GENOME_VIEW; 
        
        cmd = UPDATE;
        
        populations = pops;
        ArrayList<Vertex> coords = readCustomerCoords(fileName);
        DynamicTower.setCustomerCoords(coords);
        
        scene = new Scene(populations);
        
        activeGenome = scene.getNumGenomesOfCluster(activeTower, activeSlab, 
                activeWall) - 1;
        
        cPanel = createControlPanel();
        getContentPane().add(cPanel, BorderLayout.EAST);
        
        setVisible(true);
    }
    
    
    @Override
    public void init(GLAutoDrawable drawable) {
        
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        
        gl.glClearColor(0, 0, 0, 1);
        gl.glViewport(0, 0, width, height);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
        gl.glEnable (GL2.GL_BLEND);
        gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double aspectRatio = (double) width/height;
        glu.gluPerspective(fovy, aspectRatio, 1, 1000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        cam.setPosX(scene.startPointX());
    }
    
    
    @Override
    public void draw(GL2 gl) {
        
        GLU glu = GLU.createGLU(gl);
        
        switch(cmd) {
            
            case UPDATE:
                drawScene(gl, glu);
                break;
                
            case SELECT:
                hitHandle(gl, glu);
                drawScene(gl, glu);
                break;
        }
    }
    
    
    public void drawScene(GL2 gl, GLU glu) {
        
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        glu.gluLookAt(cam.posX(), cam.posY(), cam.posZ(),
                  cam.atX(), cam.atY(), cam.atZ(),
                  cam.upX(), cam.upY(), cam.upZ());

        scene.setViewMode(viewMode);
        scene.draw(gl, viewMode, activeTower, activeSlab, activeWall, activeGenome);
        canvas.requestFocus();
    }

    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        
        if(displayWinSize) {
            System.out.println("Width:" + width + " , " + "Height:" + height);
            displayWinSize = false;
        }
        
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        
        this.width = width;
        this.height = height;
        
        gl.glClearColor(0, 0, 0, 1);
        gl.glViewport(0, 0, width, height);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double aspectRatio = (double) width/height;
        
        glu.gluPerspective(fovy, aspectRatio, 1, 1000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    
    @Override
    public void keyHandle(char key) {
        
        switch(key) {
            
            case 'q':
                System.exit(0);
                break;
                
            case 'n':
                scene.rotateLeft();
                break;

            case 'm':
                scene.rotateRight();
                break;
                
            case 'o':
                scene.decTowersDistance();
                break;

            case 'p':
                scene.incTowersDistance();
                break;
                
            case 'a':
                cam.moveLeft();
                break;

            case 'd':
                cam.moveRight();
                break;

            case 'x':
                cam.moveDown();
                break;

            case 'e':
                cam.moveUp();
                break;

            case 's':
                cam.moveBackward();
                break;

            case 'w':
                cam.moveForward();
                break;
        }
    }
    
    
    public void printPopulations() {
        
        for(int i=0; i<populations.size(); i++) {
            System.out.println("Population " + i + "\n");
            populations.get(i).print();
        }
    }
    
    
    private ArrayList<Vertex> readCustomerCoords(String fileName) 
            throws FileNotFoundException {
        
        try(Scanner scanner = new Scanner(new File(fileName))) {
            
            String strToFind;
            
            strToFind = scanner.next();
            while(!strToFind.equals("TIME")) strToFind = scanner.next();
            
            for(int j=0; j<4; j++) scanner.next(); //Skip
            
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            
            for(int i=0; i<numNodes; i++) {
                
                scanner.next(); //Skip
                
                int x = scanner.nextInt();
                int y = scanner.nextInt();
                
                if(x > maxX) maxX = x;
                if(y > maxY) maxY = y;
                
                Vertex v = new Vertex((double)x, (double)y);
                customerCoords.add(v);
                
                for(int j=0; j<4; j++) scanner.nextInt(); //Skip
            }
            
            //Normalize coords
            for(Vertex v : customerCoords) {
                v.x /= (double) maxX;
                v.y /= (double) maxY;
            }
        }
        
        return customerCoords;
    }
    
    
    public void printCustomerCoords() {
        
        System.out.println();
        
        for(int i=0; i<customerCoords.size(); i++) {
            Vertex v = customerCoords.get(i);
            System.out.print(i + ":  ");
            System.out.println(v.x + " " + v.y);
        }
        
        System.out.println();
    }
    
    
    private JPanel createControlPanel() {
        
        JPanel mainPnl;
        
        //Up-Panel
        JPanel clustFilePnl = new JPanel(new GridLayout(1, 2));
        JButton chooseFileBtn = new JButton("Choose File");
        clustFilePnl.add(new JLabel("Data:"));
        clustFilePnl.add(chooseFileBtn);
        
        String[] clustAlgorithms = {"SOM (GPU)","K-Means (CPU)"};
        JComboBox clustAlgoComboBox = new JComboBox(clustAlgorithms);
        clustAlgoComboBox.setSelectedIndex(0);
        JPanel clustAlgoPnl = new JPanel(new GridLayout(1, 2));
        clustAlgoPnl.add(new JLabel("Algorithm:"));
        clustAlgoPnl.add(clustAlgoComboBox);
        
        JButton rerunClustBtn = new JButton("Re-run");
                
        JPanel clusteringPnl = new JPanel(new BorderLayout(0, 5));
        clusteringPnl.setBorder(BorderFactory.createTitledBorder("Clustering"));
        clusteringPnl.add(clustFilePnl, BorderLayout.NORTH);
        clusteringPnl.add(clustAlgoPnl, BorderLayout.CENTER);
        clusteringPnl.add(rerunClustBtn, BorderLayout.SOUTH);
        
        JRadioButton symbolViewBtn = new JRadioButton("Symbol");
        symbolViewBtn.addActionListener((ActionEvent e) -> {
            viewMode = SYMBOL_VIEW;
            cam.reset(viewMode);
            cam.setPosX(scene.startPointX());
            canvas.repaint();
        });
        
        JRadioButton genomeViewBtn = new JRadioButton("Genome Distribution");
        genomeViewBtn.addActionListener((ActionEvent e) -> {
            viewMode = GENOME_VIEW;
            cam.reset(viewMode);
            cam.setPosX(scene.startPointX());
            canvas.repaint();
        });
        
        JRadioButton effectiveViewBtn = new JRadioButton("Phenotype");
        effectiveViewBtn.addActionListener((ActionEvent e) -> {
            viewMode = EFFECTIVE_VIEW;
            cam.reset(viewMode);
            canvas.repaint();
        });
        
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(symbolViewBtn);
        radioGroup.add(genomeViewBtn);
        radioGroup.add(effectiveViewBtn);
        
        JPanel viewModePnl = new JPanel(new GridLayout(3, 1));
        viewModePnl.setBorder(BorderFactory.createTitledBorder("View Mode"));
        viewModePnl.add(symbolViewBtn);
        viewModePnl.add(genomeViewBtn);
        viewModePnl.add(effectiveViewBtn);
        
        if(viewMode == SYMBOL_VIEW) symbolViewBtn.setSelected(true);
        if(viewMode == GENOME_VIEW) genomeViewBtn.setSelected(true);
        if(viewMode == EFFECTIVE_VIEW) effectiveViewBtn.setSelected(true);
        
        JPanel upPnl = new JPanel(new BorderLayout(0, 10));
        upPnl.add(new JPanel(), BorderLayout.NORTH);
        upPnl.add(clusteringPnl, BorderLayout.CENTER);
        upPnl.add(viewModePnl, BorderLayout.SOUTH);
        
        //Center-Panel
        activePopLbl = new JLabel(Integer.toString(activeTower+1));
        JPanel popNumPnl = new JPanel();
        popNumPnl.add(new JLabel("Population:"));
        popNumPnl.add(activePopLbl);
        
        activeGenLbl = new JLabel(Integer.toString(activeSlab+1));
        JPanel genNumPnl = new JPanel();
        genNumPnl.add(new JLabel("Generation:"));
        genNumPnl.add(activeGenLbl);
        
        activeClusterLbl = new JLabel(Integer.toString(activeWall+1));
        JPanel clusterNumPnl = new JPanel();
        clusterNumPnl.add(new JLabel("Cluster:"));
        clusterNumPnl.add(activeClusterLbl);
        
        activeGenomeLbl = new JLabel(Integer.toString(activeGenome+1));
        
        genomeInfoText = new JTextArea();
        genomeInfoText.setColumns(15);
        genomeInfoText.setEditable(false);
        genomeInfoText.setText(getActiveGenomeInfo());
        JScrollPane scrollPane = new JScrollPane(genomeInfoText);
        
        JButton prevBtn = new JButton("Previous");
        prevBtn.addActionListener((ActionEvent e) -> {
            if(activeGenome > 0) {
                activeGenome--;
                activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                genomeInfoText.setText(getActiveGenomeInfo());
            }
            else if(activeGenome == 0) {
                activeGenome = scene.getNumGenomesOfCluster(activeTower, 
                        activeSlab, activeWall) - 1;
                activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                genomeInfoText.setText(getActiveGenomeInfo());
            }
            canvas.repaint();
        });
        
        JButton nextBtn = new JButton("Next");
        nextBtn.addActionListener((ActionEvent e) -> {
            int numGenomes = scene.getNumGenomesOfCluster(activeTower, 
                        activeSlab, activeWall);
            if(activeGenome < numGenomes-1) {
                activeGenome++;
                activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                genomeInfoText.setText(getActiveGenomeInfo());
            }
            else if(activeGenome == numGenomes-1) {
                activeGenome = 0;
                activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                genomeInfoText.setText(getActiveGenomeInfo());
            }
            canvas.repaint();
        });
        
        JPanel genomeNumPnl = new JPanel();
        genomeNumPnl.add(prevBtn);
        genomeNumPnl.add(activeGenomeLbl);
        genomeNumPnl.add(nextBtn);
        
        JPanel activeGenomeNumsPnl = new JPanel(new GridLayout(4, 1));
        activeGenomeNumsPnl.add(popNumPnl);
        activeGenomeNumsPnl.add(genNumPnl);
        activeGenomeNumsPnl.add(clusterNumPnl);
        activeGenomeNumsPnl.add(genomeNumPnl);
        
        JPanel centerPnl = new JPanel(new BorderLayout(0, 10));
        centerPnl.setBorder(BorderFactory.createTitledBorder("Genome Info"));
        centerPnl.add(activeGenomeNumsPnl, BorderLayout.NORTH);
        centerPnl.add(scrollPane, BorderLayout.CENTER);
        
        //Down-Panel
        JPanel resetPnl = new JPanel(new GridLayout(1, 2));
        resetPnl.setBorder(BorderFactory.createTitledBorder("Reset"));
        
        String[] resetItems = {"Active Slab","Active Tower","Scene","Camera","All"};
        JComboBox resetComboBox = new JComboBox(resetItems);
        resetComboBox.setSelectedIndex(ACTIVE_TOWER);
        
        JButton resetBtn = new JButton("Apply");
        resetBtn.addActionListener((ActionEvent e) -> {
            int item = resetComboBox.getSelectedIndex();
            
            switch(item) {
                
                case ACTIVE_SLAB:
                    activeGenome = 0;
                    activeWall = 0;
                    activeClusterLbl.setText(Integer.toString(activeWall+1));
                    activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                    genomeInfoText.setText(getActiveGenomeInfo());
                    break;
                    
                case ACTIVE_TOWER:
                    activeGenome = 0;
                    activeWall = 0;
                    activeSlab = 0;
                    activeGenLbl.setText(Integer.toString(activeSlab+1));
                    activeClusterLbl.setText(Integer.toString(activeWall+1));
                    activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                    genomeInfoText.setText(getActiveGenomeInfo());
                    break;
                    
                case SCENE:
                    activeGenome = 0;
                    activeWall = 0;
                    activeSlab = 0;
                    scene.reset();
                    activePopLbl.setText(Integer.toString(activeTower+1));
                    activeGenLbl.setText(Integer.toString(activeSlab+1));
                    activeClusterLbl.setText(Integer.toString(activeWall+1));
                    activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                    genomeInfoText.setText(getActiveGenomeInfo());
                    break;
                    
                case CAMERA:
                    cam.reset(viewMode);
                    cam.setPosX(scene.startPointX());
                    break;
                    
                case ALL:
                    activeGenome = 0;
                    activeWall = 0;
                    activeSlab = 0;
                    scene.reset();
                    cam.reset(viewMode);
                    cam.setPosX(scene.startPointX());
                    activePopLbl.setText(Integer.toString(activeTower+1));
                    activeGenLbl.setText(Integer.toString(activeSlab+1));
                    activeClusterLbl.setText(Integer.toString(activeWall+1));
                    activeGenomeLbl.setText(Integer.toString(activeGenome+1));
                    genomeInfoText.setText(getActiveGenomeInfo());
                    break;
            }
            
            canvas.repaint();
        });
        
        resetPnl.add(resetComboBox);
        resetPnl.add(resetBtn);
        
        JButton quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        JPanel downPnl = new JPanel(new BorderLayout(0, 10));
        downPnl.add(resetPnl, BorderLayout.NORTH);
        downPnl.add(quitBtn, BorderLayout.CENTER);
        downPnl.add(new JPanel(), BorderLayout.SOUTH);
        
        //Main-Panel
        mainPnl = new JPanel(new BorderLayout(0, 30));
        mainPnl.add(upPnl, BorderLayout.NORTH);
        mainPnl.add(centerPnl, BorderLayout.CENTER);
        mainPnl.add(downPnl, BorderLayout.SOUTH);
        
        return mainPnl;
    }
    
    
    public String getActiveGenomeInfo() {
        
        String info = "";
        
        if(!scene.getPopulation(activeTower).getGeneration(activeSlab).getClusters().isEmpty()
                &&
                !scene.getPopulation(activeTower).getGeneration(activeSlab)
                        .getCluster(activeWall).isEmpty()) {
            
            Genome genome = scene.getPopulation(activeTower).getGeneration(activeSlab)
                        .getCluster(activeWall).getGenome(activeGenome);
            
            if(viewMode == SYMBOL_VIEW)
                scene.getPopulation(activeTower).getGeneration(activeSlab)
                        .getCluster(activeWall).getBestGenome();
            
            info = info.concat("Local Aggregate Fitness: ");
            info = info.concat(truncateDecimal(genome.getAggregateFitness(), 5)+ "\n\n");
            
            info = info.concat("No. of Routes: ");
            int numRoutes = genome.getNumRoutes();
            info = info.concat(Integer.toString(numRoutes));
            info = info.concat("\n\n");
            
            int geneCounter = 0;
            ArrayList<Integer> geneList = genome.getGenes();
            int numGenes = geneList.size();
            for(int i=0; i<numRoutes; i++) {
                
                info = info.concat("R" + (i+1) + ": ");
                info = info.concat(Integer.toString(geneList.get(geneCounter)));
                info = info.concat(" ");
                geneCounter++;
                
                while(geneCounter<numGenes && geneList.get(geneCounter)!=0) {
                    info = info.concat(Integer.toString(geneList.get(geneCounter)));
                    info = info.concat(" ");
                    geneCounter++;
                }
                
                info = info.concat(Integer.toString(numNodes) + "\n");
            }
            
            info = info.concat("\nTotal Distance: ");
            info = info.concat(Double.toString(genome.getTotalDistance()));
        }
        
        return info;
    }
    
    
    private static BigDecimal truncateDecimal(double x, int numberofDecimals) {
        
        if ( x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, 
                    BigDecimal.ROUND_FLOOR);
        } 
        else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, 
                    BigDecimal.ROUND_CEILING);
        }
    }

    
    @Override
    public void clickHandle(MouseEvent e) {
        
        mouseX = e.getX();
        mouseY = e.getY();
        
        if(SwingUtilities.isLeftMouseButton(e)) cmd = SELECT;
    }
    
    
    public void hitHandle(GL2 gl, GLU glu) {
        
        double x = (double) mouseX;
        double y = (double) mouseY;

        IntBuffer selBuffer = BufferUtil.newIntBuffer(64);
        DoubleBuffer projMatrix = BufferUtil.newDoubleBuffer(16);
        IntBuffer viewport = BufferUtil.newIntBuffer(4);

        gl.glSelectBuffer(64, selBuffer);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projMatrix);
        gl.glRenderMode(GL2.GL_SELECT);
        gl.glInitNames();
        gl.glPushName(0);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluPickMatrix((double)x, (double)(viewport.get(3)-y), 1, 1, viewport);
        gl.glMultMatrixd(projMatrix);

        gl.glMatrixMode(GL2.GL_MODELVIEW);

        drawScene(gl, glu);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();

        int hits = gl.glRenderMode(GL2.GL_RENDER);

        if(hits>0 && selBuffer.get(3)!=0) { //0 is the Surface name
            activeTower = selBuffer.get(3)-1;
            activeSlab = selBuffer.get(4)-1;
            activeWall = selBuffer.get(5)-1;
            activeGenome = scene.getNumGenomesOfCluster(activeTower, activeSlab, 
                    activeWall) - 1;
            
            activePopLbl.setText(Integer.toString(activeTower+1));
            activeGenLbl.setText(Integer.toString(activeSlab+1));
            activeClusterLbl.setText(Integer.toString(activeWall+1));
            activeGenomeLbl.setText(Integer.toString(activeGenome+1));
            genomeInfoText.setText(getActiveGenomeInfo());
        }

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        cmd = UPDATE;
    }
    
}
