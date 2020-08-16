///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Models
// File:             BPlusTree_Model.java
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

package BPlusTree_App.Models;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;

/**
 * A B+ tree
 * Since the structures and behavior between internal node and external node are different,
 * so there are two different classes for each kinde of node
 */
public class BPlusTree_Model implements Serializable {
    protected int orderOfTheTree;
    private BTreeNode root;
    private int numberOfTheKeys;
    private int numberOfTheLeaves;
    private int numberOfTheNodes;
    private int heightOfTheTree;
    private int currentLeafNodeX;
    
    
    public BPlusTree_Model(int orderOfTheTree) {
        this.orderOfTheTree = orderOfTheTree;
        this.root = new BTreeLeafNode(orderOfTheTree);
        numberOfTheKeys = 0;
        numberOfTheNodes = 0;
        numberOfTheLeaves = 0;
        heightOfTheTree = 0;
    }
    
    public int getOrderOfTheTree() {
       return orderOfTheTree;
    }
    
    public BTreeNode getRoot() {
        return root;
    }
    
    public void setRoot(BTreeNode newRoot) {
        this.root = newRoot;
    }
    
    public int getNumberOfTheKeys() {
        return numberOfTheKeys;
    }
    
    public int getNumberOfTheInnerNodes() {
        numberOfTheNodes = 0;
        innerNodeCounter(root);
        return numberOfTheNodes;
    }
    
    public int getNumberOfTheLeaves() {
        return numberOfTheLeaves;
    }
    
    public int getHeightOfTheTree() {
        return heightOfTheTree;
    }
    
    public int getRootXCoordinate() {
        return getRoot().getX();
    }
        
    /**
     * Insert a new key and its associated value into the b+ tree
     * @param key
     * @param value
     */
    public void insert(int key, String value) {
        numberOfTheKeys++;
            BTreeLeafNode leaf = this.findLeafNodeShouldContainKey(key);
            leaf.insertKey(key, value);

            if(leaf.isOverFlow()) {
                BTreeNode n = leaf.dealOverFlow();
                if (n != null) {
                    this.root = n;
                    if (this.root.getKeyCount() == 1)
                    {
                        //When we have a new root node all the other nodes Y coordinates need to change
                        moveUp_DownTheTree(70);
                        heightOfTheTree++;
                    }
                }
                
                //When we add a new leaf to the tree we need to re-positioned the leefs
                if (this.root.getNodeType() != TreeNodeType.LeafNode)                    
                    resetLeefPositions(root);
            }
            numberOfTheLeaves = 0;
            leafNodeCounter(root);
    }
    
    /**
     * Search a key value on the tree and return its associated value
     * @param key
     * @return 
     */
    public int search(int key) {
        BTreeLeafNode leaf = this.findLeafNodeShouldContainKey(key);
        
        int index = leaf.search(key);
        return (index == -1) ? -1 : leaf.getKey(index);
    }
    
    /**
     * Delete a key and its associated value from the tree
     * @param key
     */
    public void delete(int key) {
        numberOfTheKeys--;
        BTreeLeafNode leaf = this.findLeafNodeShouldContainKey(key);
        
        if (leaf.delete(key) && leaf.isUnderFlow()) {
            BTreeNode n = leaf.dealUnderFlow();
            if (n != null) {
                this.root = n;
                if (n.getNodeType() == TreeNodeType.InnerNode )
                    moveUp_DownTheTree(-70);
                else 
                    n.setY(n.getY() -70);
                heightOfTheTree--;
            }
            currentLeafNodeX = 0;
            resetLeefPositions(root);
        }
        numberOfTheLeaves = 0;
        leafNodeCounter(root);
    }
    
    public boolean treeIsEmpty()
    { 
        if (root.getKeyCount() == 0) {
            if ( root.getNodeType() == TreeNodeType.InnerNode)
                if (((BTreeInnerNode)root).getChild(0) != null) {
                    return false;
                }
            return true;
        }
        else
            return false;        
    }    
    
    // This process counts the number of the leaf nodes in the tree
    public void leafNodeCounter(BTreeNode node) {
        if(node.getNodeType() == TreeNodeType.LeafNode){
            numberOfTheLeaves++;
        } else {
            BTreeInnerNode innerNode = (BTreeInnerNode)node;
            for (int i = 0; i < node.getKeyCount() + 1; i++) {
                leafNodeCounter(innerNode.getChild(i));
            }
        }
    }
    
