/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox.core;

/**
 *
 * @author ashley
 */

import java.io.File;

public class HistoryLocation {

    public enum Type {
        HOME,
        FOLDER,
        TRASH
    }

    private final Type type;
    private final File rootFolder;
    private final File currentFolder;

    public HistoryLocation(Type type, File rootFolder, File currentFolder) {
        this.type = type;
        this.rootFolder = rootFolder;
        this.currentFolder = currentFolder;
    }

    public Type getType() {
        return type;
    }

    public File getRootFolder() {
        return rootFolder;
    }

    public File getCurrentFolder() {
        return currentFolder;
    }
}