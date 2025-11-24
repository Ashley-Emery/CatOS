/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package desktop;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DesktopMainCard extends JPanel {
    
    private final DesktopWindow owner;
    protected final Image bgImage;
    private final JLabel lblDateTime;
    private final Timer clockTimer;
    
    private static final int DOCK_ICON_SIZE = 88;
    private static final int SETTINGS_ICON_SIZE = 42;
    
    public DesktopMainCard(DesktopWindow owner) {
        
        this.owner = owner;
        
        bgImage = new ImageIcon(getClass().getResource("/desktop/assets/desktop_template.png")).getImage();
        
        setOpaque(false);
        setLayout(new BorderLayout());
        
        JPanel dockPanel = buildDockPanel();
        add(dockPanel, BorderLayout.WEST);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 35));
        
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        topRight.setOpaque(false);
        
        lblDateTime = new JLabel();
        lblDateTime.setForeground(Color.WHITE);
        lblDateTime.setFont(new Font("SansSerif", Font.BOLD, 26));
        
        JButton btnSettings = createIconButton(
                "/desktop/assets/settings.png",
                null,
                SETTINGS_ICON_SIZE
        );
        
        btnSettings.addActionListener(e -> owner.toggleSettings());
        
        topRight.add(lblDateTime);
        topRight.add(btnSettings);
        
        topPanel.add(topRight, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel center = new JPanel();
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);
        
        clockTimer = new Timer(1000, e -> updateDateTime());
        clockTimer.start();
        updateDateTime();
    }
    
    private JPanel buildDockPanel() {
        JPanel dockPanel = new JPanel();
        dockPanel.setOpaque(false);
        dockPanel.setLayout(new BoxLayout(dockPanel, BoxLayout.Y_AXIS));
        dockPanel.setPreferredSize(new Dimension(140, 0));
        
        dockPanel.add(Box.createVerticalStrut(5));
        
        JButton btnLynx = createIconButton("/desktop/assets/lynxstagram.png", null, DOCK_ICON_SIZE);
        JButton btnPhotos = createIconButton("/desktop/assets/photo_hunter.png", null, DOCK_ICON_SIZE);
        JButton btnLitterbox = createIconButton("/desktop/assets/litterbox.png", null, DOCK_ICON_SIZE);
        JButton btnScribble = createIconButton("/desktop/assets/scribble.png", null, DOCK_ICON_SIZE);
        JButton btnItuna = createIconButton("/desktop/assets/ituna.png", null, DOCK_ICON_SIZE);
        JButton btnFelx = createIconButton("/desktop/assets/felx.png", null, DOCK_ICON_SIZE);
        
        JButton btnMenu = createIconButton("/desktop/assets/menu.png", null, 88);
        
        btnLynx.addActionListener(e -> showNotImplemented("Lynxstagram"));
        btnPhotos.addActionListener(e -> showNotImplemented("Photos"));
        btnLitterbox.addActionListener(e -> showNotImplemented("Litter Box"));
        btnScribble.addActionListener(e -> showNotImplemented("Scribble"));
        btnItuna.addActionListener(e -> showNotImplemented("iTuna"));
        btnFelx.addActionListener(e -> showNotImplemented("FEL.X"));
        
        btnMenu.addActionListener(e -> owner.showMenu());
        
        btnLynx.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPhotos.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLitterbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnScribble.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnItuna.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFelx.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        int spacing = 18;
        
        dockPanel.add(btnLynx);
        dockPanel.add(Box.createVerticalStrut(spacing));
        dockPanel.add(btnPhotos);
        dockPanel.add(Box.createVerticalStrut(spacing));
        dockPanel.add(btnLitterbox);
        dockPanel.add(Box.createVerticalStrut(spacing));
        dockPanel.add(btnScribble);
        dockPanel.add(Box.createVerticalStrut(spacing));
        dockPanel.add(btnItuna);
        dockPanel.add(Box.createVerticalStrut(spacing));
        dockPanel.add(btnFelx);
        
        dockPanel.add(Box.createVerticalGlue());
        dockPanel.add(btnMenu);
        dockPanel.add(Box.createVerticalStrut(28));
        
        // btnMenu.addActionListener(e -> owner.showMenu());
        
        return dockPanel;
        
    }
    
    protected JButton createIconButton(String resourcePath, String text, int iconSize) {
        
        ImageIcon baseIcon = new ImageIcon(getClass().getResource(resourcePath));
        Image scaled = baseIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        
        JButton button;
        
        if (text != null && !text.isEmpty()) {
            button = new JButton(text, icon);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
        } else {
            button = new JButton(icon);
        }
        
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        
        return button;
        
    }
    
    private void showNotImplemented(String appName) {
        
        JOptionPane.showMessageDialog(
                this,
                appName + " aún no está implementado en CatOS.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void updateDateTime() {
        
        LocalDateTime now = LocalDateTime.now();
        
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM d HH:mm", new Locale("es", "ES"));
        
        String formatted = now.format(formatter).toLowerCase(Locale.ROOT);
        lblDateTime.setText(formatted);
        
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
