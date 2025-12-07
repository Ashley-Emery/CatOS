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
import java.awt.*;
import java.io.File;
import java.util.Vector;

public class CatOSFileChooserDialog extends JDialog {

    private final File rootDirectory;
    private File selectedFile;
    private JLabel currentPathLabel;
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private File currentDirectory;

    public CatOSFileChooserDialog(JFrame owner, File initialRoot, File currentFile) {
        super(owner, "Seleccionar archivo .cat", true);
        this.rootDirectory = initialRoot;
        this.currentDirectory = currentFile != null ? currentFile.getParentFile() : initialRoot;
        
        if (this.currentDirectory == null || !this.currentDirectory.isDirectory()) {
            this.currentDirectory = initialRoot;
        }

        initComponents();
        loadDirectoryContent(this.currentDirectory);
        setSize(500, 400);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new BorderLayout());
        currentPathLabel = new JLabel("Ruta: " + currentDirectory.getAbsolutePath());
        JButton upButton = new JButton("Arriba");
        upButton.addActionListener(e -> navigateUp());
        topPanel.add(upButton, BorderLayout.WEST);
        topPanel.add(currentPathLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setCellRenderer(new FileListRenderer());
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    File selected = fileList.getSelectedValue();
                    if (selected.isDirectory()) {
                        loadDirectoryContent(selected);
                    } else {
                        
                        selectedFile = selected;
                        dispose(); 
                    }
                }
            }
        });
        add(new JScrollPane(fileList), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selectButton = new JButton("Seleccionar");
        selectButton.addActionListener(e -> selectFileAndClose());
        
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> { selectedFile = null; dispose(); });

        bottomPanel.add(selectButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadDirectoryContent(File dir) {
        if (!dir.canRead() || !dir.isDirectory()) {
            JOptionPane.showMessageDialog(this, "No se puede acceder a este directorio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!dir.getAbsolutePath().startsWith(rootDirectory.getAbsolutePath()) && !dir.equals(rootDirectory)) {
             JOptionPane.showMessageDialog(this, "Acceso restringido fuera de CatOS.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        currentDirectory = dir;
        currentPathLabel.setText("Ruta: " + currentDirectory.getAbsolutePath().replace(rootDirectory.getParent(), "Z:"));
        
        listModel.clear();
        
        File[] files = currentDirectory.listFiles();
        
        if (files != null) {
            Vector<File> directories = new Vector<>();
            Vector<File> documents = new Vector<>();
            
            for (File file : files) {
                if (file.isDirectory()) {
                    directories.add(file);
                } else {
                    // Solo mostrar archivos .cat (o los que quieras)
                    if (file.getName().toLowerCase().endsWith(".cat")) {
                        documents.add(file);
                    }
                }
            }
            
            directories.sort(null);
            documents.sort(null);
            
            directories.forEach(listModel::addElement);
            documents.forEach(listModel::addElement);
        }
    }
    
    private void navigateUp() {
        if (currentDirectory.equals(rootDirectory)) {
            
            return; 
        }
        
        File parent = currentDirectory.getParentFile();
        if (parent != null) {
            loadDirectoryContent(parent);
        }
    }
    
    private void selectFileAndClose() {
        File selected = fileList.getSelectedValue();
        if (selected != null && !selected.isDirectory()) {
            selectedFile = selected;
            dispose();
        } else if (selected != null && selected.isDirectory()) {
            loadDirectoryContent(selected);
        }
    }
    
    public File getSelectedFile() {
        return selectedFile;
    }
    
    private class FileListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            File file = (File) value;
            setText(file.getName());
            if (file.isDirectory()) {
                setIcon(UIManager.getIcon("FileView.directoryIcon"));
            } else {
                setIcon(UIManager.getIcon("FileView.fileIcon"));
            }
            return this;
        }
    }
}
