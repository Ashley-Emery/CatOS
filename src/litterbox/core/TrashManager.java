/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox.core;

/**
 *
 * @author ashley
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TrashManager {

    private final PathUtils pathUtils;
    private final FileManager fileManager;

    private final File indexFile;

    public TrashManager(PathUtils pathUtils) {
        this.pathUtils = pathUtils;
        this.fileManager = new FileManager(pathUtils);
        this.indexFile = new File(pathUtils.getAdminRoot(), ".trashindex.txt");
    }

    public void moveToTrash(File src) throws IOException {
        File trashDir = pathUtils.getTrashDir();
        if (!trashDir.exists()) {
            trashDir.mkdirs();
        }

        if (!pathUtils.isInsideAdmin(src)) {
            throw new IOException("Only items inside admin can be trashed.");
        }

        String originalPath = src.getCanonicalPath();
        
        String baseName = src.getName();
        String uniqueName = findUniqueNameInTrash(baseName);
        
        File dest = new File(trashDir, uniqueName);
        Files.move(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        appendIndex(uniqueName, originalPath);
    }

    private String findUniqueNameInTrash(String baseName) {
        File trashDir = pathUtils.getTrashDir();
        File candidate = new File(trashDir, baseName);
        if (!candidate.exists()) return baseName;

        String name = baseName;
        String ext = "";
        int idx = baseName.lastIndexOf('.');
        if (idx != -1) {
            name = baseName.substring(0, idx);
            ext = baseName.substring(idx);
        }

        int counter = 1;
        while (true) {
            String newName = name + "(" + counter + ")" + ext;
            candidate = new File(trashDir, newName);
            if (!candidate.exists()) return newName;
            counter++;
        }
    }

    private synchronized void appendIndex(String trashName, String originalPath) throws IOException {
        try (FileWriter fw = new FileWriter(indexFile, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(trashName + "|" + originalPath);
            bw.newLine();
        }
    }

    private synchronized Map<String, String> loadIndex() {
        Map<String, String> map = new HashMap<>();
        if (!indexFile.exists()) return map;
        try (BufferedReader br = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf('|');
                if (idx < 0) continue;
                String trashName = line.substring(0, idx);
                String originalPath = line.substring(idx + 1);
                map.put(trashName, originalPath);
            }
        } catch (IOException ignored) {}
        return map;
    }

    private synchronized void saveIndex(Map<String, String> map) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile, false))) {
            for (Map.Entry<String, String> e : map.entrySet()) {
                bw.write(e.getKey() + "|" + e.getValue());
                bw.newLine();
            }
        }
    }

    public void restoreFromTrash(File trashFile) throws IOException {
        Map<String, String> index = loadIndex();
        String originalPath = index.get(trashFile.getName());
        if (originalPath == null) {
            throw new IOException("Original path not found in trash index.");
        }

        File originalFile = new File(originalPath);
        File originalDir = originalFile.getParentFile();
        if (!originalDir.exists()) {
            throw new IOException("Original directory no longer exists: " + originalDir);
        }

        Files.move(trashFile.toPath(), originalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        index.remove(trashFile.getName());
        saveIndex(index);
    }

    public void deletePermanent(File f) throws IOException {
        
        Map<String, String> index = loadIndex();
        if (index.containsKey(f.getName())) {
            index.remove(f.getName());
            saveIndex(index);
        }
        fileManager.deleteRecursive(f);
    }
}
