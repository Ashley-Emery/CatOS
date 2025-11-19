/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin.core;

/**
 *
 * @author ashley
 */

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    
    private static final Map<String, User> users = new HashMap<>();

    static {

        String root = FileSystemManager.getUserRoot("admin");
        User admin = new User("admin", "admin123", root, true);
        users.put("admin", admin);

        FileSystemManager.createUserFolders("admin");
    }

    private UserManager() {}

    public static boolean createUser(String username, String password, boolean isAdmin) {
        if (users.containsKey(username)) {
            return false;
        }

        String root = FileSystemManager.getUserRoot(username);
        User user = new User(username, password, root, isAdmin);

        boolean foldersOk = FileSystemManager.createUserFolders(username);
        if (!foldersOk) {
            return false;
        }

        users.put(username, user);

        return true;
    }

    public static User findUser(String username) {
        return users.get(username);
    }
    
}
