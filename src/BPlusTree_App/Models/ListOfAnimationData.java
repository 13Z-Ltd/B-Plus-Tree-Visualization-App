///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Models
// File:             ListOfAnimationData.java
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

import java.util.ArrayList;

/**
 * This class required to create the list which contains the steps of the current animation
 * @author Balla
 */
public class ListOfAnimationData {
    private ArrayList<AnimationData> animationData;
    
    private int numberOfSteps;
    private int currentAnimationIndex;
    
    public ListOfAnimationData() {
        animationData = new ArrayList<>();
        numberOfSteps = 0;
        currentAnimationIndex = -1;
    }
    
    public int getNumberOfSteps() {
        return numberOfSteps;
    }
    
    public int getCurrentAnimationIndex() {
        return currentAnimationIndex;
    }
    
    public BTreeNode getTheCurrentAnimationNode() {
        return animationData.get(currentAnimationIndex).getNode();
    }
    
    public int getTheCurrentAnimationKey() {
        return animationData.get(currentAnimationIndex).getKey();
    }
    
    public String getTheCurrentAnimationOperation() {
        return animationData.get(currentAnimationIndex).getOperation();
    }
    
    public void addAnimationStep(BTreeNode currentNode, int key, String operation) {
        numberOfSteps++;
        animationData.add(new AnimationData(currentNode, key, operation)); 
    }
    
    public boolean isItTheLastStep() {
        return currentAnimationIndex == numberOfSteps - 1;
    }
    
    public boolean hasNext() {
        return currentAnimationIndex < numberOfSteps - 1;
    }
    
    public void nextStep() {
            currentAnimationIndex++;
    }
    
    public void prevStep() {
        if (currentAnimationIndex > 0) {
            currentAnimationIndex--;
        }
    }
    
    /**
     * Data structure for one step in the list 
     */
    public class AnimationData {
        private BTreeNode node;
        private int key;
        private String operation;
        
        public AnimationData(BTreeNode node, int key, String operation){
            this.node = node;
            this.key = key;
            this.operation = operation;
        }
        
        public BTreeNode getNode() {
            return node;
        }
        
        public int getKey() {
            return key;
        }
        
        public String getOperation() {
            return operation;
        }
    }
}

