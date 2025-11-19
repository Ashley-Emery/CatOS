/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin.core;

/**
 *
 * @author ashley
 */
public class SessionManager {
    
    private static User currentUser;

    private SessionManager() {
    }

    public static boolean login(String username, String password) {
        User user = UserManager.findUser(username);
        if (user == null) {
            return false;
        }
        if (!user.getPassword().equals(password)) {
            return false;
        }
        currentUser = user;
        return true;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
}
