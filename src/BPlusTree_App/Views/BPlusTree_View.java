///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Views
// File:             BPlusTree_View.java
//
// Title:            B+ fák működésének szemléltetése grafikus programmal
//
// Author:           Balla Zoltán   (All rights reserved.)
// University:       Eötvös Loránd Tudományegyetem
// Department:       Programtervező Informatikus BSc 
// Tutor:            Dr. Ásványi Tibor 
// Semester:         Spring 2018/2019
//
///////////////////////////////////////////////////////////////////////////////

package BPlusTree_App.Views;

import BPlusTree_App.Models.AnimatedBubble;
import BPlusTree_App.Models.BPlusTree_Model;
import BPlusTree_App.Models.ListOfChanges;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * The BPlusTree_View is the main view class.
 * Create and set the application main window with all the menus buttons 
 * and panels for drawing, controlling and informing
 * It's acces the model for example it can see the tree actual tree datas what is needed to display it.
 * And implements the controllers listeners to handle the actions of the user.
 */
public class BPlusTree_View {

    public JFrame frame;
    public BPlusTreeDrawPanel drawPanel;
    public PreviousBPlusTreeDrawPanel previousTreeDrawPanel;
    public JPanel multipleDrawPanel;
    public BPlusInfoPanel infoPanel;
    
    public JTextField textField;
    public JLabel statisticsLabel;
    public JScrollPane scrollablePane;
    public JCheckBox splitViewCheckBox;
    public JSpinner orderSpinner;
    public JButton btnInsert, btnFind, btnDelete, btnNew;
    public JButton btnPrev, btnNext, btnAnimation, btnTreeMaker, btnRandom, btnStop;
    public JMenuItem newMenuItem, loadMenuItem, saveMenuItem, exitMenuItem, prevStepItem, nextStepItem;        
            
    public Double scale = 1.0;
    public int treeDrawingStartCoordinateX, treeDrawingStartCoordinateY;
    public int deltaX, deltaY = 0;
    public int currentMouseX, currentMouseY;
    public int currentOrder = 5;
    public int treeMakerCounter = 20;
    public int maxRandomNumber = 100;
    public int animationSpeed = 2;
    public boolean prevBtnOn;
    public boolean animationIsON = false;
    public boolean animationIsRun = false;
    public boolean animationIsStopped = false;
    
    public BPlusTree_Model bplusTree;
    public AnimatedBubble animatedBubble = null;
    public ListOfChanges listOfChanges;
    public byte[] savedTreeDatabyteArray;
    
