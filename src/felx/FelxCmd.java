/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package felx;

/**
 *
 * @author ashley
 */

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import admin.core.User;
import admin.core.FileSystemManager;
import admin.core.SessionManager;

public class FelxCmd {
    
    private File currentDir;
    
    private final String CATOS_ROOT_ABSOLUTE_PATH;

    public FelxCmd() {
        
        User currentUser = SessionManager.getCurrentUser();
        
        if (currentUser == null) {
            currentDir = new File(System.getProperty("user.dir"));
        } else {
            String userRootPath = FileSystemManager.getUserRoot(currentUser.getUsername());
            this.currentDir = new File(userRootPath);
            
            FileSystemManager.createUserFolders(currentUser.getUsername());
        }
        
        this.CATOS_ROOT_ABSOLUTE_PATH = new File(FileSystemManager.ROOT_DRIVE).getAbsolutePath();
    }

    public String getPrompt() {
        
        String fullPath = currentDir.getAbsolutePath();
        String displayPath;
        
        String rootName = FileSystemManager.ROOT_DRIVE.replace(File.separator, "").replace("/", "");
        
        int zIndex = fullPath.indexOf(rootName);
        
        if (zIndex != -1) {
            displayPath = fullPath.substring(zIndex);
            displayPath = displayPath.replace(File.separator, "/");
        } else {
            displayPath = fullPath;
        }
        
        return "\n" + displayPath + ">";
    }

    public String procesarComando(String linea) {

        linea = linea.trim();

        if (linea.isEmpty()) {
            return "";
        }

        String[] partes = linea.split("\\s+");
        String comando = partes[0];
        String argumentos = (linea.length() > comando.length())
                ? linea.substring(comando.length()).trim()
                : "";

        StringBuilder sb = new StringBuilder();

        switch (comando) {
            case "Mkdir":
                sb.append(cmdMkdir(argumentos));
                break;
            case "Rm":
                sb.append(cmdRm(argumentos));
                break;
            case "Cd":
                sb.append(cmdCd(argumentos));
                break;
            case "Cd..":
                sb.append(cmdCd(".."));
                break;
            case "Dir":
                sb.append(cmdDir());
                break;
            case "Date":
                sb.append(cmdDate());
                break;
            case "Time":
                sb.append(cmdTime());
                break;
            default:
                sb.append("Comando no reconocido.\n");
        }

        return sb.toString();
    }

    public String cmdMkdir(String nombre) {

        if (nombre.isEmpty()) {
            return "Uso: Mkdir <nombre>\n";
        }

        File nueva = new File(currentDir, nombre);

        if (nueva.exists()) {
            return "La carpeta ya existe.\n";
        }

        if (nueva.mkdir()) {
            return "Carpeta creada: " + nueva.getName() + "\n";
        } else {
            return "No se pudo crear la carpeta.\n";
        }
    }

    public boolean borrarRecursivo(File f) {

        if (f.isDirectory()) {
            File[] hijos = f.listFiles();
            if (hijos != null) {
                for (File h : hijos) {
                    borrarRecursivo(h);
                }
            }
        }
        return f.delete();
    }

    public String cmdRm(String nombre) {

        if (nombre.isEmpty()) {
            return "Uso: Rm <archivo/carpeta>\n";
        }

        File objetivo = new File(currentDir, nombre);

        if (!objetivo.exists()) {
            return "No existe el archivo/carpeta.\n";
        }

        boolean ok = borrarRecursivo(objetivo);

        if (ok) {
            return "Eliminado: " + nombre + "\n";
        } else {
            return "No se pudo eliminar: " + nombre + "\n";
        }
    }

    public String cmdCd(String argumento) {

        if (argumento.isEmpty()) {
            return "Uso: Cd <carpeta> o Cd..\n";
        }

        if (argumento.equals("..")) {
            File padre = currentDir.getParentFile();
            File catosRoot = new File(FileSystemManager.ROOT_DRIVE);

            if (padre != null && padre.equals(catosRoot)) {

                User currentUser = SessionManager.getCurrentUser();
                
                if (currentUser != null && !currentUser.isAdmin()) {
                    return "Permisos insuficientes para acceder al directorio raíz (Z:/).\n";
                }
            }

            if (padre != null && padre.exists()) {
                currentDir = padre;
                return "";
            } else {
                return "No hay carpeta padre válida.\n";
            }
        }

        if (argumento.contains("..")) {
            return "Comando no reconocido.\n";
        }
        
        File nueva = new File(currentDir, argumento);
        if (nueva.exists() && nueva.isDirectory()) {
            currentDir = nueva;
            return "";
        } else {
            return "La carpeta no existe: " + argumento + "\n";
        }
    }

    public String cmdDir() {
        StringBuilder sb = new StringBuilder();
        File[] archivos = currentDir.listFiles();
        if (archivos == null || archivos.length == 0) {
            sb.append("La carpeta está vacía.\n");
            return sb.toString();
        }

        sb.append("Contenido de ").append(currentDir.getAbsolutePath()).append(":\n");
        for (File f : archivos) {
            if (f.isDirectory()) {
                sb.append("<DIR>  ").append(f.getName()).append("\n");
            } else {
                sb.append("       ").append(f.getName()).append("\n");
            }
        }
        return sb.toString();
    }

    public String cmdDate() {
        LocalDate hoy = LocalDate.now();
        return "Fecha actual: " + hoy.toString() + "\n";
    }

    public String cmdTime() {
        LocalTime ahora = LocalTime.now();
        String hora = ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        return "Hora actual: " + hora + "\n";
    }
    
}
