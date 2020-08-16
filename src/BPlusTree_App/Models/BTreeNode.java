///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Models
// File:             BTreeNode.java
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

enum TreeNodeType {
    InnerNode,
    LeafNode
}

/**
 * BTreeNode is an abstract class.
 * This is the basic class for the BtreeInnerNode and BTreeLeafNode classes,
 * it's includes the common properties and procedures.
 * And implements the Serializable interface for the the tree can be saved later in the file
 */
abstract class BTreeNode implements Serializable {
    
    protected Object[] keys;
    protected int keyCount;
    protected int order;
    protected int xCoordinate;
    protected int yCoordinate;
    protected int deleteingBoxIndex;
    protected int notToDrawPointerIndex;
    protected Color nodeColor;
    
    protected BTreeNode parentNode;
    protected BTreeNode leftSibling;
    protected BTreeNode rightSibling;    
    
    protected BTreeNode() {
        this.keyCount = 0;
        this.parentNode = null;
        this.leftSibling = null;
        this.rightSibling = null;
        this.xCoordinate = 0;
        this.yCoordinate = 0;
        this.deleteingBoxIndex = -1;
        this.notToDrawPointerIndex = -1;
        this.nodeColor = null;
    }
    
    
    public int getKeyCount() {
        return this.keyCount;
    }    
    
    @SuppressWarnings("unchecked")
    public int getKey(int index) {
        return (int)this.keys[index];
    }
    
    public void setKey(int index, int key) {
        this.keys[index] = key;
    }
    
    public int getOrder(){
        return order;
    }
    
    public BTreeNode getParent() {
        return this.parentNode;
    }
    
    public void setParent(BTreeNode parent) {
        this.parentNode = parent;
    }
    
    public Color getColor(){
        return nodeColor;
    }
    
