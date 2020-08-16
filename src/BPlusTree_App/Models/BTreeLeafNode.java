///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Models
// File:             BTreeLeafNode.java
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

/**
 * BTreeLeafNode is an extended BTreeNode class.
 * They are at the bottom of the tree and they contains the key for search
 * and the real data or it"s pointer.
 */
public class BTreeLeafNode extends BTreeNode {
    protected int LEAFORDER;
    private Object[] values;        //The tree can fill with any type of data just have to make the setValue and getValue methods
                                    //In this case i'll enter the "DATA" String for the simplicity.
    
    public BTreeLeafNode() {
    }
    
    public BTreeLeafNode(int orderOfTheTree) {
        LEAFORDER = orderOfTheTree;
        this.keys = new Object[LEAFORDER];
        this.values = new Object[LEAFORDER];
        
        this.order = orderOfTheTree;
    }

    @SuppressWarnings("Unchecked")
    public void setValue(int index, String value) {
        this.values[index] = value;
    }
    
    @SuppressWarnings("Unchecked")
    public String getValue(int index) {
        return (String)this.values[index];
    }

    @Override
    public TreeNodeType getNodeType() {
        return TreeNodeType.LeafNode;
    }

    @Override
    public int search(int key) {
        for (int i = 0; i < this.getKeyCount(); ++i) {

            if(this.getKey(i) == key) {
                return i;
            }
            else if(this.getKey(i) > key) {
                return -1;
            }
        }
        return -1;
    }
    
    public void insertKey(int key, String value) {
        int index = 0;
        
        while(index < this.getKeyCount() && this.getKey(index) < key) {
            ++index;
        }
        this.insertAt(index,  key,  value);
    }
    
    private void insertAt(int index, int key, String value) {
        //Move space to the new key
        for (int i = this.getKeyCount() - 1; i >= index; --i) {
            this.setKey(i + 1, this.getKey(i));
            this.setValue(i + 1, this.getValue(i));
        }
        
        //Insert new key and value
        this.setKey(index, key);
        this.setValue(index, value);
        ++this.keyCount;
    }

    /**
     * When splits a leaf node, the middle key is kept in new node and be pushed to parent node
     * @return the new node which is made for split the datas
     */
    @Override
    protected BTreeNode split() {
        int midIndex = this.getKeyCount() / 2;
        
        BTreeLeafNode newRNode = new BTreeLeafNode(LEAFORDER);
        for (int i = midIndex; i < this.getKeyCount(); ++i) {
            newRNode.setKey(i - midIndex, this.getKey(i));
            newRNode.setValue(i - midIndex, this.getValue(i));
            this.setKey(i, -1);
            this.setValue(i, null);
        }
        newRNode.keyCount = this.getKeyCount() - midIndex;
        this.keyCount = midIndex;
        
        //Set up the coordinates of the newRNode
        newRNode.setX(this.getX());
        newRNode.setY(this.getY());
        
        return newRNode;
    }

    @Override
    protected BTreeNode pushUpKey(int key, BTreeNode leftChild, BTreeNode rightNode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void processChildrenTransfer(BTreeNode borrower, BTreeNode lender, int borrowIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected BTreeNode processChildrenFusion(BTreeNode leftChild, BTreeNode rightChild) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /*The codes below used to support deletion operation*/
    public boolean delete(int key) {
        int index = this.search(key);
        if(index == -1)
            return false;
        this.deleteAt(index);
        return true;
    }
    
    private void deleteAt(int index) {
        int i = index;
        for (i = index;  i < this.getKeyCount() - 1; ++i) {
            this.setKey(i, this.getKey(i + 1));
            this.setValue(i, this.getValue(i + 1));
        }
        this.setKey(i, -1);
        this.setValue(i, null);
        --this.keyCount;
    }

    /**
     * FusionWithSibling method merge the current and the given nodes into one and delete the empty one.
     * @param sinkKey       in the Leaf node version no need to use it
     * @param rightSibling  the other node parameter
     */
    @Override
    protected void fusionWithSibling(int sinkKey, BTreeNode rightSibling) {
        BTreeLeafNode siblingLeaf = (BTreeLeafNode)rightSibling;
        
        int j = this.getKeyCount();
        for (int i = 0; i < siblingLeaf.getKeyCount(); ++i) {
            this.setKey(j + i, siblingLeaf.getKey(i));
            this.setValue(j + i, siblingLeaf.getValue(i));
        }
        this.keyCount += siblingLeaf.getKeyCount();
        
        this.setRightSibling(siblingLeaf.rightSibling);
        if (siblingLeaf.rightSibling != null)
            siblingLeaf.rightSibling.setLeftSibling(this);
    }
    
    /**
     * This codes used to transfer a key and Data pair from one of the sibling
     * @param sinkKey   in the Leaf node version no need to use it
     * @param sibling   the sibling who will add a key
     * @param borrowIndex this index indicates left (-1) or right (0) sibling is about
     * @return the current or the sibling node first key depends on the borrowIndex
     */
    @Override
    @SuppressWarnings("unchecked")
    protected int transferFromSibling(int sinkKey, BTreeNode sibling, int borrowIndex) {
        BTreeLeafNode siblingNode = (BTreeLeafNode)sibling;
        
        int numberOfTheTransferedKeys = (siblingNode.getKeyCount() - this.getKeyCount()) / 2;
        if (borrowIndex == 0) { //from right
            for (int i = 0; i < numberOfTheTransferedKeys; i++) {
                this.insertKey(siblingNode.getKey(borrowIndex), siblingNode.getValue(borrowIndex));
                siblingNode.deleteAt(borrowIndex);                
            }
        } else {                //from left
            for (int i = 0; i < numberOfTheTransferedKeys; i++) {
                this.insertKey(siblingNode.getKey(borrowIndex), siblingNode.getValue(borrowIndex));
                siblingNode.deleteAt(borrowIndex);
                borrowIndex--;  
                //with this we can transfer as much keys as we need
            }
        }
        return borrowIndex == 0 ? sibling.getKey(0) : this.getKey(0);
    }
    
    // In the LeafNodes the key count and the childrens count are equal
    @Override
    protected int getChildrensCount() {
        return this.getKeyCount();
    }
    
}
