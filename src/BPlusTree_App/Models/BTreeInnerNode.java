///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Models
// File:             BTreeInnerNode.java
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
 * BTreeInnerNode is an extended BTreeNode class.
 * The tree structure is built from these they not contains Datas just the keys for the orientation
 * and the pointers for the childs
 */
public class BTreeInnerNode extends BTreeNode {
    protected int INNERORDER;
    protected Object[] children;
    
    
    public BTreeInnerNode(int orderOfTheTree) {
        this.INNERORDER = orderOfTheTree;
        this.keys = new Object[INNERORDER];
        this.children = new Object[INNERORDER + 1];        
        this.order = orderOfTheTree;
    }
     
    @SuppressWarnings("unchecked")
    public BTreeNode getChild(int index) {
        return (BTreeNode)this.children[index];
    }
    
    public void setChild(int index, BTreeNode child){
        this.children[index] = child;
        if (child != null)
            child.setParent(this);
    }    
   
    @Override
    public TreeNodeType getNodeType() {
        return TreeNodeType.InnerNode;
    }

    @Override
    public int search(int key) {
        int index = 0;
        for (index = 0; index < this.getKeyCount(); ++index) {
            if (this.getKey(index) == key) { //  cmp == 0
                return index + 1;
            } 
            else if (this.getKey(index) > key) { // cmp > 0
                return index;
            }
        }
        return index;
    }
        
    /* The codes below are used to support insertion operation */
    public void insertAt(int index, int key, BTreeNode leftChild, BTreeNode rightChild) {
        //move space for the new key
        for (int i = this.getKeyCount() + 1; i > index; --i) {
            this.setChild(i, this.getChild(i - 1));
        }
        for (int i = this.getKeyCount(); i > index; --i) {
            this.setKey(i, this.getKey(i - 1));
        }
        
        //insert the new key
        this.setKey(index, key);
        this.setChild(index, leftChild);
        this.setChild(index + 1, rightChild);
        this.keyCount += 1;
    }
    
    /**
     * when splits a internal node, the middle key is kicked out and be pushed to parent node
     * @return the new node which is made for split the datas
     */
    @Override
    protected BTreeNode split() {
        int midIndex = this.getKeyCount() / 2;
        
        BTreeInnerNode newRNode = new BTreeInnerNode(INNERORDER);
        for (int i = midIndex + 1; i < this.getKeyCount(); ++i) {
            newRNode.setKey(i - midIndex - 1, this.getKey(i));
            this.setKey(i, -1);
        }
        for(int i = midIndex + 1; i <= this.getKeyCount(); ++i) {
            newRNode.setChild(i - midIndex - 1, this.getChild(i));
            newRNode.getChild(i - midIndex - 1).setParent(newRNode);
            this.setChild(i, null);
        }
        this.setKey(midIndex, -1);
        newRNode.keyCount = this.getKeyCount() - midIndex - 1;
        this.keyCount = midIndex;
        
        //Set up the coordinates of the newRNode
        newRNode.setX(this.getX());
        newRNode.setY(this.getY());
        
        return newRNode;
    }
    
    /**
     * This method is set the given key and childs into the node
     * @param key   
     * @param leftChild
     * @param rightNode
     * @return the current node if it has no parrent or null
     */
    @Override
    protected BTreeNode pushUpKey(int key, BTreeNode leftChild, BTreeNode rightNode) {
        //fid the target position of the new key
        int index = this.search(key);        
        //insert the new key
        this.insertAt(index, key, leftChild, rightNode);
        
        //check whether current node need to be split
        if (this.isOverFlow()) {
            return this.dealOverFlow();
        }
        else {
            return this.getParent() == null ? this : null;
        }
    }
    
    /* The codes below are used to support delete operation */
    protected void deleteAt(int index) {
        //in this case we delete the first child and the first key
        if (index == -1) {
            int i = 0;
            for (i = 0; i < this.getKeyCount() - 1; ++i) {
		this.setKey(i, this.getKey(i + 1));
                this.setChild(i, this.getChild(i + 1));
            }
            this.setChild(i, this.getChild(i + 1));
            
            this.setKey(i, -1);
            this.setChild(i + 1, null);
            --this.keyCount;
        } else {
            int i = 0;
            for (i = index; i < this.getKeyCount() - 1; ++i) {
                    this.setKey(i, this.getKey(i + 1));
                    this.setChild(i + 1, this.getChild(i + 2));
            }
            this.setKey(i, -1);
            this.setChild(i + 1, null);
            --this.keyCount;
        }
    }
    
    /**
     * This process used to transfer a key and the associated children from one of the sibling
     * @param borrower  the node which gets the data 
     * @param lender    the node which gives the data
     * @param borrowIndex this index indicates left (-1) or right (0) sibling is about
     */
    @Override
    protected void processChildrenTransfer(BTreeNode borrower, BTreeNode lender, int borrowIndex) {
        int borrowerchildIndex = 0;
        while (borrowerchildIndex < this.getKeyCount() + 1 && this.getChild(borrowerchildIndex) != borrower)
            ++borrowerchildIndex;
        
        if(borrowIndex == 0) {
            //borrow a key from right sibling
            int upKey = borrower.transferFromSibling(this.getKey(borrowerchildIndex), lender, borrowIndex);
            this.setKey(borrowerchildIndex, upKey);
        }
        else {
            //borrow a key from left sibling
            int upKey = borrower.transferFromSibling(this.getKey(borrowerchildIndex - 1), lender, borrowIndex);
            this.setKey(borrowerchildIndex - 1, upKey);
        }
    }
    
