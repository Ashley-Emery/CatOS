/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox;

/**
 *
 * @author ashley
 */

import litterbox.core.*;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class TrashViewPanel extends JPanel {

    private final LitterBoxFrame frame;

    private final JButton btnBack = new JButton();
    private final JButton btnForward = new JButton();
    private final JComboBox<String> comboLocation = new JComboBox<>();
    private final JButton btnSearch = new JButton();
    private final JButton btnRestore = new JButton("Restore");
    private final JButton btnDeletePermanent = new JButton("Delete");
    private final JLabel lblTrashIcon = new JLabel();

    private final JTable table;
    private final FileTableModel tableModel = new FileTableModel();

    public TrashViewPanel(LitterBoxFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.decode("#36383d"));

        JPanel top = buildTopBar();
        add(top, BorderLayout.NORTH);

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.decode("#36383d"));
        table.setForeground(Color.WHITE);
        table.setGridColor(Color.DARK_GRAY);

        JTableHeader header = table.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                SortMode mode = switch (col) {
                    case 0 -> SortMode.NAME;
                    case 1 -> SortMode.DATE;
                    case 2 -> SortMode.TYPE;
                    case 3 -> SortMode.SIZE;
                    default -> SortMode.NAME;
                };
                frame.getFileManager().toggleSort(mode);
                refresh();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#545454"));

        JPanel left = new JPanel();
        left.setOpaque(false);
        
        btnBack.setIcon(IconLoader.load("backward_button.png", 24));
        btnForward.setIcon(IconLoader.load("forward_button.png", 24));
        
        makeIconOnlyButton(btnBack);
        makeIconOnlyButton(btnForward);
        
        btnBack.addActionListener(e -> frame.goBack());
        btnForward.addActionListener(e -> frame.goForward());
        
        left.add(btnBack);
        left.add(btnForward);
        left.add(comboLocation);
        
        panel.add(left, BorderLayout.WEST);

        comboLocation.addItem("Home");

        comboLocation.addActionListener(e -> {
            String sel = (String) comboLocation.getSelectedItem();
            if (sel == null) return;
            if ("Home".equals(sel)) {
                frame.navigateToHomeFromUI();
            } else if ("Trash".equals(sel)) {
                // ya estamos
            } else {
                File root = new File(frame.getPathUtils().getUserRoot(), sel);
                if (root.exists()) 
                    frame.navigateToFolderFromUI(root, root);
            }
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        btnSearch.setIcon(IconLoader.load("search_bar.png", 85));
        btnRestore.setIcon(IconLoader.load("restore.png", 85));
        btnDeletePermanent.setIcon(IconLoader.load("delete.png", 85));
        lblTrashIcon.setIcon(IconLoader.load("trash.png", 55));
        
        setActionButtonStyle(btnSearch, "search_bar.png");
        btnSearch.addActionListener(e -> doSearch());
        
        setActionButtonStyle(btnRestore, "restore.png");
        btnRestore.addActionListener(e -> doRestore());
        
       setActionButtonStyle(btnDeletePermanent, "delete.png"); 
        btnDeletePermanent.addActionListener(e -> doDeletePermanent());

        right.add(btnRestore);
        right.add(btnDeletePermanent);
        right.add(lblTrashIcon);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    public void refresh() {
        refreshDropdown();
        File trash = frame.getPathUtils().getTrashDir();
        List<FileInfo> infos = frame.getFileManager().listFolder(trash);
        tableModel.setFiles(infos);
    }

    private void refreshDropdown() {
        comboLocation.removeAllItems();
        comboLocation.addItem("Home");
        File admin = frame.getPathUtils().getUserRoot();
        File[] children = admin.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory() && !child.getName().equalsIgnoreCase("Trash")) {
                    comboLocation.addItem(child.getName());
                }
            }
        }
        if (frame.getPathUtils().getTrashDir().exists()) {
            comboLocation.addItem("Trash");
        }
        comboLocation.setSelectedItem("Trash");
    }

    private FileInfo getSelectedFileInfo() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an item first.");
            return null;
        }
        return tableModel.getFileInfoAt(row);
    }

    private void doSearch() {
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        SearchDialog dlg = new SearchDialog((w instanceof java.awt.Frame) ? (java.awt.Frame) w : null);
        
        String q = dlg.showDialog();
        
        if (q == null || q.isEmpty()) return;

        List<FileInfo> results = frame.getFileManager()
                .searchInFolderRecursive(frame.getPathUtils().getTrashDir(), q);
        tableModel.setFiles(results);
    }

    private void doRestore() {
        FileInfo fi = getSelectedFileInfo();
        if (fi == null) return;

        try {
            frame.getTrashManager().restoreFromTrash(fi.getFile());
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error restoring: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDeletePermanent() {
        FileInfo fi = getSelectedFileInfo();
        if (fi == null) return;
        File f = fi.getFile();
        int count = frame.getFileManager().countRecursive(f);

        String msg;
        if (f.isDirectory()) {
            msg = "PERMANENTLY delete folder \"" + f.getName() + "\" (" + count + " items)?";
        } else {
            msg = "PERMANENTLY delete file \"" + f.getName() + "\"?";
        }

        int opt = JOptionPane.showConfirmDialog(this, msg, "Permanent delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            frame.getTrashManager().deletePermanent(f);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setActionButtonStyle(JButton button, String iconName) {
        final int BTN_WIDTH = 100;
        final int BTN_HEIGHT = 50;

        Icon scaledIcon = IconLoader.load(iconName, BTN_WIDTH, BTN_HEIGHT); 
        button.setIcon(scaledIcon);

        button.setText(null); 
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    }
    
    private void makeIconOnlyButton(JButton button) {
        button.setText(null); 
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(30, 30));
    }
    
    public void updateHistoryButtons() {
        btnBack.setEnabled(frame.getHistoryManager().canGoBack());
        btnForward.setEnabled(frame.getHistoryManager().canGoForward());
    }
    
}
