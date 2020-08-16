///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Controllers
// File:             ButtonPressListener.java
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

import BPlusTree_App.Models.ListOfAnimationData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * ButtonPressListener implements Java ActionListener and we use it to listen the view buttons actions.
 * It's works with the BPlusTree_Controller and can reaches that methods when an action event is require it.
 */
public class ButtonPressListener implements ActionListener {
    BPlusTree_Controller controller;

    /**
     * The constructor have BPlusTree_Controller parameter witch provides access to it
     * @param controller 
     */
    public ButtonPressListener(BPlusTree_Controller controller) {
        this.controller = controller;
    }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton srcButton = (JButton)e.getSource();

            if (!controller.animationIsRun) {
            switch (srcButton.getName()) {
                case "Insert":
                    if(!controller.view.textField.getText().equals("")){
                        controller.insertionProcess();
                        controller.view.textField.setText("");
                    }
                    break;
                case "Find":
                    if(!controller.view.textField.getText().equals("")){
                        controller.findingProcess();
                        controller.view.textField.setText("");
                    }
                    break;
                case "Delete":
                    if(!controller.view.textField.getText().equals("")){                    
                        controller.deletionProcess();
                        controller.view.textField.setText("");
                    }
                    break;
                case "New":
                    controller.deleteTree();
                    break;
                case "Random":
                    controller.randomInsert();
                    break;
                case "Animation":
                    if (controller.animationIsON) {
                        controller.view.btnAnimation.setIcon(new ImageIcon("src/resources/animation_off.png"));
                        controller.animationIsON = false;
                        controller.view.animationIsON = false;
                        
                        controller.view.btnStop.setVisible(false);
                        controller.view.frame.repaint();
                    } else {
                        controller.view.btnAnimation.setIcon(new ImageIcon("src/resources/animation_on.png"));
                        controller.animationIsON = true;
                        controller.view.animationIsON = true;
                        
                        controller.view.btnStop.setVisible(true);
                        controller.view.frame.repaint();
                    }
                    break;
                case "Maker":
                    controller.treeMaker();
                    break;
                case "Prev":
                    if (controller.listOfChanges.getNumberOfChanges() != 0) {
                        try {
                            controller.stepToThePrevTree();
                        } catch (Exception ex) {
                        }
                        //Change the icon of the buttons if it necesarry
                        if (controller.listOfChanges.getCurrentStatusIndex() == 0){
                            controller.view.btnPrev.setIcon(new ImageIcon("src/resources/prevdisabled.png"));
                        }
                        if (controller.listOfChanges.getCurrentStatusIndex() != controller.listOfChanges.getNumberOfChanges() - 1) {
                            controller.view.btnNext.setIcon(new ImageIcon("src/resources/next.png"));
                        }
                    }
                    break;
                case "Next":
                    if (controller.listOfChanges.getNumberOfChanges() != 0) {
                        try {
                            controller.stepToTheNextTree();
                        } catch (Exception ex) {
                        }
                        if (controller.listOfChanges.getCurrentStatusIndex() == controller.listOfChanges.getNumberOfChanges() - 1){
                            controller.view.btnNext.setIcon(new ImageIcon("src/resources/nextdisabled.png"));
                        }
                        if (controller.listOfChanges.getCurrentStatusIndex() != 0) {
                            controller.view.btnPrev.setIcon(new ImageIcon("src/resources/prev.png"));
                        }
                    }
                    break;
                default:
                    break;
                }
            }
            if (controller.animationIsRun && srcButton.getName() == "Stop") {
                controller.animationIsStopped = true;
                controller.listOfAnimationDatas = new ListOfAnimationData();            
            }
        }
}
