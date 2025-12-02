/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox.core;

/**
 *
 * @author ashley
 */

import litterbox.core.FileInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

import java.awt.event.MouseAdapter; 
import java.awt.event.MouseEvent;

public class SearchResultsDialog extends JDialog {

    public SearchResultsDialog(Frame owner, List<FileInfo> results) {
        super(owner, "Search Results", true);
        
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        setLayout(new BorderLayout());
        
        JLabel infoLabel = new JLabel("Found " + results.size() + " item(s). Double-click to view path.", SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(infoLabel, BorderLayout.NORTH);

        String[] columnNames = {"Name", "Type", "Path"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable resultsTable = new JTable(model);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowSelectionAllowed(true);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        for (FileInfo fi : results) {
            File f = fi.getFile();
            String type = f.isDirectory() ? "Folder" : "File";
            String path = f.getAbsolutePath();

            model.addRow(new Object[]{f.getName(), type, path});
        }
        
        resultsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultsTable.getSelectedRow();
                    if (row != -1) {
                        String name = (String) model.getValueAt(row, 0);
                        String path = (String) model.getValueAt(row, 2);
                        JOptionPane.showMessageDialog(SearchResultsDialog.this,
                                "File: " + name + "\nPath: " + path,
                                "Path Detail", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        
        JPanel southPanel = new JPanel();
        southPanel.add(closeButton);
        add(southPanel, BorderLayout.SOUTH);
    }
}
