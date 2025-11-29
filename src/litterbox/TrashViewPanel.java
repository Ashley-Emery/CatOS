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
    private final JButton btnSearchBar = new JButton();
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
        btnBack.setIcon(IconLoader.load("backward_button.png"));
        btnForward.setIcon(IconLoader.load("forward_button.png"));
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
                File root = new File(frame.getPathUtils().getAdminRoot(), sel);
                if (root.exists()) frame.navigateToFolderFromUI(root, root);
            }
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        btnSearchBar.setIcon(IconLoader.load("search_bar.png"));
        btnSearchBar.setBorderPainted(false);
        btnSearchBar.setContentAreaFilled(false);
        btnSearchBar.addActionListener(e -> doSearch());
        center.add(btnSearchBar);
        panel.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setOpaque(false);
        btnRestore.setIcon(IconLoader.load("restore.png"));
        btnDeletePermanent.setIcon(IconLoader.load("delete.png"));
        lblTrashIcon.setIcon(IconLoader.load("trash.png"));

        btnRestore.addActionListener(e -> doRestore());
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
        File admin = frame.getPathUtils().getAdminRoot();
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
}
