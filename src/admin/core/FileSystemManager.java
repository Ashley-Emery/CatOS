/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin.core;

/**
 *
 * @author ashley
 */

import java.io.File;

public class FileSystemManager {
    
    public static final String ROOT_DRIVE = "Z:/";

    public static String getUserRoot(String username) {
        return ROOT_DRIVE + username;
    }

    public static void ensureBaseStructure() {

        File root = new File(ROOT_DRIVE);
        if (!root.exists()) {
            root.mkdirs();
        }
    }

    public static boolean createUserFolders(String username) {
        ensureBaseStructure();
        File userRoot = new File(getUserRoot(username));

        if (!userRoot.exists() && !userRoot.mkdirs()) {
            return false;
        }

        File litterBox = new File(userRoot, "Litter Box");
        File iTuna = new File(userRoot, "iTuna");
        File photos = new File(userRoot, "Photos");

        boolean ok = true;
        if (!litterBox.exists()) ok &= litterBox.mkdirs();
        if (!iTuna.exists()) ok &= iTuna.mkdirs();
        if (!photos.exists()) ok &= photos.mkdirs();

        return ok;
    }
    
}
