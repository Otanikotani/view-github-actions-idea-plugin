package org.github.otanikotani.ui.toolwindow;

import com.intellij.ui.table.JBTable;
import lombok.Getter;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

@Getter
public class ChecksToolWindowContent extends JPanel {

    private ChecksResultTableModel tableModel;

    public ChecksToolWindowContent() {
        super(new GridLayout(1, 0));
        tableModel = new ChecksResultTableModel();
        final JBTable table = new JBTable(tableModel);
        table.setRowSelectionAllowed(false);
        setLayout(new BorderLayout());
        add(table.getTableHeader(), BorderLayout.PAGE_START);
        add(table, BorderLayout.CENTER);
    }

    public void addRow(String name, String status, String startedAt) {
        tableModel.addRow(name, status, startedAt);
        revalidate();
        repaint();
    }

    public void removeAllRows() {
        tableModel.removeAllRows();
    }
}
