/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package admin.core;

/**
 *
 * @author ashley
 */

public class User {
    
    private String username;
    private String password;
    private String rootPath;
    private boolean admin;

    public User(String username, String password, String rootPath, boolean admin) {
        this.username = username;
        this.password = password;
        this.rootPath = rootPath;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRootPath() {
        return rootPath;
    }

    public boolean isAdmin() {
        return admin;
    }
    
}
