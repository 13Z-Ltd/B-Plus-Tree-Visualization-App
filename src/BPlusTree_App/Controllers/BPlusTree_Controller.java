///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Controllers
// File:             BPlusTree_Controller.java
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

package BPlusTree_App.Controllers;

import BPlusTree_App.Models.*;
import BPlusTree_App.Views.BPlusTree_View;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Controller class of the appilaction.
 * Because the view panel is extends this incloud all the listeners this gives information about the user actions. 
 * And it has access to the model as well so it can take the necessary steps within.
 * It also can call refresh on the view if needed.
 */
public class BPlusTree_Controller {
    
    BPlusTree_Model bplusTree;
    BPlusTree_View view;    
    
    public AnimatedBubble animatedBubble;
    public ListOfChanges listOfChanges;
    public ListOfAnimationData listOfAnimationDatas;
    public byte[] savedTreeDatabyteArray;
    
    public int currentOrder = 5;
    public int actualKey = 0;    
    public boolean animationIsON = false;
    public boolean animationIsRun = false;
    public boolean animationIsStopped = false;
    private Timer animationTimer;
    private long startTime;
    private int slideIndex;
    private final int runTime = 1500;     //1000;    
    
    ButtonPressListener buttonPressListener;    
    TextFieldKeyListener textFieldKeyListener;
    MenuActionListener menuActionListener;
    OrderChangeListener orderChangeListener;
    
    /**
     * The constructor gets access to the Model, the View and for the Changes
     * to can see and control the whole app.
     * It's set the listener to the view buttons and controls.
     * @param bPlusTreeModel
     * @param listOfChangesModel
     * @param bPlusTreeView 
     */
    public BPlusTree_Controller(BPlusTree_Model bPlusTreeModel, ListOfChanges listOfChangesModel,BPlusTree_View bPlusTreeView) {
        this.bplusTree = bPlusTreeModel;
        this.view = bPlusTreeView;
        this.listOfChanges = listOfChangesModel;
        
        currentOrder = bplusTree.getOrderOfTheTree();
        animatedBubble = null;
                
        //Set the button listeners
        buttonPressListener = new ButtonPressListener(this);
        view.btnInsert.addActionListener(buttonPressListener);
        view.btnFind.addActionListener(buttonPressListener);
        view.btnDelete.addActionListener(buttonPressListener);
        view.btnRandom.addActionListener(buttonPressListener);
        view.btnAnimation.addActionListener(buttonPressListener);
        view.btnStop.addActionListener(buttonPressListener);
        view.btnNew.addActionListener(buttonPressListener);
        view.btnTreeMaker.addActionListener(buttonPressListener);
        view.btnPrev.addActionListener(buttonPressListener);
        view.btnNext.addActionListener(buttonPressListener);
        
        //Set the menu listeners
        menuActionListener  = new MenuActionListener(this);        
        view.newMenuItem.addActionListener(menuActionListener);
        view.loadMenuItem.addActionListener(menuActionListener);
        view.saveMenuItem.addActionListener(menuActionListener);
        view.exitMenuItem.addActionListener(menuActionListener);
        view.prevStepItem.addActionListener(menuActionListener);
        view.nextStepItem.addActionListener(menuActionListener);
        
        //Set the spinner change listener for the order changer
        orderChangeListener = new OrderChangeListener(this);
        view.orderSpinner.addChangeListener(orderChangeListener);
        
        //Set key listener on the textfield to handle key commands
        textFieldKeyListener = new TextFieldKeyListener(this);
        view.textField.addKeyListener(textFieldKeyListener);
    }
    
    /**
     * This is the boot process creates the tree that is generated when 
     * when the program is started.
     */
    public void run() {
        treeMaker();
        view.infoPanel.clearTextArea();
    }
    
