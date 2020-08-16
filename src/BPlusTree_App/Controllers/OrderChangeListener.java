///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Controllers
// File:             OrderChangeListener.java
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

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * OrderChangeListener implements Java ChangeListener and we use it to check the change events on the order spinner.
 * It's works with the BPlusTree_Controller with it we can change the order of the tree.
 */
public class OrderChangeListener implements ChangeListener {
    BPlusTree_Controller controller;

    /**
     * The constructor have BPlusTree_Controller parameter witch provides access to it
     * @param controller 
     */
    public OrderChangeListener(BPlusTree_Controller controller) {
        this.controller = controller;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!controller.bplusTree.treeIsEmpty() && (int)controller.view.orderSpinner.getValue() != controller.currentOrder) {
                int response = JOptionPane.showConfirmDialog(null, "The B+ Tree is not empty and the order change will make a new one. \n Do you want to save your tree?", "New Tree",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION || response == JOptionPane.CLOSED_OPTION) {
                    controller.view.infoPanel.addLine("Change the order of the Tree is canceled!\nIt's set back to:" + controller.currentOrder + "\n");
                    controller.view.orderSpinner.setValue(controller.currentOrder);
                } else {
                    if (response == JOptionPane.YES_OPTION) {
                        controller.saveTreeToFile();
                    }
                    if (response == JOptionPane.NO_OPTION) {

                    }
                    controller.currentOrder = (int) controller.view.orderSpinner.getValue();
                    controller.deleteTree();
                }
            }
            if (controller.bplusTree.treeIsEmpty()) {
                controller.currentOrder = (int) controller.view.orderSpinner.getValue();
                controller.deleteTree();
                controller.view.infoPanel.addLine("\nThe Order of the Tree has changed to: " + controller.view.orderSpinner.getValue() + "\n");
            }
            controller.view.textField.requestFocus();
    }
}
