/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scribble;

/**
 *
 * @author ashley
 */

import admin.core.SessionManager; 
import admin.core.FileSystemManager;
import scribble.core.ScribbleEditorCore;
import admin.core.User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.filechooser.FileSystemView; 

public class ScribbleFrame extends JFrame {

    private final ScribbleEditorCore editorCore;
    
    private JTextPane textPane;
    private JComboBox<String> fontCombo;
    private JComboBox<Integer> sizeCombo;
    private JButton colorButton;
    private JButton saveButton;
    private JButton openButton;

    private JFileChooser fileChooser;

    public ScribbleFrame() {
        this.editorCore = new ScribbleEditorCore();

        setTitle("Scribble - Editor de texto");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
    }
    
    public ScribbleFrame(File fileToOpen) {
        this();

        if (fileToOpen != null && fileToOpen.isFile()) {
            try {
                editorCore.open(fileToOpen);
                textPane.setDocument(editorCore.getDocument());
                setTitle("Scribble - " + fileToOpen.getName());
            } catch (IOException | BadLocationException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al abrir el archivo desde Litter Box:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private File getDocumentsDirectory() {
        
        File userRoot = getUserRootDirectory();
        File documentsDir = new File(userRoot, "Documents");
        
        if (!documentsDir.exists()) {
            documentsDir.mkdirs();
        }
        
        return documentsDir;
    }
    
    private File getPhysicalZRootDirectory() {
        
        String physicalZRootPath = System.getProperty("user.dir") + File.separator + "Z:";
        File physicalZRoot = new File(physicalZRootPath);
        
        if (!physicalZRoot.exists()) {
            physicalZRoot.mkdirs();
        }
        return physicalZRoot;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        String loggedInUsername = "admin";
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            loggedInUsername = SessionManager.getCurrentUser().getUsername();
        }
        
        File documentsDir = getDocumentsDirectory();
        File userRoot = getUserRootDirectory();
        
        FileSystemView view;
        File initialDir;
        
        if ("admin".equals(loggedInUsername)) {
            File physicalZRoot = getPhysicalZRootDirectory(); 
            view = new RestrictedFileSystemView(physicalZRoot, userRoot); 
            initialDir = userRoot;

        } else {
            view = new RestrictedFileSystemView(userRoot, documentsDir);
            initialDir = documentsDir;
        }
        
        fileChooser = new JFileChooser(initialDir, view); 

        createTextPane();
        createToolbar();
        createMenuBar();
    }

    private void createTextPane() {
        textPane = new JTextPane();
        
        textPane.setEditorKit(editorCore.getEditorKit());
        textPane.setDocument(editorCore.getDocument());
        
        editorCore.setCurrentFont(textPane.getFont().getFamily());
        editorCore.setCurrentSize(textPane.getFont().getSize());
        editorCore.setCurrentColor(Color.BLACK);
        
        textPane.setCharacterAttributes(editorCore.getCurrentAttributes(), false);

        JScrollPane scroll = new JScrollPane(textPane);
        add(scroll, BorderLayout.CENTER);
    }

    private void createToolbar() {
        JToolBar toolBar = new JToolBar();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();
        
        fontCombo = new JComboBox<>(fonts);
        fontCombo.setPreferredSize(new Dimension(200, 40));
        fontCombo.setSelectedItem(textPane.getFont().getFamily());
        fontCombo.addActionListener(e -> {
            editorCore.setCurrentFont((String) fontCombo.getSelectedItem());
            applyCurrentStyleToSelectionOrTyping();
        });
        
        toolBar.add(new JLabel("Fuente: "));
        toolBar.add(fontCombo);
        toolBar.addSeparator();
        
        Integer[] sizes = { 8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 42, 48, 64 };
        sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setPreferredSize(new Dimension(80, 40));
        sizeCombo.setSelectedItem(textPane.getFont().getSize());
        sizeCombo.addActionListener(e -> {
            editorCore.setCurrentSize((Integer) sizeCombo.getSelectedItem());
            applyCurrentStyleToSelectionOrTyping();
        });
        toolBar.add(new JLabel("Tamaño: "));
        toolBar.add(sizeCombo);
        toolBar.addSeparator();
        
        final int ICON_SIZE = 55;
        
        colorButton = new JButton();
        colorButton.setIcon(IconLoader.load("color.png", ICON_SIZE, ICON_SIZE));
        colorButton.setToolTipText("Color de texto");
        colorButton.addActionListener(this::chooseColor);
        toolBar.add(colorButton);
        toolBar.addSeparator();
        
        openButton = new JButton();
        openButton.setIcon(IconLoader.load("open.png", ICON_SIZE, ICON_SIZE));
        openButton.setToolTipText("Abrir archivo");
        openButton.addActionListener(e -> openFile()); 
        toolBar.add(openButton);
        toolBar.addSeparator();
        
        saveButton = new JButton();
        saveButton.setIcon(IconLoader.load("save.png", ICON_SIZE, ICON_SIZE));
        saveButton.setToolTipText("Guardar");
        saveButton.addActionListener(e -> saveFile(false));
        toolBar.add(saveButton);
        
        makeIconOnlyButton(colorButton, ICON_SIZE);
        makeIconOnlyButton(openButton, ICON_SIZE);
        makeIconOnlyButton(saveButton, ICON_SIZE);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void makeIconOnlyButton(JButton button, int size) {
        button.setText(null); 
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        button.setPreferredSize(new Dimension(size + 6, size)); 
    }
    
    private void chooseColor(ActionEvent e) {
        Color chosen = JColorChooser.showDialog(this,
                "Seleccionar color de texto", Color.BLACK);
        if (chosen != null) {
            editorCore.setCurrentColor(chosen);
            applyCurrentStyleToSelectionOrTyping();
        }
    }
    
    private void applyCurrentStyleToSelectionOrTyping() {
        var attrs = editorCore.getCurrentAttributes();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start != end) {
            StyledDocument doc = (StyledDocument) textPane.getDocument();
            doc.setCharacterAttributes(start, end - start, attrs, false);
        } else {
            textPane.setCharacterAttributes(attrs, false);
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");

        JMenuItem newItem = new JMenuItem("Nuevo");
        newItem.addActionListener(e -> newFile());

        JMenuItem openItem = new JMenuItem("Abrir...");
        openItem.addActionListener(e -> openFile());

        JMenuItem saveItem = new JMenuItem("Guardar");
        saveItem.addActionListener(e -> saveFile(false));

        JMenuItem saveAsItem = new JMenuItem("Guardar como...");
        saveAsItem.addActionListener(e -> saveFile(true));

        JMenuItem exitItem = new JMenuItem("Salir");
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private void newFile() {
        editorCore.newDocument();
        textPane.setDocument(editorCore.getDocument());
        setTitle("Scribble - Editor de texto");
    }
    
    private void openFile() {

        String loggedInUsername = "admin";
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            loggedInUsername = SessionManager.getCurrentUser().getUsername();
        }

        File initialRoot;
        if ("admin".equals(loggedInUsername)) {
            initialRoot = getPhysicalZRootDirectory();
        } else {
            initialRoot = getUserRootDirectory();
        }
        
        CatOSFileChooserDialog dialog = new CatOSFileChooserDialog(this, initialRoot, editorCore.getCurrentFile());
        dialog.setVisible(true);

        File file = dialog.getSelectedFile();

        if (file != null) {
            try {
                editorCore.open(file);
                textPane.setDocument(editorCore.getDocument());
                setTitle("Scribble - " + file.getName());
            } catch (IOException | BadLocationException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error al abrir el archivo:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private File getUserRootDirectory() {
        String loggedInUsername = "admin";
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            loggedInUsername = SessionManager.getCurrentUser().getUsername();
        }
        
        File physicalZRoot = getPhysicalZRootDirectory(); 
        File userRoot = new File(physicalZRoot, loggedInUsername);

        if (!userRoot.exists()) {
            userRoot.mkdirs();
        }

        return userRoot;
    }
    
    private void saveFile(boolean saveAs) {
        File file = editorCore.getCurrentFile();

        if (file == null || saveAs) {

            String loggedInUsername = "admin";
            if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
                loggedInUsername = SessionManager.getCurrentUser().getUsername();
            }

            File initialRoot;
            // La raíz del diálogo depende del usuario: Z:/ para admin, Z:/<user> para normal.
            if ("admin".equals(loggedInUsername)) {
                initialRoot = getPhysicalZRootDirectory();
            } else {
                initialRoot = getUserRootDirectory();
            }

            // 1. Mostrar la nueva ventana de "Guardar Como"
            CatOSSaveAsDialog dialog = new CatOSSaveAsDialog(this, initialRoot);
            dialog.setVisible(true);

            // 2. Obtener el archivo final seleccionado/nombrado por el usuario
            File selectedFile = dialog.getFinalSaveFile();

            if (selectedFile == null) {
                // El usuario presionó Cancelar o cerró la ventana
                return;
            }

            // 3. Usar el archivo seleccionado para guardar
            file = selectedFile;
            // --- FIN LOGICA DE DIALOGO CUSTOMIZADO ---
        }

        try {
            editorCore.save(file);
            setTitle("Scribble - " + file.getName());
        } catch (IOException | BadLocationException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el archivo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

