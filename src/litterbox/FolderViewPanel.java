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

public class FolderViewPanel extends JPanel {

    private final LitterBoxFrame frame;

    private final JButton btnBack = new JButton();
    private final JButton btnForward = new JButton();
    private final JComboBox<String> comboLocation = new JComboBox<>();
    private final JButton btnSearchBar = new JButton();
    private final JButton btnUpload = new JButton("Upload");
    private final JButton btnNewFolder = new JButton("New Folder");
    private final JButton btnMove = new JButton("Move");
    private final JButton btnCopy = new JButton("Copy");
    private final JButton btnDelete = new JButton("Delete");

    private final JTable table;
    private final FileTableModel tableModel = new FileTableModel();

    private File currentFolder;
    private File rootLogical;

    public FolderViewPanel(LitterBoxFrame frame) {
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

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    FileInfo fi = tableModel.getFileInfoAt(table.getSelectedRow());
                    if (fi == null) return;
                    File f = fi.getFile();
                    if (f.isDirectory()) {
                        currentFolder = f;
                        refresh();
                    } else {
                        showNotImplementedDialog(f);
                    }
                }
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

        comboLocation.addActionListener(e -> {
            String sel = (String) comboLocation.getSelectedItem();
            if (sel == null) return;
            if ("Home".equals(sel)) {
                frame.navigateToHomeFromUI();
            } else if ("Trash".equals(sel)) {
                frame.navigateToTrashFromUI();
            } else {
                File root = new File(frame.getPathUtils().getAdminRoot(), sel);
                if (root.exists()) {
                    frame.navigateToFolderFromUI(root, root);
                }
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
        btnUpload.setIcon(IconLoader.load("upload.png"));
        btnNewFolder.setIcon(IconLoader.load("new_folder.png"));
        btnMove.setIcon(IconLoader.load("move.png"));
        btnCopy.setIcon(IconLoader.load("copy.png"));
        btnDelete.setIcon(IconLoader.load("delete.png"));

        btnUpload.addActionListener(e -> doUpload());
        btnNewFolder.addActionListener(e -> doNewFolder());
        btnMove.addActionListener(e -> doMove());
        btnCopy.addActionListener(e -> doCopy());
        btnDelete.addActionListener(e -> doDeleteSoft());

        right.add(btnUpload);
        right.add(btnNewFolder);
        right.add(btnMove);
        right.add(btnCopy);
        right.add(btnDelete);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }

    public void setFolder(File folder, File rootForDropdown) {
        this.currentFolder = folder;
        this.rootLogical = rootForDropdown;
        refreshDropdown();
        refresh();
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

        if (rootLogical != null) {
            comboLocation.setSelectedItem(rootLogical.getName());
        }
    }

    public void refresh() {
        if (currentFolder == null) return;
        List<FileInfo> infos = frame.getFileManager().listFolder(currentFolder);
        tableModel.setFiles(infos);
    }

    private void doSearch() {
        
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        SearchDialog dlg = new SearchDialog((w instanceof java.awt.Frame) ? (java.awt.Frame) w : null);
        
        String query = dlg.showDialog();

        if (query == null || query.isEmpty())
            return;

        List<FileInfo> results = frame.getFileManager().searchInFolderRecursive(currentFolder, query);
        tableModel.setFiles(results);
    }

    private void doUpload() {
        try {
            frame.getOrganizer().uploadFromBridge();
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Upload error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doNewFolder() {
        String name = JOptionPane.showInputDialog(this, "New folder name:", "New Folder",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.isBlank()) return;
        try {
            frame.getFileManager().createFolder(currentFolder, name.trim());
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating folder: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private FileInfo getSelectedFileInfo() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item in the list.");
            return null;
        }
        return tableModel.getFileInfoAt(row);
    }

    private void doMove() {
        FileInfo fi = getSelectedFileInfo();
        if (fi == null) return;

        String destPath = JOptionPane.showInputDialog(this, "Destination path:", fi.getFile().getParent());
        if (destPath == null || destPath.isBlank()) return;

        try {
            frame.getFileManager().move(fi.getFile(), new File(destPath.trim()));
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error moving file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doCopy() {
        FileInfo fi = getSelectedFileInfo();
        if (fi == null) return;

        JTextField txtName = new JTextField(fi.getFile().getName(), 20);
        JTextField txtPath = new JTextField(fi.getFile().getParent(), 20);
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("New name:"));
        panel.add(txtName);
        panel.add(new JLabel("Destination path:"));
        panel.add(txtPath);

        int opt = JOptionPane.showConfirmDialog(this, panel, "Copy", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) return;

        try {
            frame.getFileManager().copy(fi.getFile(), new File(txtPath.getText().trim()),
                    txtName.getText().trim());
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error copying file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDeleteSoft() {
        FileInfo fi = getSelectedFileInfo();
        if (fi == null) return;
        File f = fi.getFile();
        int count = frame.getFileManager().countRecursive(f);

        String msg;
        if (f.isDirectory()) {
            msg = "Delete folder \"" + f.getName() + "\" with " + count + " items?\n"
                    + "(It will be moved to Trash)";
        } else {
            msg = "Delete file \"" + f.getName() + "\"?\n(It will be moved to Trash)";
        }

        int opt = JOptionPane.showConfirmDialog(this, msg,
                "Move to Trash", JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) return;

        try {
            frame.getTrashManager().moveToTrash(f);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error moving to trash: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showNotImplementedDialog(File f) {
        String ext = frame.getFileManager().getExtension(f.getName());
        JOptionPane.showMessageDialog(this,
                "La app para abrir archivos con extensión ." + ext + " no está implementada aún.",
                "Not implemented",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