    // This process counts the number of the inner nodes in the tree
    public void innerNodeCounter(BTreeNode node) {
        if(node.getNodeType() == TreeNodeType.InnerNode){
            numberOfTheNodes++;
            BTreeInnerNode innerNode = (BTreeInnerNode)node;
            for (int i = 0; i < node.getKeyCount() + 1; i++) {
                innerNodeCounter(innerNode.getChild(i));
            }
        } 
    }
    
    /**
     * Search the leaf node wich should contain the specified key
     */
    @SuppressWarnings("unchecked")
    private BTreeLeafNode findLeafNodeShouldContainKey(int key) {
        BTreeNode node = this.root;
        while (node.getNodeType() == TreeNodeType.InnerNode) {
            node = ((BTreeInnerNode)node).getChild(node.search(key));
        }
        return (BTreeLeafNode)node;
    }
    
    /**
     * This method will make a list for the animation that contains all the steps 
     * what are required to insert the specified key in parameter
     * @param key
     * @return the list itself
     */
    public ListOfAnimationData routeForTheAnimationInsert(int key) {
        ListOfAnimationData list = new ListOfAnimationData();
        BTreeNode node = this.root;
        list.addAnimationStep(node, key, "StepDown");
        
        while (node.getNodeType() == TreeNodeType.InnerNode) {
            node = ((BTreeInnerNode)node).getChild(node.search(key));
            list.addAnimationStep(node, key, "StepDown");
        }
        //ifnodeisoverflow add a new node to the list
        if(node.getKeyCount() == orderOfTheTree - 1){
            list.addAnimationStep(node, key, "OverFlow");            
            if (node.getParent() == null)
                list.addAnimationStep(node, key, "SplitNodeAddNewParent");
            else
                list.addAnimationStep(node, key, "SplitNode");
                
            list.addAnimationStep(node, key, "StepUp");
        
            while (node.getParent() != null && node.getParent().getKeyCount() == orderOfTheTree - 1) {
                node = node.getParent();
                list.addAnimationStep(node, key, "OverFlow"); 

                if (node.getParent() == null)
                    list.addAnimationStep(node, key, "SplitNodeAddNewParent");
                else
                    list.addAnimationStep(node, key, "SplitNode");

                list.addAnimationStep(node, key, "StepUp"); 
            }
        }
        return list;
    }
    
    /**
     * This method will make a list for the animation that contains all the steps 
     * what are required in the specified key finding process.
     * @param key
     * @return the list itself
     */
    public ListOfAnimationData routeForTheAnimationFind(int key) {
        ListOfAnimationData list = new ListOfAnimationData();
        BTreeNode node = this.root;
        list.addAnimationStep(node, key, "StepDown");
        
        while (node.getNodeType() == TreeNodeType.InnerNode) {
            node = ((BTreeInnerNode)node).getChild(node.search(key));
            list.addAnimationStep(node, key, "StepDown");
        }        
        list.addAnimationStep(node, key, "Find");            
        return list;
    }
    
    /**
     * This method will make a list for the animation that contains all the steps 
     * what are required to complete the deletion
     * @param key
     * @return the list itself
     */
    public ListOfAnimationData routeForTheAnimationDelete(int key) {
        ListOfAnimationData list = new ListOfAnimationData();
        BTreeNode node = this.root;
        list.addAnimationStep(node, key, "StepDown");
        
        while (node.getNodeType() == TreeNodeType.InnerNode) {
            node = ((BTreeInnerNode)node).getChild(node.search(key));
            list.addAnimationStep(node, key, "StepDown");
        }
        oneStepInTheAnimationDelete(node, key, list);
        
        return list;
    }
    