    /**
     * The constructor is recive the BPlusTree_Model and ListOfChanges from the Models
     * to display them and us it for the "Split View" option if needed.
     * Set the default values for the items and variables.
     * @param model
     * @param listOfChangesModel 
     */
    public BPlusTree_View(BPlusTree_Model model, ListOfChanges listOfChangesModel) {
        
        this.bplusTree = model;
        this.listOfChanges = listOfChangesModel;
        
        frame = new JFrame("B+ Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        //Menus 
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.white);
        ImageIcon newIcon = new ImageIcon("src/resources/new.png");
        ImageIcon loadIcon = new ImageIcon("src/resources/load.png");
        ImageIcon saveIcon = new ImageIcon("src/resources/save.png");
        ImageIcon exitIcon = new ImageIcon("src/resources/exit.png");
        ImageIcon nextIcon = new ImageIcon("src/resources/nextMenu.png");
        ImageIcon prevIcon = new ImageIcon("src/resources/prevMenu.png");
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        exitMenuItem = new JMenuItem("Exit", exitIcon);
        exitMenuItem.setName("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.setToolTipText("Exit application");
        
        newMenuItem = new JMenuItem("New", newIcon);
        newMenuItem.setName("New");
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.setToolTipText("Create a new Tree");
        
        loadMenuItem = new JMenuItem("Load", loadIcon);
        loadMenuItem.setName("Load");
        loadMenuItem.setMnemonic(KeyEvent.VK_L);
        loadMenuItem.setToolTipText("Load Tree from file");
        
        saveMenuItem = new JMenuItem("Save", saveIcon);
        saveMenuItem.setName("Save");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setToolTipText("Save the Tree to a file");
        
        JMenu stepsMenu = new JMenu("Steps");
        stepsMenu.setMnemonic(KeyEvent.VK_S);
        
        prevStepItem = new JMenuItem("Prev", prevIcon);
        prevStepItem.setName("Prev");
        prevStepItem.setMnemonic(KeyEvent.VK_P);
        prevStepItem.setToolTipText("Step to the prev status of the tree");
        
        nextStepItem = new JMenuItem("Next", nextIcon);
        nextStepItem.setName("Next");
        nextStepItem.setMnemonic(KeyEvent.VK_N);
        nextStepItem.setToolTipText("Step to the next status of the tree");
        
        stepsMenu.add(prevStepItem);
        stepsMenu.add(nextStepItem);        
        fileMenu.add(newMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(stepsMenu);
        frame.setJMenuBar(menuBar);
        
        //Options Panel
        JPanel optionsPanel = new JPanel();
        BorderLayout optionsPanelLayout = new BorderLayout();
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanel.setBorder(new TitledBorder(BorderFactory.createLoweredSoftBevelBorder(), "Options"));
        optionsPanel.setBackground(Color.white);
        Font optionsPanelFont = new Font("Arial", Font.PLAIN, 18);
        Dimension btnDimension = new Dimension(100,40);
        
        FlowLayout flow = new FlowLayout();
        flow.setHgap(15);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.white);
        ButtonsLine1 buttonsLine1 = new ButtonsLine1();
        ButtonsLine2 buttonsLine2 = new ButtonsLine2();
        buttonsLine1.setBackground(Color.white);
        buttonsLine2.setBackground(Color.white);
        JLabel label = new JLabel("Key: ");
        label.setFont(optionsPanelFont);
        buttonsLine1.add(label);
        textField = new JTextField();
        textField.setPreferredSize(btnDimension);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setFont(new Font("SansSerif", Font.BOLD, 16));
        buttonsLine1.add(textField);
        
        //Buttons panel
        btnInsert = new JButton("Insert");
        btnInsert.setName("Insert");
        btnInsert.setPreferredSize(btnDimension);
        btnInsert.setMnemonic(KeyEvent.VK_I);
        btnInsert.setFocusable(false);
        btnInsert.setToolTipText("The inserted key must be an integer between 0 and 999!");
        buttonsLine1.add(btnInsert);
        btnFind = new JButton("Find");
        btnFind.setName("Find");
        btnFind.setPreferredSize(btnDimension);
        btnFind.setMnemonic(KeyEvent.VK_F);
        btnFind.setFocusable(false);
        buttonsLine1.add(btnFind);
        btnDelete = new JButton("Delete");
        btnDelete.setName("Delete");
        btnDelete.setPreferredSize(btnDimension);
        btnDelete.setMnemonic(KeyEvent.VK_D);
        btnDelete.setFocusable(false);
        btnDelete.setToolTipText("The deleted key must be an integer between 0 and 999!");
        buttonsLine1.add(btnDelete);
        
        btnRandom = new JButton("Random");
        btnRandom.setName("Random");
        btnRandom.setPreferredSize(btnDimension);
        btnRandom.setMnemonic(KeyEvent.VK_R);
        btnRandom.setFocusable(false);
        btnRandom.setToolTipText("You can adjust the possible size of the Random number with the mouse wheel!");
        btnRandom.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                
                if (e.getPreciseWheelRotation() < 0) {
                    if (maxRandomNumber < 200)
                        maxRandomNumber += 10;
                    if (maxRandomNumber > 199 && maxRandomNumber < 999)
                        if (maxRandomNumber == 950)
                            maxRandomNumber = 999;
                        else
                            maxRandomNumber += 50;
                } else {
                    if (maxRandomNumber < 201 && maxRandomNumber > 100)
                        maxRandomNumber -= 10;
                    if (maxRandomNumber > 200)
                        if (maxRandomNumber == 999)
                            maxRandomNumber = 950;
                        else
                            maxRandomNumber -= 50;
                }
                if (maxRandomNumber <= treeMakerCounter) {
                    treeMakerCounter = 50;
                    buttonsLine2.repaint();
                }
                buttonsLine1.repaint();
            }
        });
        buttonsLine1.add(btnRandom);
        
        btnAnimation = new JButton();
        btnAnimation.setName("Animation");
        btnAnimation.setIcon(new ImageIcon("src/resources/animation_off.png"));
        btnAnimation.setBorder(BorderFactory.createEmptyBorder());
        btnAnimation.setMnemonic(KeyEvent.VK_A);
        btnAnimation.setFocusable(false);
        btnAnimation.setToolTipText("You can turn on/off the animation and can adjust the speed of it with the mouse wheel!");
        btnAnimation.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {                
                if (e.getPreciseWheelRotation() < 0) {
                    if (animationSpeed < 5)
                        animationSpeed++;
                } else {
                    if (animationSpeed > 1)
                        animationSpeed--;
                }
                buttonsLine1.repaint();
            }
        });
        
        buttonsLine1.add(Box.createRigidArea(new Dimension(50, 0)));
        buttonsLine1.add(btnAnimation);
        buttonsLine1.add(Box.createRigidArea(new Dimension(40, 0)));
        
        btnStop = new JButton("Stop");
        btnStop.setName("Stop");
        btnStop.setPreferredSize(btnDimension);
        btnStop.setMinimumSize(btnDimension);
        btnStop.setMnemonic(KeyEvent.VK_A);
        btnStop.setFocusable(false);
        btnStop.setVisible(false);
        buttonsLine1.add(btnStop);        
        
        btnNew = new JButton("New Tree");
        btnNew.setName("New");
        btnNew.setPreferredSize(btnDimension);
        btnNew.setMnemonic(KeyEvent.VK_N);
        btnNew.setFocusable(false);
        btnNew.setToolTipText("Itt will make a new empty tree with the given degree");
        buttonsLine2.add(btnNew);
        
        btnTreeMaker = new JButton("Tree Maker");
        btnTreeMaker.setName("Maker");
        btnTreeMaker.setPreferredSize(btnDimension);
        btnTreeMaker.setMnemonic(KeyEvent.VK_M);
        btnTreeMaker.setFocusable(false);
        btnTreeMaker.setToolTipText("You can adjust the amount of keys in the Tree with the mouse wheel!");
        btnTreeMaker.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                
                if (e.getPreciseWheelRotation() < 0) {
                    if (treeMakerCounter < 50)
                        treeMakerCounter++;
                    if (treeMakerCounter > 49 && treeMakerCounter < 200)
                        treeMakerCounter += 10;
                } else {
                    if (treeMakerCounter < 51 && treeMakerCounter > 5)
                        treeMakerCounter--;
                    if (treeMakerCounter > 50)
                        treeMakerCounter -= 10;
                }
                if (treeMakerCounter >= maxRandomNumber) {
                    maxRandomNumber = 300;
                    buttonsLine1.repaint();
                }
                buttonsLine2.repaint();
            }
        });
        buttonsLine2.add(btnTreeMaker);
        buttonsLine2.add(Box.createRigidArea(new Dimension(50, 0)));
        
        //Previous and Next buttons
        btnPrev = new JButton();
        btnPrev.setName("Prev");
        btnPrev.setIcon(new ImageIcon("src/resources/prev.png"));
        btnPrev.setBorder(BorderFactory.createEmptyBorder());
        buttonsLine2.add(btnPrev);
        btnNext = new JButton(); 
        btnNext.setName("Next");
        btnNext.setIcon(new ImageIcon("src/resources/nextdisabled.png"));
        btnNext.setBorder(BorderFactory.createEmptyBorder());
        buttonsLine2.add(btnNext);
        
        buttonsLine1.add(Box.createRigidArea(new Dimension(0, 50)));
        buttonsPanel.add(buttonsLine1);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(buttonsLine2);
        
        optionsPanel.add(buttonsPanel, BorderLayout.CENTER);        
        
        //Settings Panel with split view and order spinner
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBackground(Color.white);
        splitViewCheckBox = new JCheckBox(" Split view");
        splitViewCheckBox.setBackground(Color.white);
        splitViewCheckBox.setFont(optionsPanelFont);
        //split view checkbox change listener
        splitViewCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (splitViewCheckBox.isSelected()) {
                    infoPanel.textArea.append("Split view is On!\n");
                    changeMultipleDrawpanelView(true);
                } else {
                    infoPanel.textArea.append("Split view is Off!\n");
                    changeMultipleDrawpanelView(false);
                }
            }
        });
        
        //Spinner
        JLabel spinnerLabel = new JLabel("Order of the B+ Tree: ");
        spinnerLabel.setFont(optionsPanelFont);
        orderSpinner = new JSpinner(new SpinnerNumberModel(currentOrder, 4, 8 , 1));
        orderSpinner.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.add(splitViewCheckBox);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        JPanel settingsPanelLine1 = new JPanel();
        settingsPanelLine1.setBackground(Color.white);
        settingsPanelLine1.add(spinnerLabel);
        settingsPanelLine1.add(orderSpinner);
        settingsPanel.add(settingsPanelLine1);        
        optionsPanel.add(settingsPanel, BorderLayout.EAST);       
        
        //Statistic label
        statisticsLabel = new JLabel(" ", SwingConstants.CENTER);
        statisticsLabel.setLayout(new FlowLayout(FlowLayout.CENTER));
        statisticsLabel.setFont(optionsPanelFont);
        //statisticsLabel.setBorder(new TitledBorder(null, "Statistics"));
        statisticsLabel.setFont(new Font("SansSerif", Font.PLAIN , 13));
        optionsPanel.add(statisticsLabel, BorderLayout.SOUTH); 

        
        //Draw Panel initialization
        drawPanel = new BPlusTreeDrawPanel();
        drawPanel.addMouseWheelListener(new MouseAdapter() {    
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double delta = 0.05f * e.getPreciseWheelRotation();
                if (delta < 0 && scale > 0.1)                
                    scale += delta;
                if (delta > 0 && scale < 5.0) 
                    scale += delta;
                
                repaintAll();
            }
        });
        
        //Mouse listener for the draw panel
        drawPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    currentMouseX = e.getX();
                    currentMouseY = e.getY();
                }
                if(e.getButton() == MouseEvent.BUTTON3){
                    setTreeStartingCoordinates();
                    if (!bplusTree.treeIsEmpty())
                        repaintAll();
                }
            }
            public void mouseReleased(MouseEvent e) {
                treeDrawingStartCoordinateX += deltaX;
                treeDrawingStartCoordinateY += deltaY;
                deltaX = deltaY = 0; 
            }
        });
        
        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if ((MouseEvent.BUTTON1_DOWN_MASK & e.getModifiersEx()) != 0) {
                    deltaX = - (currentMouseX - e.getX());
                    deltaY = - (currentMouseY - e.getY());
                    if (!bplusTree.treeIsEmpty())
                        repaintAll();
                }
            }
        });

        //DrawPanel
        multipleDrawPanel = new MultipleDrawPanel();
        multipleDrawPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(1), "B+ Tree", javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, null, java.awt.Color.black));
        multipleDrawPanel.setLayout(new BoxLayout(multipleDrawPanel, BoxLayout.Y_AXIS));
        multipleDrawPanel.add(drawPanel);
        
        //Info Panel and Main Frame
        infoPanel = new BPlusInfoPanel();        
        frame.add(multipleDrawPanel, BorderLayout.CENTER);
        frame.add(infoPanel, BorderLayout.EAST);
        frame.add(optionsPanel, BorderLayout.SOUTH);  
        frame.pack();
        //frame.setSize(1600, 1000); //Win
        frame.setSize(1440, 900);  //MacBook limit
        frame.setMinimumSize(new Dimension(1200, 750));
        frame.setMaximumSize(new Dimension(2560, 1440));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        textField.requestFocus();        
        setTreeStartingCoordinates();
    }
    
    /**
     * This process can change the view of the draw panel from normal(single) view to split look
     * depending on the parameter
     * @param splitViewIsSelected 
     */    
    public void changeMultipleDrawpanelView(boolean splitViewIsSelected) {
        if (splitViewIsSelected) {
            multipleDrawPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(1), "Previous / Current B+ Tree", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, null, java.awt.Color.black));
            multipleDrawPanel.removeAll();
            previousTreeDrawPanel = new PreviousBPlusTreeDrawPanel();
            
            multipleDrawPanel.add(previousTreeDrawPanel);
            multipleDrawPanel.add(Box.createRigidArea(new Dimension(0,40)));
            multipleDrawPanel.add(drawPanel);
            multipleDrawPanel.revalidate();
        }else {
            multipleDrawPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createSoftBevelBorder(1), "B+ Tree", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, null, java.awt.Color.black));
            drawPanel.setBorder(null);
            multipleDrawPanel.removeAll();
            multipleDrawPanel.add(drawPanel);
            multipleDrawPanel.revalidate();
        }
    }
    
    /**
     * This method is repaint all the important parts of the window 
     * to see all possible changes 
     */
    public void repaintAll() {
        drawPanel.repaint();
        if (splitViewCheckBox.isSelected()){
            previousTreeDrawPanel.repaint();
        }
        multipleDrawPanel.repaint();
        refreshStatistics();
    }
    
    /**
     * This refers the datas on the statistisc panel from the actual tree
     */
    public void refreshStatistics() {
        if (bplusTree.getNumberOfTheLeaves() > 0) {
        statisticsLabel.setText(bplusTree.getNumberOfTheLeaves() + " Leaves;   " + bplusTree.getNumberOfTheKeys() + 
                    " Keys;   " + (bplusTree.getNumberOfTheKeys()*100 / (bplusTree.getNumberOfTheLeaves() * (bplusTree.getOrderOfTheTree()-1))) +
                    "% (of full);   " + bplusTree.getHeightOfTheTree()+ " Height");
        } else {
            statisticsLabel.setText("\"The tree is empty right now\"");
        }
    }
    
    public void setTreeStartingCoordinates() {        
        treeDrawingStartCoordinateX = (drawPanel.getWidth() - 28 * (currentOrder - 1)) / 2;
        treeDrawingStartCoordinateY = 60;
        scale = 1.0;
    }    
    
    /**
     * This class is the responsible for splitting the panel between the PreviousBPlusTreeDrawPanel and the BPlusTreeDrawPanel
     * Also gives information from 
     */
    public class MultipleDrawPanel extends JPanel {
        public MultipleDrawPanel() {
            setBackground(Color.white);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            if(splitViewCheckBox.isSelected()) {
                g2d.setColor(new Color(187, 189, 190));  //Cool gray
                g2d.fillRect(7, 17, this.getWidth() - 14, this.getHeight()/ 2 + 7);
                g2d.setColor(new Color(152, 172, 159));
                g2d.fillRect(7, this.getHeight()/2 + 7, this.getWidth() - 14, this.getHeight());
                if (!bplusTree.treeIsEmpty() || listOfChanges.getNumberOfChanges() != 0) {
                    if (listOfChanges.getTheCurrentOperationKind() == "Insert") {
                        g2d.setColor(new Color(126, 156, 191));
                        g2d.fillRoundRect(this.getWidth() - 250 , this.getHeight() / 2 - 13, 190, 35, 10, 10);
                        g2d.setColor(new Color(5, 5, 5));
                        g2d.drawRoundRect(this.getWidth() - 250 , this.getHeight() / 2 - 13, 190, 35, 10, 10);
                        g2d.setFont(new Font("SansSerif", Font.BOLD , 18));
                        g2d.setColor(Color.white);
                        g2d.drawString(listOfChanges.getTheCurrentOperationKind() + ": " + listOfChanges.getTheCurrentOperationKey(), this.getWidth() - 200, this.getHeight() / 2 + 12 );
                    }
                    if (listOfChanges.getTheCurrentOperationKind() == "Delete") {
                        g2d.setColor(new Color(137, 89, 95));
                        g2d.fillRoundRect(this.getWidth() - 250 , this.getHeight() / 2 - 13, 190, 35, 10, 10);
                        g2d.setColor(new Color(5, 5, 5));
                        g2d.drawRoundRect(this.getWidth() - 250 , this.getHeight() / 2 - 13, 190, 35, 10, 10);
                        g2d.setFont(new Font("SansSerif", Font.BOLD , 18));
                        g2d.setColor(Color.white);
                        g2d.drawString(listOfChanges.getTheCurrentOperationKind() + ": " + listOfChanges.getTheCurrentOperationKey(), this.getWidth() - 200, this.getHeight() / 2 + 12);
                    }
                    if (listOfChanges.getTheCurrentOperationKind() == "Load") {
                        g2d.setColor(new Color(126, 156, 191));
                        g2d.fillRoundRect(this.getWidth() - 260 , this.getHeight() / 2 - 13, 210, 35, 10, 10);
                        g2d.setColor(new Color(5, 5, 5));
                        g2d.drawRoundRect(this.getWidth() - 260 , this.getHeight() / 2 - 13, 210, 35, 10, 10);
                        g2d.setFont(new Font("SansSerif", Font.BOLD , 18));
                        g2d.setColor(Color.white);
                        g2d.drawString("Loaded Tree from file", this.getWidth() - 250, this.getHeight() / 2 + 12);
                    }
                }
            }
        }
    }
    
    /**
     * This inner class is the main draw panel for drawing the tree and painting the
     * bubble for the animation if the user wants.
     */
    class BPlusTreeDrawPanel extends JPanel {
        
        public BPlusTreeDrawPanel() {
            setBackground(new Color(152, 172, 159));
        }
    
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.scale(scale, scale);
            paintTree(g2d);
            paintAnimatedBubble(g2d);
            
            g2d.dispose();
        }

        public void paintTree(Graphics2D g2d) {
            bplusTree.paintTree(treeDrawingStartCoordinateX + deltaX, treeDrawingStartCoordinateY + deltaY , g2d);
        }        
        
        public void paintAnimatedBubble(Graphics2D g2d) {
            if (animatedBubble != null) {
                animatedBubble.paint(treeDrawingStartCoordinateX + deltaX - bplusTree.getRootXCoordinate(), treeDrawingStartCoordinateY + deltaY, g2d);
            }
        }
    }
    
    /**
     * Similar class as the BPlusTreeDrawPanel just here drawing the AnimatedBubble is not needed.
     * And it's paint the previous status of the tree if it exists.
     */
    class PreviousBPlusTreeDrawPanel extends JPanel {
        
        public PreviousBPlusTreeDrawPanel() {
            setBackground(new Color(187, 189, 190));  //Cool gray            
        }
    
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            //g2d.scale(1.0, 1.0);
            g2d.scale(scale, scale);
            
            if (!bplusTree.treeIsEmpty() || listOfChanges.getNumberOfChanges() != 0) {
                try {
                    paintTree(g2d);
                } catch (Exception e) {
                }
            }
        }
        
        public void paintTree(Graphics2D g2d) throws IOException, ClassNotFoundException {
            //Call the draw method to listOfChanges prev status
            if (listOfChanges.getNumberOfChanges() == 0 || listOfChanges.isItTheFirst()) {                
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRoundRect(this.getWidth() / 2 - 170, this.getHeight() / 2 - 50, 350, 50, 7, 7);
                g2d.setColor(new Color(5, 5, 5));
                g2d.drawRoundRect(this.getWidth() / 2 - 170, this.getHeight() / 2 - 50, 350, 50, 7, 7);
                Font font = new Font("Helvetica", Font.PLAIN, 20);
                g2d.setColor(Color.black);
                g2d.setFont(font);
                g2d.drawString("There is no previous tree in the list!", this.getWidth() / 2 - 150, this.getHeight() / 2 - 20); 
            } else {
                listOfChanges.getThePrevTree().paintTree(treeDrawingStartCoordinateX + deltaX, treeDrawingStartCoordinateY + deltaY , g2d);
            }
        }
    }

    /**
     * Separated calss for the first line of buttons.
     * It's speciality is that to draw a conter in the top right corner of the "Random" button,
     * and a dispal scale for the annimation speed if the animation option is "On".
     */
    public class ButtonsLine1 extends JPanel {        
        public ButtonsLine1 () {
        FlowLayout flow = new FlowLayout();
        flow.setHgap(15);
        this.setLayout(flow);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2d.setColor(new Color(126, 156, 191));
            g2d.setFont(new Font("TimesRoman", Font.BOLD, 14));
            g2d.fillOval(btnRandom.getLocation().x + 102, btnRandom.getLocation().y - 11, 30, 30);
            g2d.setColor(Color.white);
            g2d.drawString("" + maxRandomNumber, btnRandom.getLocation().x + 105, btnRandom.getLocation().y + 8);               

            if (animationIsON) {
                for (int i = 0; i < 5; i++) {
                    if (i < 5 - animationSpeed) {
                        g2d.setColor(Color.lightGray);
                        g2d.fillRoundRect(btnAnimation.getLocation().x + 165, btnAnimation.getLocation().y + (i * 10)+2, 20, 6, 5, 5);                
                        g2d.setColor(Color.gray);
                        g2d.drawRoundRect(btnAnimation.getLocation().x + 165, btnAnimation.getLocation().y + (i * 10)+2, 20, 6, 5, 5);
                    } else {
                        g2d.setColor(new Color(6, 189, 60));
                        g2d.fillRoundRect(btnAnimation.getLocation().x + 165, btnAnimation.getLocation().y + (i * 10)+2, 20, 6, 5, 5);
                        g2d.setColor(Color.gray);
                        g2d.drawRoundRect(btnAnimation.getLocation().x + 165, btnAnimation.getLocation().y + (i * 10)+2, 20, 6, 5, 5);
                    }
                }
            }
        }      
    }

    /**
     * Another separated calss for the second line of buttons.
     * It's draw a conter to in the top right corner of the "Tree Maker" button.
     */
    public class ButtonsLine2 extends JPanel {        
        public ButtonsLine2 () {
            FlowLayout flow = new FlowLayout();
            flow.setHgap(15);
            this.setLayout(flow);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);            
            g2d.setFont(new Font("TimesRoman", Font.BOLD, 14));
            g2d.setColor(new Color(126, 156, 191));            
            g2d.fillOval(btnTreeMaker.getLocation().x + 102, btnTreeMaker.getLocation().y - 11, 30, 30);
            g2d.setColor(Color.white);
            if (treeMakerCounter < 100 && treeMakerCounter > 9)
                g2d.drawString("" + treeMakerCounter, btnTreeMaker.getLocation().x + 109, btnTreeMaker.getLocation().y + 8);
            if (treeMakerCounter < 10)
                g2d.drawString("" + treeMakerCounter, btnTreeMaker.getLocation().x + 113, btnTreeMaker.getLocation().y + 8);
            if (treeMakerCounter > 99)
                g2d.drawString("" + treeMakerCounter, btnTreeMaker.getLocation().x + 105, btnTreeMaker.getLocation().y + 8);
        }      
    }
}

