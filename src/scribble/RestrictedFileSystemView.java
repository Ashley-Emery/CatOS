/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scribble;

/**
 *
 * @author ashley
 */

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;

public class RestrictedFileSystemView extends FileSystemView {

    private final File userRoot;
    private final File documentsDir;

    public RestrictedFileSystemView(File userRoot, File documentsDir) {
        this.userRoot = userRoot;
        this.documentsDir = documentsDir;
    }
    
    @Override
    public File[] getRoots() {
        return new File[]{ userRoot };
    }
    
    @Override
    public File getDefaultDirectory() {
        return documentsDir; 
    }
    
    @Override
    public File getHomeDirectory() {
        return userRoot;
    }
    
    @Override
    public File createNewFolder(File containingDir) throws IOException {
        
        File newFolder = new File(containingDir, "New Folder");
        
        int i = 1;
        File checkFile = newFolder;
        while (checkFile.exists()) {
            checkFile = new File(containingDir, "New Folder (" + i++ + ")");
        }
        newFolder = checkFile;

        if (newFolder.mkdirs()) {
            return newFolder;
        } else {
            throw new IOException("Could not create folder: " + newFolder.getAbsolutePath());
        }
    }
    
    @Override
    public File getParentDirectory(File dir) {
        if (dir.equals(userRoot)) {
            return null;
        }
        return super.getParentDirectory(dir);
    }
}
