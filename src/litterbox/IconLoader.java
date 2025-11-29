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
import java.net.URL;

public class IconLoader {

    public static ImageIcon load(String name) {
        String path = "/litterbox/assets/" + name;
        URL url = IconLoader.class.getResource(path);
        if (url == null) {
            System.err.println("No se encontró el recurso: " + path);
            return null;
        }
        return new ImageIcon(url);
    }
}
