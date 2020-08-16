///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Models
// File:             ListOfChanges.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * With this class the program can make a list from all the performed operation.
 * And with this list the user can move backwards or forwards between the steps
 * @author Balla
 */
public class ListOfChanges implements Serializable {
    private ArrayList<byte[]> changesOfTheTree;
    private ArrayList<Operations> listOfOperations;
    
    private int numberOfChanges;
    private int currentStatusIndex;
    
    
    public ListOfChanges() {
        changesOfTheTree = new ArrayList<byte[]>();
        listOfOperations = new ArrayList<Operations>();
        numberOfChanges = 0;
        currentStatusIndex = -1;
    }
    
    public int getNumberOfChanges() {
        return numberOfChanges;
    }
    
    public int getCurrentStatusIndex() {
        return currentStatusIndex;
    }
    
    public int getTheCurrentOperationKey () {
        return listOfOperations.get(currentStatusIndex).getKey();
    }
    
    public String getTheCurrentOperationKind() {
        return listOfOperations.get(currentStatusIndex).getOperation();
    }
    
    /* The codes below are used to add a new step to the list*/
    public void addChange(BPlusTree_Model tree, int key, String operation) {
        currentStatusIndex++;
        numberOfChanges = currentStatusIndex + 1;
        for (int j = changesOfTheTree.size() - 1; j >= currentStatusIndex; j--) {
            changesOfTheTree.remove(j);
            listOfOperations.remove(j);
        }
        
        try {
            byte[] databytearray;
            databytearray = convertToBytes(tree);
            changesOfTheTree.add(databytearray);
            listOfOperations.add(new Operations(key, operation));
        } catch (IOException e) {
        }
    }
    
    public boolean isItTheFirst(){
        return currentStatusIndex == 0;
    }
    
    public boolean isItTheLast(){
        return currentStatusIndex == numberOfChanges - 1;
    }
    
    public BPlusTree_Model getPrevStatus () throws IOException, ClassNotFoundException {
        if (!isItTheFirst()) {
            currentStatusIndex--;
            return convertFromBytes(changesOfTheTree.get(currentStatusIndex));
        } else {
            return null;
        }
    }
    
    public BPlusTree_Model getNextStatus () throws IOException, ClassNotFoundException {
        if (!isItTheLast()) {
            currentStatusIndex++;
            return convertFromBytes(changesOfTheTree.get(currentStatusIndex));
        } else
            return null;
    }
    
    public BPlusTree_Model getThePrevTree () throws IOException, ClassNotFoundException {
        if (!isItTheFirst()) {
            return convertFromBytes(changesOfTheTree.get(currentStatusIndex - 1));
        } else {
            return null;
        }
    }
    
    /**
     * This method convert (serialized) the current BPlusTree_Model status to a byte array
     * @param tree  current tree status
     * @return  the converted tree byte array
     * @throws IOException 
     */
    private byte[] convertToBytes(BPlusTree_Model tree) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(tree);
            return bos.toByteArray();
        } 
    }

    /**
     * This method convert a byte array back to a BPlusTree_Model 
     * @param bytes current tree status byte array
     * @return the restored BPlusTree_Model
     * @throws IOException/ClassNotFoundException  
     */
    private BPlusTree_Model convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (BPlusTree_Model) in.readObject();
        } 
    }
    
    /**
     * Data structure to store the data of the implemented operation
     */
    class Operations {
        private int key;
        private String operation;
        
        public Operations (int key, String operation) {
            this.key = key;
            this.operation = operation;
        }
        
        public int getKey() {
            return key;
        }
        
        public String getOperation() {
            return operation;
        }
    }
}

