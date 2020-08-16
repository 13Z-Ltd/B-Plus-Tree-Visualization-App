///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Models
// File:             AnimatedBubble.java
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;


public class AnimatedBubble {    
    private int keyNumber;
    private String operationType;    
    private String animationType;    
    private Color bubbleColor;
    
    private Point startPoint;
    private Point targetPoint;
    private Point locationPoint;
    private Point pointerLocation;
    private int newRightNodeTargetDistance;
    private int newLeftNodeTargetDistance;
    private boolean drawPointer;
    
    private BTreeNode currentNode;
    private BTreeNode newRightNode;
    private BTreeNode newLeftNode;
    private BTreeNode newParentNode;    
        
    public AnimatedBubble(String type) {
        this.keyNumber = 0;
        this.animationType = type;
        switch (animationType) {
            case "INSERT":
                bubbleColor = new Color(126, 156, 191);
                break;
            case "DELETE":
                bubbleColor = new Color(137, 89, 95);
                break;
            case "FIND":
                bubbleColor = new Color(228, 216, 32);
                break;                
        }
        
        locationPoint = null;
        startPoint = null;
        targetPoint = null;
        currentNode = null;
        drawPointer = false;
    }
    
    public void setCurrentNode(BTreeNode node) {
        currentNode = node;
    }
    
    public BTreeNode getCurrentNode() {
        return currentNode;
    }
    
    public int getCurrentNodeKeyCount() {
        return currentNode.getKeyCount();
    }
    
    public int getCurrentNodeKey(int index) {
        return currentNode.getKey(index);
    }
    
    public int getParentNodeKeyCount() {
        return currentNode.getParent().getKeyCount();
    }
    
    public int getParentNodeKey(int index) {
        return currentNode.getParent().getKey(index);
    }
    
    public int getParentNodeXCoordinate() {
        return currentNode.getParent().getX();
    }
    
    public int getParentNodeYCoordinate() {
        return currentNode.getParent().getY();
    }
    
    public String getCurrentNodeTypeString() {
        if (currentNode.getNodeType() == TreeNodeType.LeafNode)
            return "LeafNode";
        else
            return "InnerNode";
    }
    
    public int getNewRightNodeKeyCount() {
        if (newRightNode != null)
            return newRightNode.getKeyCount();
        else 
            return 0;
    }
    
    public int getCurrentNodeRightSiblingKeyCount() {
        if (currentNode.rightSibling != null)
            return currentNode.rightSibling.getKeyCount();
        else 
            return 0;
    }
    
    public int getCurrentNodeRightSiblingKey(int index) {
        if (currentNode.rightSibling != null)
            return currentNode.rightSibling.getKey(index);
        else 
            return -1;
    }
    
    public BTreeNode getRightSiblingNode() {
        return currentNode.rightSibling;
    }
    
    public BTreeNode getNewRightNode() {
        if (newRightNode != null)
            return newRightNode;
        else 
            return null;
    }
    
    public BTreeNode getNewParentNode() {
        if (newParentNode != null)
            return newParentNode;
        else 
            return null;
    }
    
    public String getAnimationType() {
        return animationType;
    }
    
    public int getKeyNumber() {
        return keyNumber;
    }
    
    public void setAllNodeNull(){
        currentNode = newLeftNode = newRightNode = newParentNode = null;
    }
    
