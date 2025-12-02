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
import java.util.stream.Collectors;

public class FileManager {

    private final PathUtils pathUtils;
    private SortMode sortMode = SortMode.NAME;
    private boolean sortAsc = true;

    public FileManager(PathUtils pathUtils) {
        this.pathUtils = pathUtils;
    }

    // ====== Sort control ======

    public void toggleSort(SortMode mode) {
        if (mode == this.sortMode) {
            sortAsc = !sortAsc;
        } else {
            this.sortMode = mode;
            sortAsc = true;
        }
    }

    // ====== Listado de carpeta ======

    public List<FileInfo> listFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return Collections.emptyList();

        List<FileInfo> infos = new ArrayList<>();
        for (File f : files) {
            infos.add(new FileInfo(f, classify(f)));
        }

        Comparator<FileInfo> cmp = getComparator();
        infos.sort(cmp);
        if (!sortAsc) Collections.reverse(infos);
        return infos;
    }

    private Comparator<FileInfo> getComparator() {
        return switch (sortMode) {
            case NAME -> Comparator.comparing(fi -> fi.getFile().getName().toLowerCase());
            case DATE -> Comparator.comparing(fi -> fi.getFile().lastModified());
            case TYPE -> Comparator.comparing(fi -> fi.getType().name());
            case SIZE -> Comparator.comparingLong(fi -> fi.getFile().length());
        };
    }

    private FileType classify(File f) {
        if (f.isDirectory()) return FileType.FOLDER;
        String ext = getExtension(f.getName()).toLowerCase();
        return switch (ext) {
            case "png", "jpg", "jpeg" -> FileType.PICTURE;
            case "mp3" -> FileType.MUSIC;
            case "txt", "doc", "docx", "pdf" -> FileType.DOCUMENT;
            default -> FileType.OTHER;
        };
    }

    public String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        if (idx < 0) return "";
        return name.substring(idx + 1);
    }
    
    public FileInfo findFileInfo(File f) {
        if (f == null) return null;
        return new FileInfo(f, classify(f));
    }

    // ====== Operaciones de FS ======

    public void createFolder(File parent, String name) throws IOException {
        File dir = new File(parent, name);
        if (!pathUtils.isInsideAdmin(dir)) {
            throw new IOException("Destination must be inside admin root.");
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Could not create directory " + dir);
            }
        } else {
            throw new IOException("Folder already exists.");
        }
    }

    public void move(File src, File destDir) throws IOException {
        if (!destDir.exists() || !destDir.isDirectory()) {
            throw new IOException("Destination folder does not exist.");
        }
        if (!pathUtils.isInsideAdmin(destDir)) {
            throw new IOException("Destination must be inside admin root.");
        }
        Path target = destDir.toPath().resolve(src.getName());
        Files.move(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
    }

    public void copy(File src, File destDir, String newName) throws IOException {
        if (!destDir.exists() || !destDir.isDirectory()) {
            throw new IOException("Destination folder does not exist.");
        }
        if (!pathUtils.isInsideAdmin(destDir)) {
            throw new IOException("Destination must be inside admin root.");
        }
        Path target = destDir.toPath().resolve(newName);
        if (src.isDirectory()) {
            copyDirectory(src.toPath(), target);
        } else {
            Files.copy(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            try {
                Path rel = source.relativize(path);
                Path destPath = target.resolve(rel);
                if (Files.isDirectory(path)) {
                    if (!Files.exists(destPath)) {
                        Files.createDirectories(destPath);
                    }
                } else {
                    Files.copy(path, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }

    public int countRecursive(File f) {
        if (!f.exists()) return 0;
        if (f.isFile()) return 1;

        final int[] count = {0};
        try {
            Files.walk(f.toPath()).forEach(p -> count[0]++);
        } catch (IOException e) {
            return 0;
        }
        return count[0];
    }

    public void deleteRecursive(File f) throws IOException {
        if (!f.exists()) return;
        Path path = f.toPath();
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }

    // ====== Búsqueda ======

    public List<FileInfo> searchInAdmin(String query) {
        return searchInFolderRecursive(pathUtils.getAdminRoot(), query);
    }

    public List<FileInfo> searchInFolderRecursive(File folder, String query) {
        String q = query.toLowerCase();
        List<FileInfo> result = new ArrayList<>();
        try {
            Files.walk(folder.toPath())
                    .forEach(p -> {
                        File f = p.toFile();
                        if (f.equals(folder)) return;
                        String name = f.getName().toLowerCase();
                        if (name.contains(q)) {
                            result.add(new FileInfo(f, classify(f)));
                        }
                    });
        } catch (IOException ignored) {
        }
        Comparator<FileInfo> cmp = getComparator();
        result.sort(cmp);
        if (!sortAsc) Collections.reverse(result);
        return result;
    }
}