    /**
     * This proccess has been called after any insertation command.
     * It's checks the correctnes of the given data and start the animation when the option is on
     * or just insert the data if not.
     */
    public void insertionProcess() {
        actualKey = tryParse(view.textField.getText());
        if(actualKey < 1 || actualKey > 999) {
            view.infoPanel.addLine("Invalid data for insert! \n");
        }
        else {
            if (bplusTree.search(actualKey) == -1 ) {
                if (animationIsON && bplusTree.getNumberOfTheKeys() > 0) {
                    //It's try to save the original tree before the animation is can change it status 
                    try {                        
                        savedTreeDatabyteArray = btreeConvertToBytes(bplusTree);
                    } catch (IOException ex) {
                        Logger.getLogger(BPlusTree_Controller.class.getName()).log(Level.SEVERE, null, ex);                        
                    }
                    //Make new animatedBubble for the animation
                    animatedBubble = new AnimatedBubble("INSERT");
                    view.animatedBubble = animatedBubble;                    
                    animatedBubble.setKeyNumber(actualKey);
                    //prepares the list of animation steps
                    listOfAnimationDatas = new ListOfAnimationData();
                    listOfAnimationDatas = bplusTree.routeForTheAnimationInsert(actualKey);                    
                    animationIsRun = true;
                    //Start to run the animation
                    runFullAnimation();                    
                } else {
                    insertData(actualKey);
                }
            } else {
                view.infoPanel.addLine("This key " + actualKey + " is in the tree. Please enter a new one.\n");
            }
        }
    }
    
    public void insertData(int key) {
        view.infoPanel.addLine("Insert the " + key + " key into the tree. \n");
        bplusTree.insert(key, "(" + key + "Key) DATA");
        listOfChanges.addChange(bplusTree, key, "Insert");
        view.btnPrev.setIcon(new ImageIcon("src/resources/prev.png"));
        view.btnNext.setIcon(new ImageIcon("src/resources/nextdisabled.png"));
        
        view.repaintAll();
    }
    
