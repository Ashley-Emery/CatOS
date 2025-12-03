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
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo {
    
    private final File file;
    private final FileType type;
    private final long size;
    private final long lastModified;

    public FileInfo(File file, FileType type) {
        this.file = file;
        this.type = type;
        this.size = file.isDirectory() ? -1L : file.length();
        this.lastModified = file.lastModified();
    }

    public File getFile() {
        return file;
    }

    public FileType getType() {
        return type;
    }

    public String getDisplayName() {
        return file.getName();
    }

    public String getFormattedModified() {
        if (lastModified <= 0) return "";
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy");
        return fmt.format(new Date(lastModified));
    }

    public String getTypeText() {
        switch (type) {
            case FOLDER -> {
                return "Folder";
            }
            case PICTURE -> {
                return "Picture";
            }
            case MUSIC -> {
                return "MP3";
            }
            case DOCUMENT -> {
                return "File";
            }
            default -> {
                return "File";
            }
        }
    }

    public String getFormattedSize() {
        
        if (file.isDirectory()) {
            return "";
        }
        
        long bytes = size;
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format("%.1f kB", kb);
        double mb = kb / 1024.0;
        return String.format("%.1f MB", mb);
    }
}