    public void setColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }
    
    public void setX(int x) {
        this.xCoordinate = x;
    }
    
    public int getX() {
        return this.xCoordinate;
    }
    
    public void setY(int y) {
        this.yCoordinate = y;
    }
    
    public int getY() {
        return this.yCoordinate;
    }
    
    public void setDeleteingBoxIndex (int boxIndex) {
        this.deleteingBoxIndex = boxIndex;
    }
    
    public void setNotToDrawPointerIndex (int boxIndex) {
        this.notToDrawPointerIndex = boxIndex;
    }

    public void slideNodeVertical(int y) {
        this.yCoordinate += y;
    }

    public abstract TreeNodeType getNodeType();
    
    /**
     * Search a key on current node, if found the key then return it's position,
     * otherwise return -1 for a leaf node,
     * return the child node index wich should contain the key for a internal node.
     */    
    public abstract int search (int key);
    
    /*The codes below are used to support insertion operation*/
    public boolean isOverFlow() {
        return this.getKeyCount() == this.keys.length;
    }
    
    /**
     * The isUnderFlow will give back a boolean variable.
     * return true if the current node childrens count is less than half of the order of the tree bottom part
     * otherwise it return false.
     */ 
    public boolean isUnderFlow() {
        return this.getChildrensCount() < (this.keys.length / 2); //It can be differenet, depends on the current node type.
    }
    
    /**
     * The canLendAKey return True if the node key count is less than the order of the tree.
     * and the opposite if not
     */
    public boolean canLendAKey() {
        return this.getChildrensCount() > (this.keys.length / 2);
    }
    
    /**
     * dealOverFlow method is make a new node and split the keys between 
     * return with their parents after call pushUp the middle key.
     */
    public BTreeNode dealOverFlow() {
        int midIndex = this.getKeyCount() / 2;
        int upKey = this.getKey(midIndex);
        //Make a new Node and call the split() method to distribution the key between nodes
        BTreeNode newRNode = this.split();
        
        if(this.getParent() == null) {
            this.setParent(new BTreeInnerNode(this.getOrder()));
        }
        newRNode.setParent(this.getParent());        
        //maintain links of sibling nodes        
        newRNode.setLeftSibling(this);
        newRNode.setRightSibling(this.rightSibling);
        if(this.rightSibling != null) {
            this.rightSibling.setLeftSibling(newRNode);
        }
        this.setRightSibling(newRNode);        
        //push up a key to parent internal node
        return this.getParent().pushUpKey(upKey, this, newRNode);
    }
    
    /**
     * dealUnderFlow algorithm try to borrow a key from the current node siblings and if it's can not
     * start to merge with one of them
     */
    public BTreeNode dealUnderFlow() {
        if (this.getParent() == null)
            return null;        
        
        //try to borrow a key from sibling (felcserélve elösször a bal testvér aztán a jobb)        
        BTreeNode leftSibling = this.getLeftSibling();
        if (leftSibling != null && leftSibling.canLendAKey()) {
            this.getParent().processChildrenTransfer(this, leftSibling, leftSibling.getKeyCount() - 1);
            return null;
        }
        
        BTreeNode rightSibling = this.getRightSibling();
        if (rightSibling != null && rightSibling.canLendAKey()) {
            this.getParent().processChildrenTransfer(this, rightSibling, 0);
            return null;
        }
        
        //can not borrow a key from any sibling, then do fusion with sibling
        if (leftSibling != null) {
            return this.getParent().processChildrenFusion(leftSibling, this);
        }
        else {
            return this.getParent().processChildrenFusion(this, rightSibling);
        }
    }
    
    //The codes below are used to support deletion operation
    public BTreeNode getLeftSibling() {
        if(this.leftSibling != null && this.leftSibling.getParent() == this.getParent())
            return this.leftSibling;
        return null;
    }
    
    public void setLeftSibling(BTreeNode sibling) {
        this.leftSibling = sibling;
    }
    
    public BTreeNode getRightSibling() {
        if(this.rightSibling != null && this.rightSibling.getParent() == this.getParent())
            return this.rightSibling;
        return null;
    }

    public void setRightSibling(BTreeNode sibling) {
        this.rightSibling = sibling;
    }    
    
    protected abstract BTreeNode split();
    
    protected abstract BTreeNode pushUpKey(int key, BTreeNode leftChild, BTreeNode rightNode);
        
    protected abstract void processChildrenTransfer(BTreeNode borrower, BTreeNode lender, int borrowIndex);

    protected abstract BTreeNode processChildrenFusion(BTreeNode leftChild, BTreeNode rightChild);

    protected abstract void fusionWithSibling(int sinkKey, BTreeNode rightSibling);
    
    protected abstract int transferFromSibling(int sinkKey, BTreeNode sibling, int borrowIndex);
    
    protected abstract int getChildrensCount();
    
    // Paint procedure for one Node
    public void nodePainter(int modifiedStartCoordinateX, int nodeY, Graphics g) {
        int currentNodeX = modifiedStartCoordinateX + this.getX();
        int currentNodeY = nodeY + this.getY();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        //Draw the node box
        if (this.getNodeType() == TreeNodeType.LeafNode){
            g.setColor(new Color(82, 110, 42));             //Leef green
        } else {
            //g.setColor(new Color(102, 51, 0));            //Nutmeg wood
            g.setColor(new Color(65, 36, 23));            //Brown wedding 
        }
        if (nodeColor != null) {
            g2d.setColor(nodeColor);
        }
            g2d.fillRoundRect(currentNodeX , currentNodeY, 28 * (getOrder() - 1) + 7, 36, 10, 10);
            g2d.setColor(new Color(5, 5, 5));
            g2d.drawRoundRect(currentNodeX , currentNodeY, 28 * (getOrder() - 1) + 7, 36, 10, 10);

        //draw the nodes keys boxes 
        g2d.setColor(new Color(240, 240, 240));
        for (int i = 0; i < getOrder() - 1; i++) {
            if (i != deleteingBoxIndex){
                g2d.fillRoundRect(currentNodeX + (i * 28) + 6 , currentNodeY + 6, 23, 20, 5, 5);
            } else {
                g2d.setColor(new Color(175, 0, 2)); //Red
                g2d.fillRoundRect(currentNodeX + (i * 28) + 6 , currentNodeY + 6, 23, 20, 5, 5);
                g2d.setColor(new Color(240, 240, 240));
            }
        }

        //Draw the numbers
        Font font = new Font("Helvetica", Font.BOLD, 12);
        g2d.setColor(Color.black);
        g2d.setFont(font);
        for (int i = 0; i < this.getKeyCount(); i++) {                
            if (i < this.getOrder()-1) {
                if (i != deleteingBoxIndex){
                    g2d.drawString(Integer.toString(this.getKey(i)), currentNodeX + (i * 28) + 8 , currentNodeY + 22);
                } else {
                    g2d.setColor(new Color(240, 240, 240));
                    g2d.drawString(Integer.toString(this.getKey(i)), currentNodeX + (i * 28) + 8 , currentNodeY + 22);
                    g2d.setColor(Color.black);
                }
            }
        }

        //Draw the pointers for the next nodes or datafiles
        if (this.getNodeType() == TreeNodeType.LeafNode) {      //the data pointers
            for (int i = 0; i < getOrder() - 1; i++) {
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRoundRect(currentNodeX + (i * 28) + 8, currentNodeY + 29, 19, 4, 2, 2);

                //Draw data pointers arrow if it exist
                if (i < this.getKeyCount()) {
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
            g2d.setColor(new Color(240, 240, 240));
            for (int i = 0; i < getOrder(); i++) {
                g2d.fillOval(currentNodeX + (i * 28) + 1, currentNodeY + 29, 5, 5);
            }
        }
    }
}
