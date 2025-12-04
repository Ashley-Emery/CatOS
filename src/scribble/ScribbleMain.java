/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scribble;

/**
 *
 * @author ashley
 */

import javax.swing.SwingUtilities;

public class ScribbleMain {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ScribbleFrame frame = new ScribbleFrame();
            frame.setVisible(true);
        });
    }
}

