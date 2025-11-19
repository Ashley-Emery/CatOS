/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import java.awt.*;

public class AdminWindow extends JFrame {
    
    public AdminWindow() {
        
        setTitle("CatOS - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        LoginCardsPanel cardsPanel = new LoginCardsPanel();
        setContentPane(cardsPanel);
    }

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            AdminWindow window = new AdminWindow();
            window.setVisible(true);
        });
    }
      
}
