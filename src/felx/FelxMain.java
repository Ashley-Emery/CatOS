/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package felx;

/**
 *
 * @author ashley
 */

import javax.swing.SwingUtilities;

public class FelxMain {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FelxWindow().setVisible(true);
        });
    }
    
}
