/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox;

/**
 *
 * @author ashley
 */

import litterbox.core.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LitterBoxFrame extends JFrame {

    public static final String CARD_HOME   = "HOME";
    public static final String CARD_FOLDER = "FOLDER";
    public static final String CARD_TRASH  = "TRASH";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardsPanel = new JPanel(cardLayout);

    private final HomeViewPanel   homeViewPanel;
    private final FolderViewPanel folderViewPanel;
    private final TrashViewPanel  trashViewPanel;

    private final PathUtils     pathUtils;
    private final FileManager   fileManager;
    private final TrashManager  trashManager;
    private final Organizer     organizer;
    private final HistoryManager historyManager;

    public LitterBoxFrame() {
        super("Litter Box");

        pathUtils = new PathUtils();
        fileManager = new FileManager(pathUtils);
        trashManager = new TrashManager(pathUtils);
        organizer = new Organizer(pathUtils, fileManager, trashManager);
        historyManager = new HistoryManager();

        homeViewPanel = new HomeViewPanel(this);
        folderViewPanel = new FolderViewPanel(this);
        trashViewPanel = new TrashViewPanel(this);

        cardsPanel.add(homeViewPanel, CARD_HOME);
        cardsPanel.add(folderViewPanel, CARD_FOLDER);
        cardsPanel.add(trashViewPanel, CARD_TRASH);

        setContentPane(cardsPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        showHome();

        historyManager.clear();
        historyManager.pushLocation(new HistoryLocation(HistoryLocation.Type.HOME, null, pathUtils.getAdminRoot()));
    }

    public PathUtils getPathUtils() {
        return pathUtils;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public TrashManager getTrashManager() {
        return trashManager;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
    
    public void showHome() {
        homeViewPanel.refreshAll();
        cardLayout.show(cardsPanel, CARD_HOME);
    }

    public void showFolder(File folder, File rootForDropdown) {
        folderViewPanel.setFolder(folder, rootForDropdown);
        cardLayout.show(cardsPanel, CARD_FOLDER);
    }

    public void showTrash() {
        trashViewPanel.refresh();
        cardLayout.show(cardsPanel, CARD_TRASH);
    }

    public void navigateToHomeFromUI() {
        historyManager.pushLocation(new HistoryLocation(HistoryLocation.Type.HOME, null, pathUtils.getAdminRoot()));
        showHome();
    }

    public void navigateToFolderFromUI(File folder, File rootForDropdown) {
        historyManager.pushLocation(new HistoryLocation(HistoryLocation.Type.FOLDER, rootForDropdown, folder));
        showFolder(folder, rootForDropdown);
    }

    public void navigateToTrashFromUI() {
        historyManager.pushLocation(new HistoryLocation(HistoryLocation.Type.TRASH, null, pathUtils.getTrashDir()));
        showTrash();
    }

    public void goBack() {
        HistoryLocation loc = historyManager.goBack();
        if (loc != null) {
            openFromHistory(loc);
        }
        homeViewPanel.updateHistoryButtons();
    }

    public void goForward() {
        HistoryLocation loc = historyManager.goForward();
        if (loc != null) {
            openFromHistory(loc);
        }
        homeViewPanel.updateHistoryButtons();
    }

    private void openFromHistory(HistoryLocation loc) {
        if (loc == null) return;

        switch (loc.getType()) {
            case HOME -> showHome();
            case FOLDER -> showFolder(loc.getCurrentFolder(), loc.getRootFolder());
            case TRASH -> showTrash();
        }
    }
    
}