    public void setAnimationType(String type) {
        this.animationType = type;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setNewParentNodeDeleteingBoxIndex(int i) {
        if (newParentNode != null)
            newParentNode.setDeleteingBoxIndex(i);
    }
    
    public void setCurrentNodeColor(Color color) {
        this.currentNode.setColor(color);
    }
    
    public void setBubbleColor(Color color) {
        this.bubbleColor = color;
    }
    
    public void setKeyNumber(int key) {
        this.keyNumber = key;
    }
    
    public Point getLocation() {
        return locationPoint;
    }

    public void setLocationPoint(Point location) {
        this.locationPoint = location;
    }
    
    public void setLocationX(int x) {
        this.locationPoint.x = x;
    }
    
    public void setLocationY(int y) {
        this.locationPoint.y = y;
    }
    
    public Point getStartPoint() {
        return startPoint;
    }
    
    public void setStartPoint(Point point) {
        this.startPoint = point;
    }
    
    public Point getTargetPoint() {
        return targetPoint;
    }
    
    public void setTheChildParentToNull(int i) {
        ((BTreeInnerNode)currentNode).getChild(i).setParent(null);
    }
    
    public int findKeyInTheNode() {
          return currentNode.search(getKeyNumber());
    }
    
    public void setTargetPoint(int x, int y) {
        this.targetPoint = new Point(x, y);
    }
    
    public void setCurrentLocation (Point currentLocation) {
        this.locationPoint = currentLocation;
    }
    
    public void setDrawPointer(boolean b) {
        this.drawPointer = b;
    }
    
    public void setBubblePoints() {
        targetPoint = new Point(currentNode.getX(), currentNode.getY());
        if (currentNode.getParent() == null) {
                startPoint = new Point(currentNode.getX(), currentNode.getY() - 60);
                locationPoint = startPoint;
        } else {
            startPoint = locationPoint;
        }
    }
    
    public void setParentToBubbleTarget(BTreeNode node) {
        if (node == null){
            targetPoint = new Point(locationPoint.x + 50, locationPoint.y - 40);
            startPoint = locationPoint;
        }else {
            targetPoint = new Point(node.getX(), node.getY());
            startPoint = locationPoint;
        }
    }
    
    public void setBubbleSentKeyFromParent(int direction) {
        int borrowerchildIndex = 0;
        while (borrowerchildIndex < currentNode.getParent().getKeyCount() + 1 && ((BTreeInnerNode)currentNode.getParent()).getChild(borrowerchildIndex) != currentNode)
            ++borrowerchildIndex;
          
        //give direction
        setKeyNumber(currentNode.getParent().getKey(borrowerchildIndex + (direction%10)));
        setBubbleColor(new Color(228, 216, 32));
        newParentNode = currentNode.getParent();
        newParentNode.setDeleteingBoxIndex(borrowerchildIndex + (direction%10));
        if (direction == 10) {
            newLeftNode = currentNode;
            newLeftNode.setColor(new Color(228, 216, 32));
            newRightNode = currentNode.rightSibling;
            newRightNode.setColor(new Color(175, 0, 2));
        }
        if (direction == -10) {
            newLeftNode = currentNode;
            newLeftNode.setColor(new Color(175, 0, 2));
            newRightNode = currentNode.rightSibling;
            newRightNode.setColor(new Color(228, 216, 32));
        }
        if (direction == 0) {
            newLeftNode = currentNode;
            newLeftNode.setColor(new Color(175, 0, 2));  
            newRightNode = currentNode.rightSibling;
            newRightNode.setColor(new Color(228, 216, 32));
        }
        if (direction == -1) {            
            newLeftNode = currentNode.leftSibling;
            newLeftNode.setColor(new Color(228, 216, 32));
            newRightNode = currentNode;
            newRightNode.setColor(new Color(175, 0, 2));
        }
        
        targetPoint = new Point(currentNode.getX(), currentNode.getY());
        if (currentNode.getParent() == null) {
                startPoint = new Point(currentNode.getX(), currentNode.getY() - 60);
                locationPoint = startPoint;
        } else {
            startPoint = new Point(currentNode.getParent().getX(), currentNode.getParent().getY());
            locationPoint = startPoint;
        }
    }    

    public void setInnerNodeFusionParameters() {
        if (newRightNode != null) {
            startPoint = new Point(newRightNode.getX(), newRightNode.getY());
            targetPoint = new Point(currentNode.getX() + 28 * (currentNode.getOrder() - 1), currentNode.getY());
        }
    }
    
    public void transferKeyAndChildFromSiblingNode() {
        if (newRightNode.getKeyCount() != 0){
            if (locationPoint != null && keyNumber != 0){
               currentNode.setKey(currentNode.getKeyCount(), keyNumber);
               ((BTreeInnerNode)currentNode).setChild(currentNode.getKeyCount() + 1, ((BTreeInnerNode)newRightNode).getChild(0));
               currentNode.keyCount++;
               locationPoint = null;
               newRightNode.setNotToDrawPointerIndex(0);
               return;
            }
            ((BTreeInnerNode)currentNode).insertAt(currentNode.getKeyCount(), newRightNode.getKey(0), ((BTreeInnerNode)currentNode).getChild(currentNode.getKeyCount()), ((BTreeInnerNode)newRightNode).getChild(1));
            ((BTreeInnerNode)newRightNode).deleteAt(0);
        }
    }
    
    public void makeNewParent() {
        newParentNode = new BTreeInnerNode(currentNode.getOrder());
        newParentNode.setX(currentNode.getX());
        newParentNode.setY(currentNode.getY() - 70);
        ((BTreeInnerNode)newParentNode).setChild(0, getCurrentNode());
        currentNode.setParent(newParentNode);
    }
    
    public void makeTheSplitingNodes() {
        setBubbleColor(new Color(126, 156, 191));  //Insert Blue
        if (currentNode.getNodeType() == TreeNodeType.LeafNode) {
            ((BTreeLeafNode)currentNode).insertKey(keyNumber, "(" + keyNumber + "Key) DATA");
            newRightNode = (BTreeLeafNode)currentNode.split();
            newRightNode.setParent(currentNode.getParent());

            //maintain links of sibling nodes
            newRightNode.setLeftSibling(currentNode);
            newRightNode.setRightSibling(currentNode.rightSibling);
            if(currentNode.getRightSibling() != null) {
                currentNode.getRightSibling().setLeftSibling(newRightNode);
            }
            currentNode.setRightSibling(newRightNode);
            newLeftNode = currentNode;
            keyNumber = newRightNode.getKey(0);                       
        }else {        
            //insert the new key
            int index = currentNode.search(keyNumber);
            if (currentNode.getParent().getKeyCount() > 0) {
                if (((BTreeInnerNode)currentNode).getChild(0).getNodeType() == TreeNodeType.LeafNode) {
                    ((BTreeInnerNode)currentNode).insertAt(index, keyNumber, newLeftNode, newRightNode);
                }
            } else {
                ((BTreeInnerNode)currentNode.getParent()).setChild(0, currentNode);
                //((BTreeInnerNode)currentNode).insertAt(index, keyNumber, newLeftNode, newRightNode);
            }
            keyNumber = currentNode.getKey(currentNode.getKeyCount()/2);
            
            newRightNode = (BTreeInnerNode)currentNode.split();
            newLeftNode = currentNode;
            setNewNodesTargetDistance();
            if (currentNode.getParent() != null) {
                newParentNode = new BTreeInnerNode(currentNode.getOrder());
                for (int i = 0; i < currentNode.getParent().getKeyCount(); i++) {
                    newParentNode.setKey(i, currentNode.getParent().getKey(i));
                    newParentNode.keyCount++;                    
                }
                newParentNode.setX(currentNode.getParent().getX());
                newParentNode.setY(currentNode.getParent().getY());

                int parentIndex = currentNode.getParent().search(keyNumber);
                ((BTreeInnerNode)currentNode.getParent()).insertAt(parentIndex, keyNumber, currentNode, newRightNode);
            }
        }
        setNewNodesTargetDistance();
    }
    
    public void borrowFromRightSibling() {
        if (currentNode.getNodeType() == TreeNodeType.LeafNode){
            newRightNode = currentNode.rightSibling;
            newRightNode.setColor(new Color(228, 216, 32));
            
            newLeftNode = currentNode;
            newLeftNode.setColor(new Color(175, 0, 2));            
            setBubbleColor(new Color(228, 216, 32));    //Find yellow)            
            locationPoint = new Point(newRightNode.getX(), newRightNode.getY());
            startPoint = locationPoint;
            targetPoint = new Point(currentNode.getX() + (currentNode.getKeyCount() * 26), currentNode.getY());
            keyNumber = newRightNode.getKey(0);
            ((BTreeLeafNode)newRightNode).delete(keyNumber);
        } 
    }
    
    public void borrowFromLeftSibling() {
        if (currentNode.getNodeType() == TreeNodeType.LeafNode){
            newRightNode = currentNode.leftSibling;
            
            newLeftNode = currentNode;
            newLeftNode.setColor(new Color(175, 0, 2));
            newRightNode.setColor(new Color(228, 216, 32));
            setBubbleColor(new Color(228, 216, 32));    //Find yellow)            
            locationPoint = new Point(newRightNode.getX() + (newRightNode.getKeyCount() - 1) * 26, newRightNode.getY());            
            startPoint = locationPoint;
            targetPoint = new Point(currentNode.getX() + (currentNode.getKeyCount() * 26), currentNode.getY());
            keyNumber = newRightNode.getKey(newRightNode.getKeyCount() - 1);
            ((BTreeLeafNode)newRightNode).delete(keyNumber);            
        }
    }
    
    public void borrowChildFromRightSibling() {
        newLeftNode = currentNode;
        newLeftNode.setColor(new Color(175, 0, 2));     //Red
        newRightNode = currentNode.rightSibling;           
        newRightNode.setColor(new Color(228, 216, 32));
        
        newParentNode = ((BTreeInnerNode)newRightNode).getChild(0);
        newParentNode.setColor(new Color(228, 216, 32));
        //Delete the first child pointer
        newRightNode.setNotToDrawPointerIndex(0);

        setBubbleColor(new Color(228, 216, 32));    //Find yellow)
        pointerLocation = new Point(newRightNode.getX(), newRightNode.getY());
        startPoint = pointerLocation;
        targetPoint = new Point(currentNode.getX() + (currentNode.getKeyCount() + 1) * 28, currentNode.getY());        
        setDrawPointer(true);
    }
    
    public void borrowChildFromLeftSibling() {
        newLeftNode = currentNode;
        newLeftNode.setColor(new Color(175, 0, 2));     //Red
        newRightNode = currentNode.leftSibling;           
        newRightNode.setColor(new Color(228, 216, 32));
        
        newParentNode = ((BTreeInnerNode)newRightNode).getChild(newRightNode.getKeyCount());
        newParentNode.setColor(new Color(228, 216, 32));
        //Delete the last child pointer
        newRightNode.setNotToDrawPointerIndex(newRightNode.getKeyCount());

        setBubbleColor(new Color(228, 216, 32));    //Find yellow)
        pointerLocation = new Point(newRightNode.getX() + (newRightNode.getKeyCount() * 28), newRightNode.getY());  
        startPoint = pointerLocation;
        targetPoint = new Point(currentNode.getX() + 10, currentNode.getY());
        setDrawPointer(true);
    }
    
    public void childrenFusionWithSibling() {
        if (currentNode.getNodeType() == TreeNodeType.LeafNode){
            setLocationPoint(null);
            newLeftNode = currentNode;            
            newRightNode = currentNode.rightSibling;
            newParentNode = currentNode.getParent();
            newParentNode.setDeleteingBoxIndex(newParentNode.search(keyNumber) - 1);            
            newLeftNode.setColor(new Color(175, 0, 2));
            newRightNode.setColor(new Color(175, 0, 2));
        }
    }
    
    public void slideAllOfTheRightSiblings() {
        BTreeNode rightSiblings = currentNode.getRightSibling();
        while (rightSiblings != null) {
            rightSiblings.setX(rightSiblings.getX() - 2);
            rightSiblings = rightSiblings.getRightSibling();
        }
    }
    
    public void insertKeyFromParent(int direction) {        
        if (direction == 0) {   //from right
            ((BTreeInnerNode)currentNode).insertAt(currentNode.getKeyCount(), keyNumber, ((BTreeInnerNode)currentNode).getChild(currentNode.getKeyCount()), null);            
        }
        if (direction == -1) {  //from left
            ((BTreeInnerNode)currentNode).insertAt(0, keyNumber, null, ((BTreeInnerNode)currentNode).getChild(0));         
        }
    }
    
    public void insertKeyToCurrentNode(int direction) {
        if (currentNode.getNodeType() == TreeNodeType.LeafNode){
            ((BTreeLeafNode)this.currentNode).insertKey(keyNumber, "(" + keyNumber + "Key) DATA");
        } else {
            if (direction == -1) {  //from left
                ((BTreeInnerNode)currentNode).insertAt(0, keyNumber, newParentNode, ((BTreeInnerNode)currentNode).getChild(0));                
                //Update key for multiple transfering in Inner Node
                keyNumber = newRightNode.getKey(newRightNode.getKeyCount() - 1);
                
                ((BTreeInnerNode)newRightNode).deleteAt(newRightNode.search(keyNumber));
            }
            if (direction == 0) {   //from right
                ((BTreeInnerNode)currentNode).insertAt(currentNode.getKeyCount(), keyNumber, ((BTreeInnerNode)currentNode).getChild(currentNode.getKeyCount()), newParentNode);
                ((BTreeInnerNode)newRightNode).deleteAt(-1);
            }
            setDrawPointer(false);
            getNewRightNode().notToDrawPointerIndex = -1;
            newRightNode = newParentNode = null;
            currentNode.setColor(new Color(65, 36, 23));
        }        
    }
    
    public void overWriteKeyInCurrentNode() {
        if (currentNode.getParent().search(keyNumber) == 0)
            currentNode.getParent().setKey(0, keyNumber);
        else
            currentNode.getParent().setKey(currentNode.getParent().search(keyNumber) - 1, keyNumber);
    }
    
    public void setStepUpNodeForAnimate() {
        newRightNode = currentNode;
        setStartPoint(new Point(getCurrentNode().getX() + currentNode.search(keyNumber) * 28, getCurrentNode().getY()));
        setTargetPoint(getCurrentNode().getParent().getX(), getCurrentNode().getParent().getY());
        newParentNode = currentNode.getParent();
        int insertingBoxIndex = 0;
        while (insertingBoxIndex < currentNode.getParent().getKeyCount() + 1 && ((BTreeInnerNode)currentNode.getParent()).getChild(insertingBoxIndex) != currentNode)
            insertingBoxIndex++;
        newParentNode.setDeleteingBoxIndex(insertingBoxIndex - 1);
        //newParentNode.setDeleteingBoxIndex(newParentNode.search(keyNumber));
    }
    
    public void deleteKeyForAnimation() {
        if (currentNode.getNodeType() == TreeNodeType.LeafNode){
            ((BTreeLeafNode)currentNode).delete(keyNumber);
            keyNumber = 0;
        }else {
            int index  = currentNode.search(keyNumber);
            if (index > 0) {
                index--;
            }
            ((BTreeInnerNode)currentNode).deleteAt(index);
            newLeftNode = null;
            newRightNode = null;
        }        
    }    
    
    public void slideBubble() {
        if (newRightNode != null)
            locationPoint = new Point(newRightNode.getX(), newRightNode.getY());
    }
    
    public void childNodesSlide() {
        if (currentNode.getNodeType() == TreeNodeType.LeafNode){
            currentNode.setX(currentNode.getX() - 2);
            if(currentNode.leftSibling != null) {
                BTreeNode siblingNode = currentNode.leftSibling;
                while (siblingNode != null) {
                    siblingNode.setX(siblingNode.getX() - 2);
                    siblingNode = siblingNode.leftSibling;                
                }
            }
            if (currentNode.rightSibling != null) {
                BTreeNode siblingNode = currentNode.rightSibling;                
                while (siblingNode != null) {
                    siblingNode.setX(siblingNode.getX() + 2);
                    siblingNode = siblingNode.rightSibling;                
                }
            }
        } else {
            newLeftNode.setX(newLeftNode.getX() + newLeftNodeTargetDistance / 50);
            newRightNode.setX(newRightNode.getX() + newRightNodeTargetDistance / 50);
        }
    }
    
    public void setNewNodesTargetDistance() {
        if (newRightNode.getNodeType() == TreeNodeType.InnerNode) {
            newRightNodeTargetDistance = ((BTreeInnerNode)newRightNode).getChild(newRightNode.getKeyCount()).getX() - newRightNode.getX();            
        }
        if (newLeftNode.getNodeType() == TreeNodeType.InnerNode) {
            newLeftNodeTargetDistance = ((BTreeInnerNode)newLeftNode).getChild(0).getX() - newLeftNode.getX();
        }   
    }    
    
    public  void update(float progress) {
        if (progress < 1){
            Point currentLocationPoint = new Point(0,0);
        
            if (startPoint != null && targetPoint != null) {
                currentLocationPoint.x = calculateProgress(startPoint.x, targetPoint.x, progress);
                currentLocationPoint.y = calculateProgress(startPoint.y, targetPoint.y, progress);
            }        
            setLocationPoint(currentLocationPoint);
        }
    }
    
    public  void update2(float progress) {
        if (progress < 1){
            Point currentLocationPoint = new Point(0,0);
        
            if (startPoint != null && targetPoint != null) {
                currentLocationPoint.x = calculateProgress(startPoint.x, targetPoint.x, progress);
                currentLocationPoint.y = calculateProgress(startPoint.y, targetPoint.y, progress);
            }        
            pointerLocation = currentLocationPoint;
        }
    }
    
    public void updateNewRightNodeLocation(float progress) {
        if (progress < 1 && newRightNode != null) {
            if (startPoint != null && targetPoint != null) {
                int distance = Math.abs(targetPoint.x - startPoint.x) / 50;
                targetPoint.x += distance;
                newRightNode.setX(calculateProgress(startPoint.x, targetPoint.x, progress));
                newLeftNode.setX(newLeftNode.getX() + distance);
                locationPoint.x += distance;
            }
        }
    }
    
    
    public int calculateProgress(int startValue, int targetValue, float progress) {
        int value = 0;
        int distance = targetValue - startValue;
        value = (int) Math.round((double) distance * progress);
        value += startValue;
        
        return value;
    }
    
    public void paint(int startCoordinateX, int startCoordinateY, Graphics2D g2d) {
        if (locationPoint != null && keyNumber != 0){
            g2d.setColor(bubbleColor);
            g2d.fillOval(locationPoint.x - 8 + startCoordinateX, locationPoint.y + startCoordinateY - 30, 35, 35);
            g2d.setColor(Color.white);
            g2d.setFont(new Font("SansSerif", Font.BOLD , 15));           
            if (keyNumber < 10)
                g2d.drawString("" + keyNumber, locationPoint.x + startCoordinateX + 6, locationPoint.y + startCoordinateY - 8);
            if (keyNumber < 100 && keyNumber > 9)
                g2d.drawString("" + keyNumber, locationPoint.x + startCoordinateX - 0, locationPoint.y + startCoordinateY - 8);            
            if (keyNumber > 99)
                g2d.drawString("" + keyNumber, locationPoint.x + startCoordinateX - 5, locationPoint.y + startCoordinateY - 8);            
        }
        if ("OverFlow".equals(operationType) || "UnderFlow".equals(operationType) || "Find".equals(operationType)) {
            currentNode.setColor(bubbleColor);
            currentNode.nodePainter(startCoordinateX, startCoordinateY, g2d);
        }
        if (newLeftNode != null){
            newLeftNode.nodePainter(startCoordinateX, startCoordinateY, g2d);
        }
        if (newRightNode != null && "INSERT".equals(animationType)){
            newRightNode.setColor(new Color(126, 156, 191));    //Insert blue
            newRightNode.nodePainter(startCoordinateX, startCoordinateY, g2d);
        }
        if (newParentNode != null) {
            newParentNode.nodePainter(startCoordinateX, startCoordinateY, g2d);
        }            
        if (newRightNode != null && "DELETE".equals(animationType)){
            newRightNode.nodePainter(startCoordinateX, startCoordinateY, g2d);
        }            
        if (drawPointer) {
            g2d.setColor(Color.BLACK);
            g2d.drawLine(pointerLocation.x + startCoordinateX + 2, pointerLocation.y + startCoordinateY + 32, newParentNode.getX() + (14 * (newParentNode.getOrder() - 1) + 3) + startCoordinateX, newParentNode.getY() + startCoordinateY);
        }
    }
}
