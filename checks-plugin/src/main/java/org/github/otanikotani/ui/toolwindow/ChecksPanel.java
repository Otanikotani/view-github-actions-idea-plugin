package org.github.otanikotani.ui.toolwindow;

import com.intellij.ui.table.JBTable;
import java.util.List;
import javax.swing.JLabel;
import lombok.Getter;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import org.github.otanikotani.repository.CheckRun;
import org.ocpsoft.prettytime.PrettyTime;

@Getter
public class ChecksPanel extends JPanel {

    private ChecksTableModel tableModel;
    private JLabel testLabel;

    public ChecksPanel() {
        super(new GridLayout(1, 0));
        tableModel = new ChecksTableModel();
        final JBTable table = new JBTable(tableModel);
        table.setRowSelectionAllowed(false);
        setLayout(new BorderLayout());
        add(table.getTableHeader(), BorderLayout.PAGE_START);
        add(table, BorderLayout.CENTER);
        testLabel = new JLabel("{{branch-name}}");
        add(testLabel, BorderLayout.PAGE_END);
    }

    public void addRow(String name, String status, String startedAt) {
        tableModel.addRow(name, status, startedAt);
        revalidate();
        repaint();
    }

    public void setBranch(String branchName) {
        testLabel.setText(branchName);
        revalidate();
        repaint();
    }

    public void removeAllRows() {
        tableModel.removeAllRows();
    }

    public void addRows(List<CheckRun> checkRuns) {
        PrettyTime prettyTime = new PrettyTime();
        for (CheckRun checkRun : checkRuns) {
            String name = checkRun.getName();
            String status = statusToIcons(checkRun.getStatus());
            String startedAt = prettyTime.format(checkRun.getStarted_at());
            tableModel.addRow(name, status, startedAt);
        }
    }

    private String statusToIcons(String status) {
        if (status.equals("in_progress")) {
            return "⌛";
        } else if (status.equals("completed")) {
            return "✓";
        } else {
            return "❌";
        }
    }
}
