///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App
// File:             BPlusTree_Main.java
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

package BPlusTree_App;

import BPlusTree_App.Controllers.BPlusTree_Controller;
import BPlusTree_App.Models.BPlusTree_Model;
import BPlusTree_App.Models.ListOfChanges;
import BPlusTree_App.Views.BPlusTree_View;

public class BPlusTree_Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {   
        
        BPlusTree_Model model = new BPlusTree_Model(5);
        ListOfChanges listOfChangesModel = new ListOfChanges();
        
        BPlusTree_View view = new BPlusTree_View(model, listOfChangesModel);
        BPlusTree_Controller controller = new BPlusTree_Controller(model, listOfChangesModel, view);
        
        controller.run();
    }
}
