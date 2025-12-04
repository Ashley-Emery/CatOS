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

public class ScribbleFrame extends JFrame {

    private final ScribbleEditorCore editorCore;

    // GUI
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
    
    private File getDocumentsDirectory() {
        String loggedInUsername = "admin";
        if (SessionManager.isLoggedIn() && SessionManager.getCurrentUser() != null) {
            loggedInUsername = SessionManager.getCurrentUser().getUsername();
        }

        String logicalUserRoot = FileSystemManager.getUserRoot(loggedInUsername);

        String physicalZRoot = System.getProperty("user.dir") + File.separator + "Z:";

        File userRoot = new File(physicalZRoot, loggedInUsername);
        File documentsDir = new File(userRoot, "Documents");

        if (!documentsDir.exists()) {
            documentsDir.mkdirs();
        }

        return documentsDir;
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        fileChooser = new JFileChooser(getUserRootDirectory());

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
        fontCombo.setPreferredSize(new Dimension(200, 25));
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
        sizeCombo.setPreferredSize(new Dimension(80, 25));
        sizeCombo.setSelectedItem(textPane.getFont().getSize());
        sizeCombo.addActionListener(e -> {
            editorCore.setCurrentSize((Integer) sizeCombo.getSelectedItem());
            applyCurrentStyleToSelectionOrTyping();
        });
        toolBar.add(new JLabel("Tamaño: "));
        toolBar.add(sizeCombo);
        toolBar.addSeparator();
        
        colorButton = new JButton("Color");
        colorButton.addActionListener(this::chooseColor);
        toolBar.add(colorButton);
        toolBar.addSeparator();
        
        openButton = new JButton("Abrir");
        openButton.addActionListener(e -> openFile()); 
        toolBar.add(openButton);
        toolBar.addSeparator();
        
        saveButton = new JButton("Guardar");
        saveButton.addActionListener(e -> saveFile(false));
        toolBar.add(saveButton);
        
        add(toolBar, BorderLayout.NORTH);
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
        
        fileChooser.setCurrentDirectory(getUserRootDirectory()); 

        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
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
        
        String physicalZRoot = System.getProperty("user.dir") + File.separator + "Z:";
        File userRoot = new File(physicalZRoot, loggedInUsername);

        if (!userRoot.exists()) {
            userRoot.mkdirs();
        }

        return userRoot;
    }
    
    private void saveFile(boolean saveAs) {
        File file = editorCore.getCurrentFile();

        if (file == null || saveAs) {
            fileChooser.setCurrentDirectory(getUserRootDirectory());
        
        if (file == null) {
            File defaultFile = new File(getUserRootDirectory(), "Nuevo Documento.txt");
            fileChooser.setSelectedFile(defaultFile);
        } else {
            
            fileChooser.setSelectedFile(file);
        }
        
        int option = fileChooser.showSaveDialog(this);
        
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        file = fileChooser.getSelectedFile();
            
            String name = file.getName().toLowerCase();
            if (!name.endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
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