    /**
     * This step can be called recursively if it's needed in the list for delete animation
     * @param node  the recursive parameter which gives the node to be tested
     * @param key
     * @param list what have made so far
     */
    public void oneStepInTheAnimationDelete(BTreeNode node, int key, ListOfAnimationData list) {
        if(node.getNodeType() == TreeNodeType.LeafNode) {
            if (node == this.root)
                return;
            if(node.getChildrensCount() <= orderOfTheTree / 2){
                if (node.leftSibling != null && node.leftSibling.canLendAKey() && node.getParent() == node.leftSibling.getParent() || 
                        node.rightSibling != null && node.rightSibling.canLendAKey() && node.getParent() == node.rightSibling.getParent()) {
                    if ( node.leftSibling != null && node.leftSibling.canLendAKey() && node.getParent() == node.leftSibling.getParent()) {
                        list.addAnimationStep(node, key, "UnderFlow");
                        int numberOfTheTransferedKeys = (node.leftSibling.getKeyCount() - (node.getKeyCount() - 1)) / 2;
                        for (int i = 0; i < numberOfTheTransferedKeys; i++) {
                            list.addAnimationStep(node, -1, "BorrowFromSibling");
                        }
                        list.addAnimationStep(node, node.leftSibling.getKey(node.leftSibling.getKeyCount() - numberOfTheTransferedKeys), "DeleteStepUp");
                    } else {
                        list.addAnimationStep(node, key, "UnderFlow");
                        int numberOfTheTransferedKeys = (node.rightSibling.getKeyCount() - (node.getKeyCount() - 1)) / 2;
                        for (int i = 0; i < numberOfTheTransferedKeys; i++) {
                            list.addAnimationStep(node, 0, "BorrowFromSibling");
                        }
                        list.addAnimationStep(node.rightSibling, node.rightSibling.getKey(numberOfTheTransferedKeys), "DeleteStepUp");
                    }
                } else {                    
                    if (node.leftSibling != null && node.getParent() == node.leftSibling.getParent()){  //left
                        list.addAnimationStep(node, key, "UnderFlow");
                        list.addAnimationStep(node.leftSibling, node.getKey(0), "ChildrenFusion");
                        list.addAnimationStep(node, 0, "DeleteChild");
                    } else {                                                                            //right
                        list.addAnimationStep(node, key, "UnderFlow");
                        list.addAnimationStep(node, node.rightSibling.getKey(0), "ChildrenFusion");
                        list.addAnimationStep(node.rightSibling, 0, "DeleteChild");
                    }
                    
                    oneStepInTheAnimationDelete(node.getParent(), key, list);
                }
            }
        } else {
            if (node == this.root && node.getKeyCount() == 1) {
                list.addAnimationStep(node, -1, "UnderFlow");
                list.addAnimationStep(node, 0, "DeleteChild");
                list.addAnimationStep(node, key, "ParentDelete");
                return;
            }
            if(node.getChildrensCount() <= orderOfTheTree / 2){                
                if (node.leftSibling != null && node.leftSibling.canLendAKey() && node.getParent() == node.leftSibling.getParent() || 
                        node.rightSibling != null && node.rightSibling.canLendAKey() && node.getParent() == node.rightSibling.getParent()) {
                    if ( node.leftSibling != null && node.leftSibling.canLendAKey() && node.getParent() == node.leftSibling.getParent()) {
                        list.addAnimationStep(node, key, "UnderFlow");
                        list.addAnimationStep(node, -1, "KeyFromParrent");
                        int numberOfTheTransferedCilds = (node.leftSibling.getKeyCount() - (node.getKeyCount() - 1)) / 2;
                        for (int i = 0; i < numberOfTheTransferedCilds; i++) {
                            list.addAnimationStep(node, -1, "BorrowChildFromSibling");  //Left
                            //The left child last key goes up to the parent
                            list.addAnimationStep(node.leftSibling, ((BTreeInnerNode)node.leftSibling).getKey(node.leftSibling.getKeyCount() - (i + 1)), "DeleteStepUp");                       
                            if (i < numberOfTheTransferedCilds - 1)
                                list.addAnimationStep(node, -1, "KeyFromParrent");
                        }                     
                    } else {
                        list.addAnimationStep(node, key, "UnderFlow");
                        list.addAnimationStep(node, 0, "KeyFromParrent");
                        int numberOfTheTransferedCilds = (node.rightSibling.getKeyCount() - (node.getKeyCount() - 1)) / 2;
                        for (int i = 0; i < numberOfTheTransferedCilds; i++) {
                            list.addAnimationStep(node, 0, "BorrowChildFromSibling");   //Right
                            //The right sibling current first key will sended up in the animation 
                            list.addAnimationStep(node.rightSibling, ((BTreeInnerNode)node.rightSibling).getKey(i), "DeleteStepUp");
                            if (i < numberOfTheTransferedCilds - 1)
                                list.addAnimationStep(node, 0, "KeyFromParrent");
                        }
                    }
                } else {
                    //this starts when need to fusion with on of the siblings
                    if (node.leftSibling != null && node.getParent() == node.leftSibling.getParent() ) {
                        
                        list.addAnimationStep(node, key, "UnderFlow");
                        //It send a key to the left sibling and the "10" parameter helps the process to select the color of the node
                        list.addAnimationStep(node.leftSibling, 10, "KeyFromParrent");
                        list.addAnimationStep(node.leftSibling, -1, "InnerNodeFusion");
                        list.addAnimationStep(node, 0, "DeleteChild");
                    } else {
                        list.addAnimationStep(node, key, "UnderFlow");
                        //It send a key to the right sibling and the "10" parameter helps the process to select the color of the node
                        list.addAnimationStep(node, -10, "KeyFromParrent");
                        list.addAnimationStep(node, -1, "InnerNodeFusion");
                        list.addAnimationStep(node.rightSibling, 0, "DeleteChild");
                    }
                    
                    oneStepInTheAnimationDelete(node.getParent(), key, list);
                }
            }
        }
    }
        