    /**
     * Creat a random number within the value of the view random number counter
     */
    public void randomInsert() {
        //Check the random number is exists in the tree, if there it's make a new.
        int randomInsertNumber = (int) ((Math.random() * view.maxRandomNumber) + 1);
        while (bplusTree.search(randomInsertNumber) != -1 ) {
            randomInsertNumber = (int) ((Math.random() * view.maxRandomNumber) + 1);
        }
        actualKey = randomInsertNumber;
        if (animationIsON && bplusTree.getNumberOfTheKeys() > 0) {
            //It's try to save the original tree before the animation is can change it status
            try {                        
                savedTreeDatabyteArray = btreeConvertToBytes(bplusTree);
            } catch (IOException ex) {
                Logger.getLogger(BPlusTree_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Make new animatedBubble for the animation
            animatedBubble = new AnimatedBubble("INSERT");
            view.animatedBubble = animatedBubble;            
            animatedBubble.setKeyNumber(actualKey);
            //prepares the list of animation steps
            listOfAnimationDatas = new ListOfAnimationData();
            listOfAnimationDatas = bplusTree.routeForTheAnimationInsert(actualKey);            
            animationIsRun = true;
            runFullAnimation();
        } else {
            insertData(actualKey);
        }
    }
    
    /**
     * Make a new tree with the number of items specified at the tree maker counter in the view
     */
    public void treeMaker() {
        deleteTree();
        //The animation has to be off!
        boolean currentAnimationIsON = animationIsON;
        animationIsON = false;
        for (int i = 0; i < view.treeMakerCounter; i++) {
            randomInsert();
        }
        animationIsON = currentAnimationIsON;
        view.infoPanel.clearTextArea();
        view.infoPanel.addLine("This method made a new tree with " + view.treeMakerCounter + " keys.\n");        
    }
    
    /**
     * This proccess has been called when the user wants to delete a data.
     * It's checks the correctnes of the given data and whether is in the tree.
     * After start the animation when the "Animation" is on and delete the data.
     */
    public void deletionProcess() {
        actualKey = tryParse(view.textField.getText());
        if(actualKey < 1) {
            view.infoPanel.addLine("Invalid data! \n");
        }
        else {
            if(bplusTree.search(actualKey) > -1) {
                if (animationIsON) {
                    //It's try to save the original tree before the animation is can change it status
                    try {                        
                        savedTreeDatabyteArray = btreeConvertToBytes(bplusTree);
                    } catch (IOException ex) {
                        Logger.getLogger(BPlusTree_Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Make new animatedBubble for the animation
                    animatedBubble = new AnimatedBubble("DELETE");
                    view.animatedBubble = animatedBubble;
                    animatedBubble.setKeyNumber(actualKey);
                    //Prepares the list of animation steps
                    listOfAnimationDatas = new ListOfAnimationData();
                    listOfAnimationDatas = bplusTree.routeForTheAnimationDelete(actualKey);                    
                    animationIsRun = true;
                    runFullAnimation();
                } else {
                    deleteData(actualKey);
                }
            }
            else {
                view.infoPanel.addLine("The " + actualKey + " key is not in the tree. \n");
            }
        }
    }
    
    public void deleteData(int key) {
        view.infoPanel.addLine("The " + key + " key has deleted from the tree. \n");
        bplusTree.delete(key);
        listOfChanges.addChange(bplusTree, key, "Delete");
        //Change the icon of the prev/next buttons
        view.btnPrev.setIcon(new ImageIcon("src/resources/prev.png"));
        view.btnNext.setIcon(new ImageIcon("src/resources/nextdisabled.png"));
        view.repaintAll();
    }
    
    /**
     * This method delete all the data in the tree and create a new one.
     * And set the split view option back to normal view!
     */
    public void deleteTree() {
        if (!bplusTree.treeIsEmpty())
            view.infoPanel.addLine("The tree is deleted! \n");
        view.splitViewCheckBox.setSelected(false);
        
        bplusTree = new BPlusTree_Model(currentOrder);
        view.bplusTree = bplusTree;      
        listOfChanges = new ListOfChanges();
        view.listOfChanges = listOfChanges;
        
        //Change the icon of the prev/next buttons
            view.btnPrev.setIcon(new ImageIcon("src/resources/prevdisabled.png"));
            view.btnNext.setIcon(new ImageIcon("src/resources/nextdisabled.png"));
        view.setTreeStartingCoordinates();
        view.repaintAll();
    }
    
    /**
     * It's checks the correctnes of the given data and make the list for the finding animation.
     * At the end it will display the search result.
     */
    public void findingProcess() {
        actualKey = tryParse(view.textField.getText());
        if(actualKey < 1) {
            view.infoPanel.addLine("Invalid data! \n");
        }
        else {
            if (animationIsON) {
                animatedBubble = new AnimatedBubble("FIND");
                view.animatedBubble = animatedBubble;                
                animatedBubble.setKeyNumber(actualKey);
                
                listOfAnimationDatas = new ListOfAnimationData();
                listOfAnimationDatas = bplusTree.routeForTheAnimationFind(actualKey);
                animationIsRun = true;
                runFullAnimation();
            }
            else {
                findData(actualKey);
            }
        }
    }
    
    public void findData(int key) {
        if(bplusTree.search(key) > -1) {
            view.infoPanel.addLine("The searched key (" + key + ") is find in the tree. \n");
        }
        else {
            view.infoPanel.addLine("The searched key (" + key + ") is not found in the tree. \n");
        }
        view.repaintAll();
    }
    
    /**
     * This has been called when the user press the prev arrow button int the view.
     * It step back one in the list of changes if it's possible.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void stepToThePrevTree() throws IOException, ClassNotFoundException {
        if (!listOfChanges.isItTheFirst()) {
            bplusTree = listOfChanges.getPrevStatus();
            view.bplusTree = bplusTree;
            view.repaintAll();
        } else
            view.infoPanel.addLine("Can not step back more in the list!\n");
    }
    
    /**
     * This has been called when the user press the next arrow button.
     * And it step forward one in the list of changes if it's possible. 
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void stepToTheNextTree() throws IOException, ClassNotFoundException {
        if (!listOfChanges.isItTheLast()) {
            bplusTree = listOfChanges.getNextStatus();
            view.bplusTree = bplusTree;
            view.repaintAll();
        } else
            view.infoPanel.addLine("There is no more step forward in the list!\n");
    }
    
    /**
     * This method is try to convert the given text in the text field to an integer
     * @param text contents of the view textField
     * @return 
     */
    public int tryParse(String text) {
        try {
            int insertNumber = Integer.parseInt(view.textField.getText());
            return insertNumber;
        } catch (NumberFormatException e) {
            view.infoPanel.addLine("The " + text + " can not be parsed!\n");
            return 0;
        }
    }
    
    /**
     * It's convert the tree datas into byte array using the java Serialization method
     * @param tree the current tree datas
     * @return byte array what can be save into a file for example
     * @throws IOException 
     */    
    private byte[] btreeConvertToBytes(BPlusTree_Model tree) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(tree);
            return bos.toByteArray();
        } 
    }
    
    /**
     * It's convert the given byte array back to a BPlusTree using the java Serialization method
     * @param bytes the recived byte array
     * @return a BPlusTree copy
     * @throws IOException 
     */
    private BPlusTree_Model btreeConvertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis)) {
            return (BPlusTree_Model) in.readObject();
        } 
    }
    
    /**
     * This used to load a BPlusTree data from file
     */
    public void loadTreeFromFile() {
        JFileChooser fileChooser = new JFileChooser("src/saves"); //Jar
        FileNameExtensionFilter filter = new FileNameExtensionFilter("BPlusTree Binary Files", "bin");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            view.infoPanel.addLine("Opening: " + file.getName() + ".\n");
            try {
                readFile(file);
            } catch (IOException ex) {
                view.infoPanel.addLine("File not found!\n");
                view.infoPanel.addLine(ex.getMessage() + "\n");
            } catch (ClassNotFoundException ex) {
                view.infoPanel.addLine("Can not cast the datas from the input stream!");
                view.infoPanel.addLine(ex.getMessage() + "\n");
            }
            if (bplusTree.getOrderOfTheTree() != currentOrder) {
                view.infoPanel.addLine("The order of the tree is changed to " + bplusTree.getOrderOfTheTree() + " becouse of the loaded datas!\n");
                currentOrder = bplusTree.getOrderOfTheTree();
                view.orderSpinner.setValue(currentOrder);
                view.setTreeStartingCoordinates();
            }
            view.repaintAll();
        }
        else {
            view.infoPanel.addLine("Open command cancelled by user.\n");
        }
    }
    
    public void readFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
        
        bplusTree = (BPlusTree_Model) objectInputStream.readObject();
        view.bplusTree = bplusTree;
        listOfChanges = new ListOfChanges();
        ///
        view.listOfChanges = listOfChanges;
        
        listOfChanges.addChange(bplusTree, 0, "Load");
        //Change the icon of the prev/next buttons
            view.btnPrev.setIcon(new ImageIcon("src/resources/prevdisabled.png"));
            view.btnNext.setIcon(new ImageIcon("src/resources/nextdisabled.png"));
        view.setTreeStartingCoordinates();
    }
    
    /**
     * This used to save the current BPlusTree into file
     */
    public void saveTreeToFile(){
        JFileChooser fileChooser = new JFileChooser("src/saves"); //Win
        FileNameExtensionFilter filter = new FileNameExtensionFilter("BPlusTree Binary Files", "bin");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            view.infoPanel.addLine("Saving: " + file.getName() + "(" + bplusTree.getOrderOfTheTree() + ").bin \n");
            view.infoPanel.addLine("The number at the end shows the order of the saved tree!\n");
            try {
                writeFile(file, bplusTree);
            } catch (IOException ex) {
                view.infoPanel.addLine("The tree can not write into the file!");
                view.infoPanel.addLine(ex.getMessage() + "\n");
                view.infoPanel.addLine(ex.getStackTrace().toString());
            }
        } 
        else {
            view.infoPanel.addLine("Save command cancelled by user.\n");
        }
    }
    
    public static void writeFile(File file, BPlusTree_Model tree) throws IOException {
        ObjectOutputStream objectOutputSteam = new ObjectOutputStream(new FileOutputStream(file + "(" + tree.getOrderOfTheTree() + ").bin"));        
        objectOutputSteam.writeObject(tree);
    }
    
    /**
     * Run full animation process drives the animation  all the way trough the prepared listOfAnimationDatas
     * Checks the type of the CurrentAnimationOperation and make the necessary modification with the animatedBubble
     * and lunch the right animation for it.
     */
    public void runFullAnimation() {
        if (listOfAnimationDatas.hasNext()){
            listOfAnimationDatas.nextStep();
            
            animatedBubble.setCurrentNode(listOfAnimationDatas.getTheCurrentAnimationNode());
            animatedBubble.setOperationType(listOfAnimationDatas.getTheCurrentAnimationOperation());
            
            
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "StepDown") {
                animatedBubble.setBubblePoints();                
                animateOneStep();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "StepUp") {
                animatedBubble.setStartPoint(animatedBubble.getLocation());         
                animatedBubble.setTargetPoint(animatedBubble.getParentNodeXCoordinate(), animatedBubble.getParentNodeYCoordinate()); 
                animateStepUp();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "OverFlow") {
                animatedBubble.setBubbleColor(new Color(175, 0, 2));  //Red                
                animateOverFlow();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "SplitNode") {
                view.infoPanel.addLine("The algorithm is start the SplitNode method!\n");
                animatedBubble.makeTheSplitingNodes();
                animateSplitNode();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "SplitNodeAddNewParent") {
                view.infoPanel.addLine("The algorithm is start the SplitNodeAddNewParent method!\n");
                animatedBubble.makeNewParent();
                bplusTree.setRoot(animatedBubble.getNewParentNode());
                
                animatedBubble.makeTheSplitingNodes();
                animateSplitNodeWithNewParent();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "UnderFlow") {
                view.infoPanel.addLine("The algorithm is start the animateUnderFlow method!\n");
                animatedBubble.deleteKeyForAnimation();
                view.repaintAll();
                animatedBubble.setBubbleColor(new Color(175, 0, 2));  //Red
                animateOverFlow();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "BorrowFromSibling") {
                view.infoPanel.addLine("The algorithm is start the BorrowFromSibling method!\n");
                if (listOfAnimationDatas.getTheCurrentAnimationKey() == 0) {
                    animatedBubble.borrowFromRightSibling();
                    animateBorrowFromSibling(1);
                } else {
                    animatedBubble.borrowFromLeftSibling();
                    animateBorrowFromSibling(1);
                }
            }            
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "BorrowChildFromSibling") {
                view.infoPanel.addLine("The algorithm is start the BorrowFromChild method!\n");
                if (listOfAnimationDatas.getTheCurrentAnimationKey() == 0) {
                    animatedBubble.borrowChildFromRightSibling();                    
                    animateBorrowFromSibling(0);    //0  indicates it will borrow from the right sibling
                } else {
                    animatedBubble.borrowChildFromLeftSibling();
                    animateBorrowFromSibling(-1);   //-1 indicates it will borrow from the left sibling
                }
            }            
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "ChildrenFusion") {
                view.infoPanel.addLine("The algorithm is start the ChildrenFusion method!\n");
                animatedBubble.setKeyNumber(listOfAnimationDatas.getTheCurrentAnimationKey());
                animatedBubble.childrenFusionWithSibling();
                animateChildrenFusion();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "InnerNodeFusion") {
                view.infoPanel.addLine("The algorithm is start the InnerNodeFusion method!\n");
                animatedBubble.setInnerNodeFusionParameters();
                animateInnerNodeFusion();               
            }        
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "KeyFromParrent") {                
                animatedBubble.setBubbleSentKeyFromParent(listOfAnimationDatas.getTheCurrentAnimationKey());
                animateOneStep();
            }        
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "DeleteStepUp") {
                animatedBubble.setKeyNumber(listOfAnimationDatas.getTheCurrentAnimationKey());
                animatedBubble.setStepUpNodeForAnimate();
                animateStepUp();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "DeleteChild") {
                animateDeleteChild();
            }
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "ParentDelete") {
                bplusTree.setRoot(((BTreeInnerNode)animatedBubble.getCurrentNode()).getChild(0));
                animatedBubble.setTheChildParentToNull(0);
                
                animatedBubble.setAllNodeNull();
                animateSlideUp();                
            }            
            if (listOfAnimationDatas.getTheCurrentAnimationOperation() == "Find") {
                if (animatedBubble.findKeyInTheNode() == -1)
                    animatedBubble.setBubbleColor(new Color(175, 0, 2));  //Red
                animateFind();
            }
        } else {
            //When the animation is over
            if (!animationIsStopped) {
                switch (animatedBubble.getAnimationType()) {
                    case "INSERT":
                        try {
                            bplusTree = btreeConvertFromBytes(savedTreeDatabyteArray);
                            view.bplusTree = bplusTree;
                        } catch (IOException | ClassNotFoundException ex) {
                        }
                        insertData(actualKey); 
                        break;
                    case "DELETE":
                        try {
                            bplusTree = btreeConvertFromBytes(savedTreeDatabyteArray);
                            view.bplusTree = bplusTree;
                        } catch (IOException | ClassNotFoundException ex) {
                        }
                        deleteData(actualKey);
                        break;
                    case "FIND":
                        findData(actualKey);
                        break;
                }
            } else {
                try {
                    bplusTree = btreeConvertFromBytes(savedTreeDatabyteArray);
                    view.bplusTree = bplusTree;
                } catch (IOException | ClassNotFoundException ex) {
                }
                animationIsStopped = false;
                view.infoPanel.addLine("The animation has been stopped!\n");
            }
            animationIsRun = false;
            animatedBubble = null;
            view.animatedBubble = animatedBubble;
            view.infoPanel.addLine("The animation is over!\n");
            view.repaintAll();
        }
    }
    
    
    public void animateOneStep() {        
        startTime = -1;
        slideIndex = 0;
        animationTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if (startTime < 0)
                        startTime = System.currentTimeMillis();
                    float progress = 1f;
                    long duration = System.currentTimeMillis() - startTime;
                    if (duration >= (runTime / view.animationSpeed)) {
                        if ( slideIndex < animatedBubble.getCurrentNodeKeyCount() 
                                && animatedBubble.getKeyNumber() >= animatedBubble.getCurrentNodeKey(slideIndex)) {
                            animatedBubble.setLocationX(animatedBubble.getLocation().x + 26);
                            slideIndex++;
                            try {
                                TimeUnit.MILLISECONDS.sleep(300 / view.animationSpeed);
                            } catch (InterruptedException ex) {
                            }
                        } else {
                            ((Timer) e.getSource()).stop();
                            try {
                                TimeUnit.MILLISECONDS.sleep(1500 / view.animationSpeed);
                            } catch (InterruptedException ex) {
                            }
                            runFullAnimation();
                        }
                    } else {
                        progress = (float) duration / (float) (runTime / view.animationSpeed);
                        animatedBubble.update(progress);
                    }
                    view.repaintAll();
                }
            });
            animationTimer.start();
    }
    
    public void animateStepUp() {        
        startTime = -1;
        slideIndex = 0;
        animationTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0)
                    startTime = System.currentTimeMillis();
                float progress = 1f;
                long duration = System.currentTimeMillis() - startTime;
                if (duration >= (runTime / view.animationSpeed)) {                    
                    if ( slideIndex < animatedBubble.getParentNodeKeyCount() && animatedBubble.getKeyNumber() > animatedBubble.getParentNodeKey(slideIndex)) {
                        animatedBubble.setLocationX(animatedBubble.getLocation().x + 26);
                        slideIndex++;
                        try {
                            TimeUnit.MILLISECONDS.sleep(300 / view.animationSpeed);
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        ((Timer) e.getSource()).stop();
                        try {
                            TimeUnit.MILLISECONDS.sleep(1500 / view.animationSpeed);
                        } catch (InterruptedException ex) {
                        }
                        if (animatedBubble.getAnimationType() == "INSERT" && animatedBubble.getParentNodeKeyCount() == 0)         
                            view.deltaY -= 70;
                        if (animatedBubble.getAnimationType() == "DELETE")
                            animatedBubble.overWriteKeyInCurrentNode();
                        runFullAnimation();
                    }
                } else {
                    progress = (float) duration / (float) (runTime / view.animationSpeed);
                    animatedBubble.update(progress);
                }
                view.repaintAll();
            }
        });
        animationTimer.start();
    }
    
    public void animateOverFlow() {
        startTime = 0;
        animationTimer = new Timer(500 / view.animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTime++;
                if (startTime == 5) {
                    ((Timer) e.getSource()).stop();
                    runFullAnimation();                    
                }
                    view.repaintAll();
                }
            });
            animationTimer.start();
    }
    
    public void animateSplitNode(){
        startTime = -1;
        slideIndex = 0;
        Timer slideTimer = new Timer(50 / view.animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slideIndex++;
                animatedBubble.childNodesSlide();
                animatedBubble.slideBubble();           
                if (animatedBubble.getCurrentNodeTypeString() == "LeafNode")
                    bplusTree.adjustInnerNodes(bplusTree.getRoot());

                if (slideIndex == bplusTree.getOrderOfTheTree()* (28 / 4)) {
                    ((Timer) e.getSource()).stop();
                    runFullAnimation();
                }
                view.repaintAll();
            }
        });
        slideTimer.start(); 
    }
    
    public void animateSplitNodeWithNewParent(){
        startTime = -1;
        slideIndex = 0;
        Timer slideTimer = new Timer(50 / view.animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slideIndex++;
                view.deltaY += 2;        
                animatedBubble.childNodesSlide();
                animatedBubble.slideBubble();
                
                if (slideIndex == bplusTree.getOrderOfTheTree()* (28 / 4)) {
                    ((Timer) e.getSource()).stop();
                    runFullAnimation();
                }
                view.repaintAll();
            }
        });
        slideTimer.start();        
    }
    
    public void animateBorrowFromSibling(int direction) {
        startTime = -1;
        slideIndex = 0;
        animationTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if (startTime < 0) {
                        try {
                                TimeUnit.MILLISECONDS.sleep(3000 / view.animationSpeed);
                            } catch (InterruptedException ex) {
                            }
                        startTime = System.currentTimeMillis();
                    }
                    float progress = 1f;
                    long duration = System.currentTimeMillis() - startTime;
                    if (duration >= (runTime / view.animationSpeed)) {                        
                            ((Timer) e.getSource()).stop();
                            try {
                                TimeUnit.MILLISECONDS.sleep(1500 / view.animationSpeed);
                            } catch (InterruptedException ex) {
                            }
                            animatedBubble.insertKeyToCurrentNode(direction);
                            runFullAnimation();
                        
                    } else {
                        progress = (float) duration / (float) (runTime / view.animationSpeed);
                        if (direction == 1)
                            animatedBubble.update(progress);
                        if (direction != 1)
                            animatedBubble.update2(progress);
                    }
                    view.repaintAll();
                }
            });
            animationTimer.start();
    }
    
    public void animateChildrenFusion() {
        startTime = -1;
        slideIndex = 0;
        Timer slideTimer = new Timer(50 / view.animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                startTime++;
                if(startTime > 20) {
                slideIndex++;
                if (slideIndex < 28 / 2) {
                    animatedBubble.slideAllOfTheRightSiblings();
                }                    
                if (slideIndex >=  28 / 2) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(800 / view.animationSpeed);
                        } catch (InterruptedException ex) {
                    }
                    if (animatedBubble.getCurrentNodeRightSiblingKeyCount() == 0) {
                        ((Timer) e.getSource()).stop();
                        runFullAnimation();                        
                    } else {
                        if (animatedBubble.getCurrentNodeTypeString() == "LeafNode") {
                            int trasferedNumber = animatedBubble.getCurrentNodeRightSiblingKey(0);
                            ((BTreeLeafNode)animatedBubble.getCurrentNode()).insertKey(trasferedNumber, "(" + trasferedNumber + "Key) DATA");
                            ((BTreeLeafNode)animatedBubble.getRightSiblingNode()).delete(trasferedNumber);  
                        }
                    }
                }
                view.repaintAll();
                }
            }
        });
        slideTimer.start();  
    }
    
    public void animateInnerNodeFusion() {
        startTime = -1;
        //slideIndex = 0;
        animationTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if (startTime < 0)
                        startTime = System.currentTimeMillis();
                    float progress = 1f;
                    long duration = System.currentTimeMillis() - startTime;
                    if (duration >= (runTime / view.animationSpeed)) {
                        if (animatedBubble.getNewRightNodeKeyCount() != 0) {
                            animatedBubble.transferKeyAndChildFromSiblingNode();                                                       
                            try {
                                TimeUnit.MILLISECONDS.sleep(300 / view.animationSpeed);
                            } catch (InterruptedException ex) {
                            }
                        } else {
                            ((Timer) e.getSource()).stop();
                            try {
                                TimeUnit.MILLISECONDS.sleep(1500 / view.animationSpeed);
                            } catch (InterruptedException ex) {
                            }
                            runFullAnimation();
                        }
                    } else {
                        progress = (float) duration / (float) (runTime / view.animationSpeed);
                        animatedBubble.updateNewRightNodeLocation(progress);
                    }
                    view.repaintAll();
                }
            });
            animationTimer.start();
    }
    
    public void animateDeleteChild() {
        startTime = 0;
        animationTimer = new Timer(400 / view.animationSpeed, new ActionListener() {
        int delayDelta = 20 / view.animationSpeed;
            @Override
            public void actionPerformed(ActionEvent e) {
                startTime++;                
                if((startTime%2)==0)
                   animatedBubble.setCurrentNodeColor(null);
                else
                    animatedBubble.setCurrentNodeColor(new Color(175, 0, 2));
                
                if (startTime == 10) {
                    ((Timer) e.getSource()).stop();
                    animatedBubble.setNewParentNodeDeleteingBoxIndex(-1);
                    runFullAnimation();                    
                }
                animationTimer.setDelay(animationTimer.getDelay() - (delayDelta));
                view.repaintAll();
                }
            });
            animationTimer.start();  
    }
    
    public void animateSlideUp() {
        startTime = -1;
        slideIndex = 0;
        Timer slideTimer = new Timer(50 / view.animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                slideIndex++;
                view.deltaY -= 2;                
                if (slideIndex == 35) {
                    ((Timer) e.getSource()).stop();
                    view.deltaY += 70;
                    runFullAnimation();
                }
                view.repaintAll();
            }
        });
        slideTimer.start();  
    }
    
    public void animateFind(){
        startTime = 0;
        animationTimer = new Timer(500 / view.animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTime++;
                if (startTime == 5) {
                    ((Timer) e.getSource()).stop();
                    runFullAnimation();                    
                }
                    view.repaintAll();
                }
            });
            animationTimer.start();     
    }    
}