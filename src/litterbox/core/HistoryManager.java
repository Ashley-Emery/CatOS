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
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {

    private final List<HistoryLocation> history = new ArrayList<>();
    private int currentIndex = -1;

    public void clear() {
        history.clear();
        currentIndex = -1;
    }

    public void pushLocation(HistoryLocation loc) {
        
        while (history.size() > currentIndex + 1) {
            history.remove(history.size() - 1);
        }
        history.add(loc);
        currentIndex = history.size() - 1;
    }

    public HistoryLocation goBack() {
        if (currentIndex <= 0) return null;
        currentIndex--;
        return history.get(currentIndex);
    }

    public HistoryLocation goForward() {
        if (currentIndex < 0 || currentIndex >= history.size() - 1) return null;
        currentIndex++;
        return history.get(currentIndex);
    }
    
    public boolean canGoBack() {
        return currentIndex > 0;
    }
    
    public boolean canGoForward() {
        return currentIndex < history.size() - 1;
    }
}


