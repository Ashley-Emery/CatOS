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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Organizer {

    private final PathUtils pathUtils;
    private final FileManager fileManager;
    private final TrashManager trashManager;

    public Organizer(PathUtils pathUtils, FileManager fileManager, TrashManager trashManager) {
        this.pathUtils = pathUtils;
        this.fileManager = fileManager;
        this.trashManager = trashManager;
    }

    public void uploadFromBridge() throws IOException {
        File uploads = pathUtils.getUploadsDir();
        if (!uploads.exists() || !uploads.isDirectory()) {
            throw new IOException("Uploads directory does not exist: " + uploads.getAbsolutePath());
        }

        File[] files = uploads.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (!f.isFile()) continue;
            String ext = fileManager.getExtension(f.getName()).toLowerCase();
            File targetDir;
            if ("mp3".equals(ext)) {
                targetDir = new File(pathUtils.getUserRoot(), "Music");
            } else if ("png".equals(ext) || "jpg".equals(ext) || "jpeg".equals(ext)) {
                targetDir = new File(pathUtils.getUserRoot(), "Pictures");
            } else {
                targetDir = new File(pathUtils.getUserRoot(), "Documents");
            }

            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            File targetFile = new File(targetDir, f.getName());
            if (targetFile.exists()) {
                // no duplicar
                continue;
            }

            Files.copy(f.toPath(), targetFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
        }
    }
}

