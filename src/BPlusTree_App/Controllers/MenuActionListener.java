///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Controllers
// File:             MenuActionListener.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JMenuItem;

/**
 * MenuActionListener implements Java ActionListener and we use it to listen the view menu items actions.
 * It's works with the BPlusTree_Controller and can reaches that methods when an action event is require it.
 */
public class MenuActionListener implements ActionListener {
    BPlusTree_Controller controller;

    /**
     * The constructor have BPlusTree_Controller parameter witch provides access to it
     * @param controller 
     */
    public MenuActionListener(BPlusTree_Controller controller) {
        this.controller = controller;
    }
        
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem menuItem = (JMenuItem)e.getSource();

        if (!controller.animationIsRun) {
            switch (menuItem.getName()) {
                case "New":
                    controller.deleteTree();
                    break;
                case "Load":
                    controller.loadTreeFromFile();
                    break;
                case "Save":
                    controller.saveTreeToFile();
                    break;
                case "Exit":
                    System.exit(0);
                    break;
                case "Prev":
                    try {
                        controller.stepToThePrevTree();
                    } catch (IOException | ClassNotFoundException ex) {
                    }
                    break;
                case "Next":
                    try {
                        controller.stepToTheNextTree();
                    } catch (IOException | ClassNotFoundException ex) {
                    }
                    break;
                default:
                    break;
            }
        }
    }
}