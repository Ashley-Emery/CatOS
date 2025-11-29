/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox;

/**
 *
 * @author ashley
 */

import litterbox.core.FileInfo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class FileTableModel extends AbstractTableModel {

    private final String[] columns = {"Name", "Modified", "Type", "Size"};
    private final List<FileInfo> data = new ArrayList<>();

    public void setFiles(List<FileInfo> files) {
        data.clear();
        if (files != null) data.addAll(files);
        fireTableDataChanged();
    }

    public FileInfo getFileInfoAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        FileInfo fi = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> fi.getDisplayName();
            case 1 -> fi.getFormattedModified();
            case 2 -> fi.getTypeText();
            case 3 -> fi.getFormattedSize();
            default -> "";
        };
    }
}