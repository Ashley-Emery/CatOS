/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scribble.core;

/**
 *
 * @author ashley
 */

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.rtf.RTFEditorKit;

public class ScribbleEditorCore {

    private final RTFEditorKit rtfKit;
    private Document document;
    private File currentFile;

    private String currentFont;
    private int currentSize;
    private Color currentColor;

    public ScribbleEditorCore() {
        this.rtfKit = new RTFEditorKit();
        this.document = rtfKit.createDefaultDocument();
        
        this.currentFont = "Serif";
        this.currentSize = 14;
        this.currentColor = Color.BLACK;
    }
    
    public void newDocument() {
        this.document = rtfKit.createDefaultDocument();
        this.currentFile = null;
    }
    
    public void open(File file) throws IOException, BadLocationException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser null");
        }

        Document newDoc = rtfKit.createDefaultDocument();
        try (FileInputStream in = new FileInputStream(file)) {
            rtfKit.read(in, newDoc, 0);
        }

        this.document = newDoc;
        this.currentFile = file;
    }

    public void save(File file) throws IOException, BadLocationException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser null");
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            rtfKit.write(out, document, 0, document.getLength());
        }
        this.currentFile = file;
    }

    public Document getDocument() {
        return document;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public RTFEditorKit getEditorKit() {
        return rtfKit;
    }

    public void setCurrentFont(String font) {
        if (font != null) {
            this.currentFont = font;
        }
    }

    public void setCurrentSize(int size) {
        if (size > 0) {
            this.currentSize = size;
        }
    }

    public void setCurrentColor(Color color) {
        if (color != null) {
            this.currentColor = color;
        }
    }
    
    public SimpleAttributeSet getCurrentAttributes() {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, currentFont);
        StyleConstants.setFontSize(attrs, currentSize);
        StyleConstants.setForeground(attrs, currentColor);
        return attrs;
    }
}