    /**
     * Setting for the tree nodes coordinates
     * This recoursive algorithm seting up the tree nodes coordinate for drawing
     * @param node 
     */
    public void resetLeefPositions(BTreeNode node) {
        if(node.getNodeType() == TreeNodeType.LeafNode){
            node.setX(currentLeafNodeX);
            //Gap size dipends on the orderOfTheTree
            currentLeafNodeX += (28 * (orderOfTheTree) + 5);
        } else {
            BTreeInnerNode innerNode = (BTreeInnerNode)node;
            for (int i = 0; i < node.getKeyCount() + 1; i++) {
                resetLeefPositions(innerNode.getChild(i));
            }
            node.setX((innerNode.getChild(0).getX() + innerNode.getChild(node.getKeyCount()).getX()) / 2);
        }
    }
    
    // With this codes below can move up or down the tree nodes coordinates (Y) 
    public void moveUp_DownTheTree(int y) {
        BTreeInnerNode innerNode = (BTreeInnerNode)this.root;
        for (int i = 0; i <= innerNode.getKeyCount(); i++) {
                moveUp_DownNodes(innerNode.getChild(i), y);
        }
        if(y < 0){          //if it's move up
            this.root.setX(0);
            this.root.setY(0);
        }
    }
    
    public void moveUp_DownNodes(BTreeNode node, int y) {
        if (node.getNodeType() == TreeNodeType.InnerNode){
            BTreeInnerNode innerNode = (BTreeInnerNode)node;
            for (int i = 0; i <= node.getKeyCount(); i++) {
                moveUp_DownNodes(innerNode.getChild(i), y);
            }
        }
        node.slideNodeVertical(y);
    }
    
    public void adjustInnerNodes(BTreeNode node) {
        BTreeInnerNode innerNode = (BTreeInnerNode) node;
        if (innerNode.getChild(0).getNodeType() == TreeNodeType.LeafNode){
            node.setX((innerNode.getChild(0).getX() + innerNode.getChild(node.getKeyCount()).getX()) / 2);
        }
        else {
            for (int i = 0; i < innerNode.getKeyCount() + 1; i++) {
                adjustInnerNodes(innerNode.getChild(i));
            }
        }
    }
    
    /**
     * Original Painters for the tree 
     * @param startCoordinateX
     * @param startCoordinateY
     * @param g
     */    
    public void paintTree(int startCoordinateX, int startCoordinateY, Graphics g) {
        if(treeIsEmpty()){
            g.setColor(new Color(240, 240, 240));
            g.fillRoundRect(startCoordinateX - 120, 150, 350, 50, 7, 7);
            g.setColor(new Color(5, 5, 5));
            g.drawRoundRect(startCoordinateX - 120, 150, 350, 50, 7, 7);
        
            Font font = new Font("Helvetica", Font.PLAIN, 20);
            g.setColor(Color.black);
            g.setFont(font);
            g.drawString("The tree is empty.", startCoordinateX - 25, 180);
            //g.drawString("Not seeing the forest for the trees!", startCoordinateX - 100, 180);            
        }
        else {
            paintNode(root, startCoordinateX - root.getX(), startCoordinateY, g);
        }
    }
    
