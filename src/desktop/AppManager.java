/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package desktop;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AppManager {

    private final List<Window> openApps = new ArrayList<>();
    
    public void registerApp(Window window) {
        if (window != null && !openApps.contains(window)) {
            openApps.add(window);
        }
    }
    
    public void unregisterApp(Window window) {
        openApps.remove(window);
    }
    
    public boolean hasOpenApps() {
        return !openApps.isEmpty();
    }
    
    public void closeAllApps() {
        
        List<Window> copy = new ArrayList<>(openApps);
        for (Window w : copy) {
            if (w != null) {
                if (w instanceof JFrame frame) {
                    frame.dispose();
                } else if (w instanceof JDialog dialog) {
                    dialog.dispose();
                } else {
                    w.dispose();
                }
            }
        }
        openApps.clear();
    }
}