    /**
     * FusionWithSibling process merge the current and the given nodes into one and delete unnecessary from the parent
     * @param leftChild 
     * @param rightChild
     * @return the results of the dealUnderflow method if it's needed 
     * or in case when the parent nod is the root node and it's key count equals 0 return the left childe
     * otherwise return with null
     */
    @Override
    protected BTreeNode processChildrenFusion(BTreeNode leftChild, BTreeNode rightChild) {
        int index = 0;
        while (index < this.getKeyCount() && this.getChild(index) != leftChild)
            ++index;
        int sinkKey = this.getKey(index);        
        //merge tow children and the sink key into the left child node
        leftChild.fusionWithSibling(sinkKey, rightChild);        
        // remove the sink key, keep the left child and abandon the right child
        this.deleteAt(index);
        
        // check wheter need to propagate borrow or fusion to parent
        if(this.isUnderFlow()) {
            if (this.getParent() == null) {
                if (this.getKeyCount() == 0) {
                    leftChild.setParent(null);
                    return leftChild;
                }
                else {
                    return null;
                }
            }
            return this.dealUnderFlow();
        }
        return null;
    }
    
    /**
     * FusionWithSibling method merge the current and the given nodes into one and delete the empty one.
     * @param sinkKey       first this key is set into the current node
     * @param rightSibling  the other node parameter for the fusion
     */
    @Override
    protected void fusionWithSibling(int sinkKey, BTreeNode rightSibling) {
        BTreeInnerNode rightSiblingNode = (BTreeInnerNode)rightSibling;        
        int j = this.getKeyCount();
        this.setKey(j++, sinkKey);
        
        for (int i = 0; i < rightSiblingNode.getKeyCount(); i++) {
            this.setKey(j + i, rightSiblingNode.getKey(i));
        }
        for (int i = 0; i < rightSiblingNode.getKeyCount() + 1; i++) {
            this.setChild(j + i, rightSiblingNode.getChild(i));
        }
        this.keyCount += 1 + rightSiblingNode.getKeyCount();
        
        this.setRightSibling(rightSiblingNode.rightSibling);
        if (rightSiblingNode.rightSibling != null)
            rightSiblingNode.rightSibling.setLeftSibling(this);
        //Move the nodes after a deleted node
    }
    
    /**
     * This codes used to transfer a key and Data pair from one of the sibling
     * @param sinkKey   this key is set into the current node after find the right index
     * @param sibling   the sibling who will add a key
     * @param borrowIndex this index indicates left (-1) or right (0) sibling is about
     * @return the current or the sibling node first key depends on the borrowIndex
     */
    @Override
    protected int transferFromSibling(int sinkKey, BTreeNode sibling, int borrowIndex) {
        BTreeInnerNode siblingNode = (BTreeInnerNode)sibling;
        int upKey = -1;
        if(borrowIndex == 0) {
            int numberOfTransferedChilds = (siblingNode.getChildrensCount() - this.getChildrensCount()) / 2;
            // borrow the first key from right sibling, append it to tail
            int index = this.getKeyCount();
            this.insertAt(index, sinkKey, this.getChild(index), siblingNode.getChild(0));
            
            for (int i = 1; i < numberOfTransferedChilds; i++) {
                int currentSinkKey = siblingNode.getKey(0); 
                siblingNode.deleteAt(-1);
                //borrowIndex--;
                this.insertAt(this.getKeyCount(), currentSinkKey, this.getChild(this.getKeyCount()), siblingNode.getChild(0));
            }            
            
            upKey = siblingNode.getKey(0);
            //Delete the first key and the child from the borrower node
            //Here at deletion, the -1 index shows(which does not even allowed) for the algorithm that the 0 key and the 0 child must be deleted
            siblingNode.deleteAt(-1); 
        }
        else {
            //we insert to this node into the first place the last child of the left sibling and the given key fromt the parent
            int numberOfTransferedChilds = (siblingNode.getChildrensCount() - this.getChildrensCount()) / 2;
            
            this.insertAt(0, sinkKey, siblingNode.getChild(siblingNode.getKeyCount()), this.getChild(0));
            
            for (int i = 1; i < numberOfTransferedChilds; i++) {
                int currentUpKey = siblingNode.getKey(borrowIndex); 
                siblingNode.deleteAt(borrowIndex);
                borrowIndex--;
                this.insertAt(0, currentUpKey, siblingNode.getChild(siblingNode.getKeyCount()), this.getChild(0));
            }
            upKey = siblingNode.getKey(borrowIndex);            
            //Delete the last key and child from the borrower node
            siblingNode.deleteAt(borrowIndex);
        }        
        return upKey;
    }

    //In the Inner Nodes the childrens number is one more than the number of keys
    @Override
    protected int getChildrensCount() {
        return this.getKeyCount() + 1;
    }
    
    public void searchAndInsert(int key) {
        this.insertAt(this.search(key), key, null, null);
    }
}
