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

public class DesktopSettingsCard extends JPanel {
    
    private final DesktopWindow owner;
    protected final Image bgImage;
    private final JLabel lblDateTime;
    private final Timer clockTimer;
    
    private static final int DOCK_ICON_SIZE = 88;
    private static final int SETTINGS_ICON_SIZE = 42;
    private static final int BUBBLE_ICON_SIZE = 130;
    
    public DesktopSettingsCard(DesktopWindow owner) {
        
        this.owner = owner;
        
        bgImage = new ImageIcon(
                getClass().getResource("/desktop/assets/settings_template.png")
        ).getImage();
        
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
                SETTINGS_ICON_SIZE,
                0
        );
        
        btnSettings.addActionListener(e -> owner.toggleSettings());
        
        topRight.add(lblDateTime);
        topRight.add(btnSettings);
        
        topPanel.add(topRight, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        
        JPanel bubbleContent = new JPanel();
        bubbleContent.setOpaque(false);
        
        bubbleContent.setLayout(new BoxLayout(bubbleContent, BoxLayout.X_AXIS));
        
        bubbleContent.setPreferredSize(new Dimension(360, 160));
        
        JButton btnLogout = createIconButton(
                "/desktop/assets/log_out.png",
                null,
                BUBBLE_ICON_SIZE,
                0
        );
        
        JButton btnExit = createIconButton(
                "/desktop/assets/exit.png",
                null,
                BUBBLE_ICON_SIZE,
                0
        );
        
        btnLogout.addActionListener(e -> owner.handleLogoutRequest(this));
        btnExit.addActionListener(e -> owner.handleExitRequest(this));
        
        bubbleContent.add(Box.createRigidArea(new Dimension(95, 0)));
        bubbleContent.add(btnLogout);
        bubbleContent.add(Box.createRigidArea(new Dimension(-40, 0)));
        bubbleContent.add(btnExit);
        bubbleContent.add(Box.createHorizontalGlue());
        bubbleContent.add(Box.createVerticalGlue());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        
        gbc.insets = new Insets(35, 0, 0, 100);
        
        centerWrapper.add(bubbleContent, gbc);
        add(centerWrapper, BorderLayout.CENTER);
        
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
        
        JButton btnLynx = createIconButton("/desktop/assets/lynxstagram.png", null,
                DOCK_ICON_SIZE, 0);
        JButton btnPhotos = createIconButton("/desktop/assets/photo_hunter.png", null,
                DOCK_ICON_SIZE, 0);
        JButton btnLitterbox = createIconButton("/desktop/assets/litterbox.png", null,
                DOCK_ICON_SIZE, 0);
        JButton btnScribble = createIconButton("/desktop/assets/scribble.png", null,
                DOCK_ICON_SIZE, 0);
        JButton btnItuna = createIconButton("/desktop/assets/ituna.png", null,
                DOCK_ICON_SIZE, 0);
        JButton btnFelx = createIconButton("/desktop/assets/felx.png", null,
                DOCK_ICON_SIZE, 0);
        JButton btnMenu = createIconButton("/desktop/assets/menu.png", null,
                88, 0);
        
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
        dockPanel.add(Box.createVerticalStrut(25));
        
        btnMenu.addActionListener(e -> owner.showMenu());
        
        return dockPanel;
        
    }

    private JButton createIconButton(String resourcePath, String text, int iconSize, int fontSize) {
        
        ImageIcon baseIcon = new ImageIcon(getClass().getResource(resourcePath));
        
        Image scaled = baseIcon.getImage().getScaledInstance(
                iconSize, iconSize, Image.SCALE_SMOOTH
        );
        
        ImageIcon icon = new ImageIcon(scaled);
        
        JButton button;
        
        if (text != null && !text.isEmpty()) {
            button = new JButton(text, icon);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setForeground(new Color(30, 30, 30));
            if (fontSize > 0) {
                button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
            }
        } else {
            button = new JButton(icon);
        }
        
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        
        return button;
        
    }

    private void updateDateTime() {
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d HH:mm", new Locale("es", "ES"));
        String formatted = now.format(formatter).toLowerCase(Locale.ROOT);
        lblDateTime.setText(formatted);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        
    }
}
