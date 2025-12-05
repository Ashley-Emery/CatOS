/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scribble;

/**
 *
 * @author ashley
 */

import javax.swing.ImageIcon;
import java.awt.Image;

public class IconLoader {
    
    private static final String ASSETS_PATH = "/scribble/assets/"; 
    
    public static ImageIcon load(String filename) {
        return load(filename, -1, -1);
    }
    
    public static ImageIcon load(String filename, int width, int height) {
        try {
            var url = IconLoader.class.getResource(ASSETS_PATH + filename);
            if (url == null) {
                System.err.println("Icon not found: " + ASSETS_PATH + filename);
                return null;
            }
            ImageIcon icon = new ImageIcon(url);
            
            if (width > 0 && height > 0) {
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            return icon;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
