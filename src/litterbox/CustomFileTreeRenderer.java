/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox;

/**
 *
 * @author ashley
 */

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CustomFileTreeRenderer extends DefaultTreeCellRenderer {
    
    private final Icon adminIcon;
    private final Icon picturesIcon;
    private final Icon musicIcon;
    private final Icon documentsIcon;
    private final Icon defaultFolderIcon;
    
    private final Icon pictureFileIcon;
    private final Icon musicFileIcon;
    private final Icon defaultFileIcon;

    public CustomFileTreeRenderer() {
        adminIcon = IconLoader.load("user_main_dir.png");
        picturesIcon = IconLoader.load("photos.png");
        musicIcon = IconLoader.load("music.png");
        documentsIcon = IconLoader.load("files.png");
        defaultFolderIcon = IconLoader.load("new_dir.png");
        
        pictureFileIcon = IconLoader.load("photo.png");
        musicFileIcon = IconLoader.load("tape_recorder.png");
        defaultFileIcon = IconLoader.load("file.png");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode node) {
            Object userObj = node.getUserObject();

            if (userObj instanceof String name) {
                setText(name); 
                if ("admin".equals(name)) {
                    setIcon(adminIcon);
                    setToolTipText("Directorio principal del usuario");
                } 
            } 

            else if (userObj instanceof File f) {
                setText(f.getName());

                String name = f.getName();

                if (f.isDirectory()) {
                    switch (name) {
                        case "Pictures" -> setIcon(picturesIcon);
                        case "Music" -> setIcon(musicIcon);
                        case "Documents" -> setIcon(documentsIcon);
                        default -> setIcon(defaultFolderIcon);
                    }
                } else {
                    String ext = getExtension(f.getName()).toLowerCase();

                    switch (ext) {
                        case "png", "jpg", "jpeg" -> setIcon(pictureFileIcon);
                        case "mp3" -> setIcon(musicFileIcon);
                        case "txt", "doc", "docx", "pdf" -> setIcon(defaultFileIcon);
                        default -> setIcon(defaultFileIcon);
                    }
                }
                setToolTipText(f.getAbsolutePath());
            }
        }

        setForeground(Color.WHITE);
        setBackgroundNonSelectionColor(Color.decode("#545454"));

        return this;
    }
    
    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        if (idx < 0) return "";
        return name.substring(idx + 1);
    }
}