    /**
     * Painter for the nodes in the tree
     * @param node  the current node parameter
     * @param xAxisDifference  modified x coordinate parameter when the tree is moved
     * @param yAxisDifference  modified y coordinate parameter when the tree is moved
     * @param g 
     */
    public void paintNode(BTreeNode node, int xAxisDifference, int yAxisDifference, Graphics g) {
        int currentNodeX = xAxisDifference + node.getX();
        int currentNodeY = yAxisDifference + node.getY();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
            //Draw the node box
            if (node.getNodeType() == TreeNodeType.LeafNode){
                g.setColor(new Color(82, 110, 42));             //Leef green 
            } else {
                //g.setColor(new Color(102, 51, 0));            //Nutmeg wood
                g.setColor(new Color(65, 36, 23));            //Brown wedding 
            }
                g.fillRoundRect(currentNodeX , currentNodeY, 28 * (orderOfTheTree - 1) + 7, 36, 10, 10);
                g.setColor(new Color(5, 5, 5));
                g.drawRoundRect(currentNodeX , currentNodeY, 28 * (orderOfTheTree - 1) + 7, 36, 10, 10);
        
            //draw the nodes keys boxes 
            g.setColor(new Color(240, 240, 240));
            for (int i = 0; i < orderOfTheTree - 1; i++) {
                g.fillRoundRect(currentNodeX + (i * 28) + 6 , currentNodeY + 6, 23, 20, 5, 5);
            }
        
            //Draw the numbers
            Font font = new Font("Helvetica", Font.BOLD, 12);
            g.setColor(Color.black);
            g.setFont(font);
            for (int i = 0; i < node.getKeyCount(); i++) {
                if (i < node.getOrder()-1)
                    g.drawString(Integer.toString(node.getKey(i)), currentNodeX + (i * 28) + 8 , currentNodeY + 22);
            }
        
            //draw the pointers for the next nodes or datafiles
            if (node.getNodeType() == TreeNodeType.LeafNode) {      //the data pointers
                for (int i = 0; i < orderOfTheTree - 1; i++) {
                    g.setColor(new Color(240, 240, 240));
                    g.fillRoundRect(currentNodeX + (i * 28) + 8, currentNodeY + 29, 19, 4, 2, 2);
                    
                    //Draw data pointers arrow if it exist
                    if (i < node.getKeyCount()) {
                        g2d.setColor(Color.black);
                        BasicStroke defaultStroke = (BasicStroke)g2d.getStroke();
                        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, new float[]{1f, 1f, 1f}, 2f));
                        g2d.drawLine(currentNodeX + (i * 28) + 17, currentNodeY + 31, currentNodeX + (i * 28) + 17, currentNodeY + 48);
                        g2d.drawLine(currentNodeX + (i * 28) + 17, currentNodeY + 48, currentNodeX + (i * 28) + 12, currentNodeY + 43);
                        g2d.drawLine(currentNodeX + (i * 28) + 17, currentNodeY + 48, currentNodeX + (i * 28) + 22, currentNodeY + 43);
                        g2d.setStroke(defaultStroke);
                    }
                }
            } else {                                                //the node ponters
                g.setColor(new Color(240, 240, 240));
                for (int i = 0; i < orderOfTheTree; i++) {
                    g.fillOval(currentNodeX + (i * 28) + 1, currentNodeY + 29, 5, 5);
                }
            }
            
            //Set the position for the next nodes
            if (node.getNodeType() == TreeNodeType.InnerNode){
                BTreeInnerNode innerNode = (BTreeInnerNode)node;
                int childrens = node.getKeyCount() + 1;
                for (int i = 0; i < childrens; i++) {
                    //Draw lines to the cildren nodes
                    g2d.setColor(Color.black);
                    if (i != node.notToDrawPointerIndex)
                        g2d.drawLine(currentNodeX + (i * 28) + 4, currentNodeY + 33, xAxisDifference + innerNode.getChild(i).getX() + (14 * (orderOfTheTree - 1) + 3), yAxisDifference + innerNode.getChild(i).getY());

                    paintNode(innerNode.getChild(i), xAxisDifference, yAxisDifference, g);
                }
            }
    }
}
