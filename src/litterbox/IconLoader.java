/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import java.awt.Image;
import java.net.URL;

public class IconLoader {

    public static ImageIcon load(String name) {

        return load(name, 24);
    }
    
    public static ImageIcon load(String name, int size) {
        String path = "/litterbox/assets/" + name;
        URL url = IconLoader.class.getResource(path);
        if (url == null) {
            System.err.println("No se encontró el recurso: " + path);
            return null;
        }
        
        ImageIcon originalIcon = new ImageIcon(url);

        if (size > 0) {
            Image image = originalIcon.getImage();
            Image scaledImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }

        return originalIcon;
    }
}