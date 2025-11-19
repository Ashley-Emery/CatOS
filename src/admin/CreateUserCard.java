/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin;

/**
 *
 * @author ashley
 */

import admin.core.UserManager;

import javax.swing.*;
import java.awt.*;

public class CreateUserCard extends JPanel {
    
    private final LoginCardsPanel parent;
    private final JTextField userField;
    private final JPasswordField passwordField;
    private final Image background;

    public CreateUserCard(LoginCardsPanel parent) {
        
        this.parent = parent;
        this.background = new ImageIcon(
                getClass().getResource("/admin/assets/login_template.png")
        ).getImage();

        setLayout(new BorderLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel userIcon = new JLabel();
        ImageIcon icon = new ImageIcon(
                getClass().getResource("/admin/assets/user-icon.png")
        );
        Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        userIcon.setIcon(new ImageIcon(scaled));
        content.add(userIcon, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;

        JLabel userLabel = new JLabel("User");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        content.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        userField = new JTextField(20);
        userField.setFont(new Font("SansSerif", Font.PLAIN, 24));
        content.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel pwdLabel = new JLabel("Password");
        pwdLabel.setForeground(Color.WHITE);
        pwdLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        content.add(pwdLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 24));
        content.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 10, 10, 10);

        JButton createButton = createImageButton(
                "/admin/assets/create_user.png",
                260, 90
        );
        createButton.addActionListener(e -> handleCreateUser());
        content.add(createButton, gbc);

        add(content, BorderLayout.CENTER);

        JButton backButton = createImageButton(
                "/admin/assets/back.png",
                80, 80
        );
        backButton.addActionListener(e -> parent.showAdminLogin());

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        backPanel.add(backButton);
        add(backPanel, BorderLayout.SOUTH);
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

    private void handleCreateUser() {
        String user = userField.getText().trim();
        String pwd = new String(passwordField.getPassword());

        if (user.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "User and password are required.",
                    "Create user",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean created = UserManager.createUser(user, pwd, false);
        if (!created) {
            JOptionPane.showMessageDialog(
                    this,
                    "User already exists or error creating user.",
                    "Create user",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "User created successfully!",
                    "Create user",
                    JOptionPane.INFORMATION_MESSAGE
            );
            parent.showAdminLogin();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
}
