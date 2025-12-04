/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package desktop;

/**
 *
 * @author ashley
 */

import felx.FelxWindow;
import litterbox.LitterBoxFrame;
import scribble.ScribbleFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DesktopMenuCard extends JPanel {
    
    private final DesktopWindow owner;
    private final Image bgImage;
    
    private static final int GRID_ICON_SIZE = 230;     
    private static final int MENU_ICON_SIZE = 88;      
    
    public DesktopMenuCard(DesktopWindow owner) {
        
        this.owner = owner;
        this.bgImage = new ImageIcon(
                getClass().getResource("/desktop/assets/login_template.png")
        ).getImage();

        initUI();
        
    }
    
    private void initUI() {
        
        setOpaque(false);
        setLayout(new BorderLayout());
        
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 90, 40));
        gridPanel.setOpaque(false);
        
        gridPanel.add(createFinalIcon("/desktop/assets/Icon-1.png", "Litter Box"));
        gridPanel.add(createFinalIcon("/desktop/assets/Icon-2.png", "Scribble"));
        gridPanel.add(createFinalIcon("/desktop/assets/Icon-3.png", "iTuna"));
        gridPanel.add(createFinalIcon("/desktop/assets/Icon-4.png", "FEL.X"));
        gridPanel.add(createFinalIcon("/desktop/assets/Icon-5.png", "Photos"));
        gridPanel.add(createFinalIcon("/desktop/assets/Icon-6.png", "Lynxstagram"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        centerWrapper.add(gridPanel, gbc);
        add(centerWrapper, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 10, 30, 40));
        
        JButton btnMenu = createIconButton("/desktop/assets/menu.png", MENU_ICON_SIZE);
        btnMenu.addActionListener(e -> owner.showDesktop());
        bottomPanel.add(btnMenu, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);
        
    }
    
    private JPanel createFinalIcon(String resourcePath, String appName) {
        
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        JButton button = createIconButton(resourcePath, GRID_ICON_SIZE);
        
        button.addActionListener(e -> {
            switch (appName) {
                case "Litter Box" -> {openAppWindow(new LitterBoxFrame());}
                case "FEL.X" -> {openAppWindow(new FelxWindow());}
                case "Scribble"   -> openAppWindow(new ScribbleFrame());
                case "iTuna" -> showNotImplemented("iTuna");
                case "Photos" -> showNotImplemented("Photos");
                case "Lynxstagram" -> showNotImplemented("Lynxstagram");
                default -> showNotImplemented(appName);
            }
        });

        panel.add(button, BorderLayout.CENTER);

        return panel;
        
    }

    private JButton createIconButton(String resourcePath, int size) {
        
        ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        
        JButton button = new JButton(new ImageIcon(scaled));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
        
    }
    
    private void openAppWindow(java.awt.Window appWindow) {
        
        if (appWindow == null) {
            return;
        }
        
        if (owner != null && owner.getAppManager() != null) {
            owner.getAppManager().registerApp(appWindow);
        }
        appWindow.setVisible(true);
    }
    
    private void showNotImplemented(String appName) {
        
        JOptionPane.showMessageDialog(
                this,
                appName + " aún no está implementado en CatOS.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        );
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        
    }
}