///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Controllers
// File:             TextFieldKeyListener.java
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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * OrderChangeListener implements Java KeyListener and we use it to handle the key commands on view text field.
 */
public class TextFieldKeyListener implements KeyListener {
    BPlusTree_Controller controller;
    
    /**
     * The constructor have BPlusTree_Controller parameter witch provides access to it
     * @param controller 
     */
    public TextFieldKeyListener(BPlusTree_Controller controller) {
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!controller.animationIsRun) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER){
                if(!controller.view.textField.getText().equals("")){
                    controller.insertionProcess();
                    controller.view.textField.setText("");
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_A && e.isControlDown())
            {
                controller.view.btnAnimation.doClick();
            }
            if (e.getKeyCode() == KeyEvent.VK_I && e.isControlDown())
            {
                if(!controller.view.textField.getText().equals("")){
                    controller.insertionProcess();
                    controller.view.textField.setText("");
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) 
            {                    
                controller.deletionProcess();
                controller.view.textField.setText("");
            }
            if (e.getKeyCode() == KeyEvent.VK_R && e.isControlDown())
            {
                controller.randomInsert();
            }
            if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) 
            {
                controller.findingProcess();
                controller.view.textField.setText("");
            }
            if (e.getKeyCode() == KeyEvent.VK_N && e.isControlDown())
            {
                controller.deleteTree();
            }
            if (e.getKeyCode() == KeyEvent.VK_M && e.isControlDown())
            {
                controller.treeMaker();
            }

            if (e.getKeyCode() == KeyEvent.VK_UP && e.isAltDown()) {
                controller.view.deltaY -= 10;
                controller.view.repaintAll();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN && e.isAltDown()) {
                controller.view.deltaY += 10;
                controller.view.repaintAll();
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT && e.isAltDown()) {
                controller.view.deltaX -= 10;
                controller.view.repaintAll();
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT && e.isAltDown()) {
                controller.view.deltaX += 10;
                controller.view.repaintAll();
            }
            if (e.getKeyCode() == KeyEvent.VK_UP && e.isAltDown() &&e.isShiftDown()) {
                controller.view.scale *= 1.1;
                controller.view.repaintAll();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN && e.isAltDown() &&e.isShiftDown()) {
                controller.view.scale /= 1.1;
                controller.view.repaintAll();
            }
            if (e.getKeyCode() == KeyEvent.VK_DELETE && e.isAltDown()) {
                controller.view.infoPanel.clearTextArea();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
