/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scribble;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeExpansionEvent;

public class CatOSSaveAsDialog extends JDialog {

    private final File rootDirectory;
    private File selectedDirectory;
    private JTextField fileNameField;
    private JButton saveButton;
    private JButton cancelButton;
    
    private File finalSaveFile; 

    public CatOSSaveAsDialog(JFrame owner, File initialRoot) {
        super(owner, "Guardar Archivo .cat Como", true);
        this.rootDirectory = initialRoot;
        this.selectedDirectory = initialRoot;
        
        initComponents();
        setSize(600, 500);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        DefaultMutableTreeNode top = createNodes(rootDirectory);

        DefaultTreeModel treeModel = new DefaultTreeModel(top);
        JTree fileTree = new JTree(treeModel);
        
        fileTree.setCellRenderer(new FileTreeRenderer(rootDirectory));

        fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        fileTree.setRootVisible(true);
        fileTree.setShowsRootHandles(true);

        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            if (node == null) return;

            Object nodeInfo = node.getUserObject();
            if (nodeInfo instanceof File) {
                File file = (File) nodeInfo;
                if (file.isDirectory()) {
                    selectedDirectory = file;
                }
            }
        });

        fileTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

                if (node.getUserObject() instanceof File) {
                    File directory = (File) node.getUserObject();

                    // *** CORRECCIÓN APLICADA AQUÍ ***
                    if (node.getChildCount() == 1 && 
                        ((DefaultMutableTreeNode) node.getFirstChild()).getUserObject().equals("dummy")) { 

                        node.removeAllChildren();

                        File[] files = directory.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (file.isDirectory()) {
                                    DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(file);

                                    if (hasSubDirectories(file)) {
                                        dirNode.add(new DefaultMutableTreeNode("dummy"));
                                    }
                                    node.add(dirNode);
                                }
                            }
                        }
                        ((DefaultTreeModel) fileTree.getModel()).nodeStructureChanged(node);
                    }
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });

        TreePath rootPath = new TreePath(top);
        fileTree.expandPath(rootPath);

        fileTree.setSelectionPath(rootPath);

        add(new JScrollPane(fileTree), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(10, 10));

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(new JLabel("Nombre del archivo:"));
        fileNameField = new JTextField(30);
        fileNameField.setText("Nuevo Documento.cat");
        namePanel.add(fileNameField);
        southPanel.add(namePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> attemptSave());
        cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> { finalSaveFile = null; dispose(); });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }
    
    private DefaultMutableTreeNode createNodes(File root) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
        
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(file);
                        
                        if (hasSubDirectories(file)) {
                            dirNode.add(new DefaultMutableTreeNode("dummy"));
                        }
                        rootNode.add(dirNode);
                    }
                }
            }
        }
        return rootNode;
    }
    
    private boolean hasSubDirectories(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return false;
        for (File file : files) {
            if (file.isDirectory()) {
                return true;
            }
        }
        return false;
    }
    
    private void attemptSave() {
        String fileName = fileNameField.getText().trim();

        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un nombre de archivo.", "Error de Guardado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!fileName.toLowerCase().endsWith(".cat")) {
            fileName += ".cat";
        }
        
        File targetFile = new File(selectedDirectory, fileName);
        
        if (targetFile.exists()) {
             int confirm = JOptionPane.showConfirmDialog(this, 
                     "El archivo ya existe. ¿Desea sobrescribirlo?", "Confirmar Sobrescritura", 
                     JOptionPane.YES_NO_OPTION);
             if (confirm != JOptionPane.YES_OPTION) {
                 return;
             }
        }

        this.finalSaveFile = targetFile;
        dispose();
    }

    public File getFinalSaveFile() {
        return finalSaveFile;
    }
    
    private class FileTreeRenderer extends DefaultTreeCellRenderer {
        private final String projectRootPath;
        private final File zRoot; // La referencia al File Z:/ físico

        public FileTreeRenderer(File zRoot) {
            this.zRoot = zRoot;
            this.projectRootPath = zRoot.getParentFile().getAbsolutePath();
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

            if (userObject instanceof File) {
                File file = (File) userObject;
                String path = file.getAbsolutePath();
                
                if (file.equals(zRoot)) {
                    setText("Z:/");
                } 
                
                else if (file.getParentFile().equals(zRoot)) {
                    setText(file.getName());
                }
                
                else {
                    setText(file.getName());
                }
                
                if (file.isDirectory()) {
                     setIcon(UIManager.getIcon("FileView.directoryIcon"));
                } else {
                     setIcon(UIManager.getIcon("FileView.fileIcon")); 
                }

            } else if (userObject.equals("dummy")) {
                setText(""); 
            }
            return this;
        }
    }
    
}
