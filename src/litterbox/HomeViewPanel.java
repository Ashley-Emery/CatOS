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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class HomeViewPanel extends JPanel {

    private final LitterBoxFrame frame;

    private final JButton btnBack = new JButton();
    private final JButton btnForward = new JButton("Forward");
    private final JComboBox<String> comboLocation = new JComboBox<>();
    
    private final JButton btnSearch = new JButton(); 
    
    private final JButton btnUpload = new JButton();
    private final JButton btnNewFolder = new JButton();
    private final JButton btnMove = new JButton();
    private final JButton btnCopy = new JButton();
    private final JButton btnDelete = new JButton();

    private final JTree tree;
    private final DefaultTreeModel treeModel;

    private File currentFolder;

    public HomeViewPanel(LitterBoxFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.decode("#545454"));

        JPanel top = buildTopBar();
        add(top, BorderLayout.NORTH);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("admin");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        
        tree.setBackground(Color.decode("#36383d"));
        tree.setForeground(Color.WHITE);
        
        tree.setCellRenderer(new CustomFileTreeRenderer());

        refreshTree();

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object sel = tree.getLastSelectedPathComponent();
                if (sel instanceof DefaultMutableTreeNode node) {
                    Object userObj = node.getUserObject();
                    if (userObj instanceof File file) {
                        currentFolder = file.isDirectory() ? file : file.getParentFile();
                    } else if ("admin".equals(userObj)) {
                        currentFolder = frame.getPathUtils().getAdminRoot();
                    }
                }
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path == null) return;
                    Object comp = path.getLastPathComponent();
                    if (comp instanceof DefaultMutableTreeNode node) {
                        Object userObj = node.getUserObject();
                        if (userObj instanceof File f) {
                            if (f.isDirectory()) {
                                File rootLogical = frame.getPathUtils().findRootLogicalFor(f);
                                frame.navigateToFolderFromUI(f, rootLogical);
                            } else {
                                showNotImplementedDialog(f);
                            }
                        }
                    }
                }
            }
        });

        JScrollPane treeScroll = new JScrollPane(tree);

        add(treeScroll, BorderLayout.CENTER);

        currentFolder = frame.getPathUtils().getAdminRoot();
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

        comboLocation.addItem("Home");

        refreshDropdown();

        comboLocation.addActionListener(e -> {
            String sel = (String) comboLocation.getSelectedItem();
            if (sel == null) return;

            if ("Home".equals(sel)) {
                frame.navigateToHomeFromUI();
            } else if ("Trash".equals(sel)) {
                frame.navigateToTrashFromUI();
            } else {
                File root = new File(frame.getPathUtils().getAdminRoot(), sel);
                if (root.exists() && root.isDirectory()) {
                    frame.navigateToFolderFromUI(root, root);
                }
            }
        });

        left.add(comboLocation);

        JPanel center = new JPanel();
        center.setOpaque(false);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5)); // Usamos FlowLayout para separar los botones

        btnSearch.setIcon(IconLoader.load("search_bar.png", 85)); // Tamaño de 80x30 o similar si conoces el radio. Usaré un tamaño grande y lo ajustaremos.
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

        panel.add(left, BorderLayout.WEST);
        panel.add(center, BorderLayout.CENTER);
        panel.add(right, BorderLayout.EAST);

        return panel;
    }
    
    private void setSearchButtonStyle(JButton button, String iconName) {
        
        final int BTN_WIDTH = 150; // Más ancho para la barra de búsqueda
        final int BTN_HEIGHT = 40; // Mantener la misma altura de los otros botones

        // Cargamos la imagen con las nuevas dimensiones (IconLoader.load(name, width, height))
        Icon scaledIcon = IconLoader.load(iconName, BTN_WIDTH, BTN_HEIGHT); 
        button.setIcon(scaledIcon);

        // Ocultamos el texto real
        button.setText(null); 

        // Estilos para hacer que parezca solo la imagen
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        
        // El tamaño del botón coincide con el tamaño del icono
        button.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
    }
    
    private void setIconButtonStyle(JButton button) {
        button.setText(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(30, 30));
    }
    
    private void setActionButtonStyle(JButton button, String iconName) {
        // Dimensiones estándar para los botones de acción (100x40, como definimos en la corrección anterior)
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


    private void doSearch() {
        java.awt.Window w = SwingUtilities.getWindowAncestor(this);
        
        SearchDialog queryDlg = new SearchDialog((w instanceof java.awt.Frame) ? (java.awt.Frame) w : null);
        String query = queryDlg.showDialog();
                
        if (query == null || query.isEmpty()) return;

        // Realizamos la búsqueda
        List<FileInfo> results = frame.getFileManager().searchInAdmin(query);
        
        // Creamos y mostramos el nuevo diálogo de resultados
        SearchResultsDialog resultsDlg = new SearchResultsDialog(
            (w instanceof java.awt.Frame) ? (java.awt.Frame) w : null, 
            results
        );
        resultsDlg.setVisible(true);
    }

    private void doUpload() {
        try {
            frame.getOrganizer().uploadFromBridge();
            refreshAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during upload: " + ex.getMessage(),
                    "Upload error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doNewFolder() {
        File targetFolder = currentFolder;
        
        if (targetFolder == null) {
             targetFolder = frame.getPathUtils().getAdminRoot();
        }

        String name = JOptionPane.showInputDialog(this, "New folder name (in " + targetFolder.getName() + "):", "New Folder",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.isBlank()) return;
        try {
            frame.getFileManager().createFolder(targetFolder, name.trim());
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating folder: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private FileInfo getSelectedFileInfoFromTree() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
             JOptionPane.showMessageDialog(this, "Please select an item in the tree.");
             return null;
        }
        Object comp = path.getLastPathComponent();
        if (comp instanceof DefaultMutableTreeNode node) {
            Object userObj = node.getUserObject();
            if (userObj instanceof File f) {
                return frame.getFileManager().findFileInfo(f);
            }
        }
        JOptionPane.showMessageDialog(this, "Please select a valid file or folder.");
        return null;
            
    }

    private void doMove() {
        FileInfo fi = getSelectedFileInfoFromTree();
        if (fi == null) return;

        String destPath = JOptionPane.showInputDialog(this, "Destination path (within admin):",
                fi.getFile().getParent());
        if (destPath == null || destPath.isBlank()) return;

        try {
            frame.getFileManager().move(fi.getFile(), new File(destPath.trim()));
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error moving file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doCopy() {
        FileInfo fi = getSelectedFileInfoFromTree();
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

        String newName = txtName.getText().trim();
        String destPath = txtPath.getText().trim();
        if (newName.isEmpty() || destPath.isEmpty()) return;

        try {
            frame.getFileManager().copy(fi.getFile(), new File(destPath), newName);
            refreshAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error copying file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDeleteSoft() {
        FileInfo fi = getSelectedFileInfoFromTree();
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
            refreshAll();
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

    public void refreshAll() {
        refreshTree();
        refreshDropdown();
    }

    public void refreshTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("admin");
        File adminRoot = frame.getPathUtils().getAdminRoot();
        buildTree(adminRoot, root);
        treeModel.setRoot(root);
        treeModel.reload();
    }

    private void buildTree(File dir, DefaultMutableTreeNode parent) {
        File[] children = dir.listFiles();
        if (children == null) return;

        for (File child : children) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
            parent.add(node);
            if (child.isDirectory()) {
                buildTree(child, node);
            }
        }
    }

    public void refreshDropdown() {
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

        comboLocation.setSelectedItem("Home");
    }
}