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

    private final File adminRoot;
    private final File uploadsDir;

    public PathUtils() {
        
        this.adminRoot = new File("/home/ashley/NetBeansProjects/CatOS/Z:/admin");
        this.uploadsDir = new File("/home/ashley/NetBeansProjects/CatOS/Uploads");
    }

    public File getAdminRoot() {
        return adminRoot;
    }

    public File getUploadsDir() {
        return uploadsDir;
    }

    public File getTrashDir() {
        return new File(adminRoot, "Trash");
    }

    public boolean isInsideAdmin(File f) {
        try {
            String rootPath = adminRoot.getCanonicalPath();
            String filePath = f.getCanonicalPath();
            return filePath.startsWith(rootPath);
        } catch (Exception ex) {
            return false;
        }
    }

    public File findRootLogicalFor(File f) {
        
        File parent = f;
        File rootCandidate = adminRoot;
        try {
            String rootPath = adminRoot.getCanonicalPath();
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