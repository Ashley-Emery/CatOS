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

public class PathUtils {

    private final File userRoot;
    private final File uploadsDir;

    public PathUtils(String username) {
        
        String userDir = System.getProperty("user.dir");
        String physicalZRoot = userDir + File.separator + "Z:";

        this.userRoot = new File(physicalZRoot, username); 
        this.uploadsDir = new File(userDir, "Uploads");
    }

    public File getUserRoot() {
        return userRoot;
    }

    public File getUploadsDir() {
        return uploadsDir;
    }

    public File getTrashDir() {
        return new File(userRoot, "Trash"); 
    }

    public boolean isInsideUserRoot(File f) {
        try {
            String rootPath = userRoot.getCanonicalPath(); 
            String filePath = f.getCanonicalPath();
            return filePath.startsWith(rootPath);
        } catch (Exception ex) {
            return false;
        }
    }
    
    public File findRootLogicalFor(File f) {
        
        File parent = f;
        File rootCandidate = userRoot;
        try {
            String rootPath = userRoot.getCanonicalPath();
            while (parent != null) {
                File up = parent.getParentFile();
                if (up == null) break;
                String upPath = up.getCanonicalPath();
                if (upPath.equals(rootPath)) {
                    rootCandidate = parent;
                    break;
                }
                parent = up;
            }
        } catch (Exception ignored) {}
        return rootCandidate;
    }
}