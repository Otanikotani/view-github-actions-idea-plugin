package org.github.otanikotani.ui.toolwindow;

import com.intellij.ui.components.labels.LinkLabel;
import javax.swing.JTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class ColumnLinkLabelMouseAdapter extends MouseAdapter {

    private final JTable table;
    private final int columnIndex;

    ColumnLinkLabelMouseAdapter(JTable table, int columnIndex) {
        this.table = table;
        this.columnIndex = columnIndex;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        if (row >= 0 && col == columnIndex) {
            table.clearSelection();
            Object value = table.getValueAt(row, col);
            if (value instanceof LinkLabel<?>) {
                ((LinkLabel<?>) value).doClick();
            }
        }
    }
}
