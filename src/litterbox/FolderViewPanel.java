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
    private final JButton btnSearch = new JButton();
    private final JButton btnUpload = new JButton("Upload");
    private final JButton btnNewFolder = new JButton("New Folder");
    private final JButton btnMove = new JButton("Move");
    private final JButton btnCopy = new JButton("Copy");
    private final JButton btnDelete = new JButton("Delete");

    private final JTable table;
    private final FileTableModel tableModel = new FileTableModel();
    
    private final LocationComboListener comboListener = new LocationComboListener();

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
                    
                    if (fi == null)
                        return;
                    
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
        
        comboLocation.addActionListener(comboListener);
        
        JPanel center = new JPanel();
        center.setOpaque(false);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        btnSearch.setIcon(IconLoader.load("search_bar.png", 85));
        btnUpload.setIcon(IconLoader.load("upload.png", 85)); 
        btnNewFolder.setIcon(IconLoader.load("new_folder.png", 85));
        btnMove.setIcon(IconLoader.load("move.png", 85));
        btnCopy.setIcon(IconLoader.load("copy.png", 85));
        btnDelete.setIcon(IconLoader.load("delete.png", 85));
        
        setActionButtonStyle(btnSearch, "search_bar.png");
        btnSearch.addActionListener(e -> doSearch());
        
        setActionButtonStyle(btnUpload, "upload.png");
        btnUpload.addActionListener(e -> doUpload());
        
        setActionButtonStyle(btnNewFolder, "new_folder.png");
        btnNewFolder.addActionListener(e -> doNewFolder());
        
        setActionButtonStyle(btnMove, "move.png");
        btnMove.addActionListener(e -> doMove());
        
        setActionButtonStyle(btnCopy, "copy.png");
        btnCopy.addActionListener(e -> doCopy());
        
        setActionButtonStyle(btnDelete, "delete.png");
        btnDelete.addActionListener(e -> doDeleteSoft());

        right.add(btnSearch);
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
        
        comboLocation.removeActionListener(comboListener); 
        refreshDropdown();
        comboLocation.addActionListener(comboListener);

        refresh();
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
    
    private class LocationComboListener implements java.awt.event.ActionListener {
        
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            String sel = (String) comboLocation.getSelectedItem();
            if (sel == null)
                return;
            
            if ("Home".equals(sel)) {
                frame.navigateToHomeFromUI();
                
            } else if ("Trash".equals(sel)) {
                frame.navigateToTrashFromUI();
                
            } else {
                
                File root = new File(frame.getPathUtils().getUserRoot(), sel);
                
                if (root.exists()) {
                    frame.navigateToFolderFromUI(root, root);
                }
            }
        }
    }
    
}
