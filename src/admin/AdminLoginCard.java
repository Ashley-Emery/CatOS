/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin;

/**
 *
 * @author ashley
 */


import admin.core.SessionManager;
import desktop.DesktopWindow;

import javax.swing.*;
import java.awt.*;

public class AdminLoginCard extends JPanel {
    
    private final LoginCardsPanel parent;
    private final JPasswordField passwordField;
    private final Image background;

    public AdminLoginCard(LoginCardsPanel parent) {
        
        this.parent = parent;
        this.background = new ImageIcon(
                getClass().getResource("/admin/assets/login_template.png")
        ).getImage();

        setLayout(new GridBagLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton userIconButton = createImageButton(
                "/admin/assets/user-icon.png",
                200, 200
        );
        userIconButton.addActionListener(e -> parent.showCreateUser());
        content.add(userIconButton, gbc);

        gbc.gridy++;
        JButton adminLabelButton = new JButton("Admin");
        adminLabelButton.setFocusPainted(false);
        adminLabelButton.setBorderPainted(false);
        adminLabelButton.setContentAreaFilled(false);
        adminLabelButton.setForeground(Color.WHITE);
        adminLabelButton.setFont(new Font("SansSerif", Font.BOLD, 40));
        adminLabelButton.addActionListener(e -> parent.showUserLogin());
        content.add(adminLabelButton, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;

        JButton enterButton = createImageButton(
                "/admin/assets/arrow_enter.png",
                80, 80
        );
        enterButton.addActionListener(e -> handleAdminLogin());

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 24));

        content.add(enterButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        content.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.insets = new Insets(80, 10, 0, 10);

        ImageIcon logoIcon = new ImageIcon(
                getClass().getResource("/admin/assets/catos_logo.png")
        );
        Image logoScaled = logoIcon.getImage().getScaledInstance(220, 80, Image.SCALE_SMOOTH);

        JLabel logoLabel = new JLabel(new ImageIcon(logoScaled));
        content.add(logoLabel, gbc);

        add(content);
    }

    private JButton createImageButton(String resourcePath, int width, int height) {
        
        ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaled));
        
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        
        return button;
    }

    private void handleAdminLogin() {
        
        String pwd = new String(passwordField.getPassword());

        boolean ok = SessionManager.login("admin", pwd);
        
        if (!ok) {
            JOptionPane.showMessageDialog(
                    this,
                    "Incorrect admin password.",
                    "Login error",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            
            openDesktopAndCloseLogin();
        }
    }
    
    private void openDesktopAndCloseLogin() {
        
        SwingUtilities.invokeLater(() -> new DesktopWindow().setVisible(true));
        
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        if (w != null) {
            w.dispose();
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
}
