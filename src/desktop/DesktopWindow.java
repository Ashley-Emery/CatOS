/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package desktop;

/**
 *
 * @author ashley
 */

import admin.AdminWindow;

import javax.swing.*;
import java.awt.*;

public class DesktopWindow extends JFrame {
    
    private final CardLayout cardLayout;
    private final JPanel mainCards;
    
    private final DesktopMainCard desktopCard;
    private final DesktopMenuCard menuCard;
    private final DesktopSettingsCard settingsCard;
    
    private final AppManager appManager;
    private String currentCard = "desktop";
    
    public DesktopWindow() {
        super("CatOS Desktop");
        
        this.appManager = new AppManager();
        
        cardLayout = new CardLayout();
        mainCards = new JPanel(cardLayout);
        
        desktopCard = new DesktopMainCard(this);
        menuCard = new DesktopMenuCard(this);
        settingsCard = new DesktopSettingsCard(this);
        
        mainCards.add(desktopCard, "desktop");
        mainCards.add(menuCard, "menu");
        mainCards.add(settingsCard, "settings");
        
        setContentPane(mainCards);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainCards.setPreferredSize(new Dimension(1536, 768));
        pack();
        
        setMinimumSize(new Dimension(1024, 600));
        setResizable(true);
        setLocationRelativeTo(null);

        showDesktop();
    }

    public AppManager getAppManager() {
        return appManager;
    }

    public void showDesktop() {
        cardLayout.show(mainCards, "desktop");
        currentCard = "desktop";
    }

    public void showMenu() {
        cardLayout.show(mainCards, "menu");
        currentCard = "menu";
    }

    public void showSettings() {
        cardLayout.show(mainCards, "settings");
        currentCard = "settings";
    }
    
    public void toggleSettings() {
        if ("desktop".equals(currentCard)) {
            showSettings();
        } else if ("settings".equals(currentCard)) {
            showDesktop();
        }
    }

    private void closeAllApps() {
        appManager.closeAllApps();
    }
    
    public void logoutToAdmin() {
        closeAllApps();
        
        SwingUtilities.invokeLater(() -> new AdminWindow().setVisible(true));
        dispose();
    }
    
    public void exitCatOS() {
        closeAllApps();
        dispose();
        System.exit(0);
    }
    
    public void handleLogoutRequest(Component parent) {
        int opt = JOptionPane.showConfirmDialog(
                parent,
                "¿Seguro que deseas cerrar sesión?",
                "Confirmar Log Out",
                JOptionPane.YES_NO_OPTION
        );
        if (opt == JOptionPane.YES_OPTION) {
            logoutToAdmin();
        }
    }
    
    public void handleExitRequest(Component parent) {
        int opt = JOptionPane.showConfirmDialog(
                parent,
                "¿Seguro que deseas salir de CatOS?",
                "Confirmar Exit",
                JOptionPane.YES_NO_OPTION
        );
        if (opt == JOptionPane.YES_OPTION) {
            exitCatOS();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesktopWindow().setVisible(true));
    }
}
