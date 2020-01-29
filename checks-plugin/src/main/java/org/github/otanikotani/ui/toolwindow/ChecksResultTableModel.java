package org.github.otanikotani.ui.toolwindow;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ChecksResultTableModel extends DefaultTableModel {

    private static final List<String> COLUMN_NAMES = Arrays.asList("Name", "Status", "Started At");

    @SuppressWarnings("unchecked")
    public ChecksResultTableModel() {
        columnIdentifiers.addAll(COLUMN_NAMES);
    }

    public void addRow(String name, String status, String startedAt) {
        addRow(new Vector<>(Arrays.asList(name, status, startedAt)));
    }

    public void removeAllRows() {
        while (getRowCount() > 0) {
            removeRow(getRowCount() - 1);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
