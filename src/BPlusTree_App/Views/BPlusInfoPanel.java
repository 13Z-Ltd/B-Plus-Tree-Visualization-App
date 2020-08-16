///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  BPlusTree_Main.java
// Package:          BPlusTree_App.Views
// File:             BPlusInfoPanel.java
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

package BPlusTree_App.Views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

/**
 * Separeted class for the display of the BPlusInfoPanel
 * where the program can communicate with the user
 */
public class BPlusInfoPanel extends JPanel {
    JTextArea textArea;
    
    public BPlusInfoPanel() {
        setBorder(new TitledBorder(BorderFactory.createLoweredSoftBevelBorder(), "B+ Tree Infos:"));
        setBackground(Color.white);
        textArea = new JTextArea();
        textArea.setFont(new Font("Serif", Font.ITALIC, 14));
        textArea.setForeground(new Color(37, 38, 39));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane.setPreferredSize(new Dimension(250, 730));
        areaScrollPane.setBorder(null);
        add(areaScrollPane);                
    }
    
    public void addLine(String line) {
        textArea.append(line);
    }
    
    public void clearTextArea() {
        textArea.setText("");
    }
    
}

