package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.ui.JBColor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ChecksTabContentPanel extends JPanel {

    private ChecksTable table;
    private JPanel stub;
    private ChecksToolbar toolbar;

    public ChecksTabContentPanel(ActionManager actionManager, Runnable refreshChecksTable, boolean isAuthorized) {
        super(new BorderLayout());
        redraw(isAuthorized);
        createToolbar(actionManager, refreshChecksTable);
    }

    public void redraw(boolean isAuthorized) {
        if (isAuthorized) {
            removeStub();
            createTable();
        } else {
            removeTable();
            createStub();
        }
        revalidate();
        repaint();
    }

    private void createTable() {
        if (isNull(table)) {
            table = new ChecksTable(new ChecksTableModel());
            add(table, BorderLayout.CENTER);
        }
    }

    private void removeTable() {
        if (nonNull(table)) {
            remove(table);
            table = null;
        }
    }

    private void createToolbar(ActionManager actionManager, Runnable refreshChecksTable) {
        toolbar = new ChecksToolbar(actionManager, refreshChecksTable);
        add(toolbar, BorderLayout.WEST);
    }

    private void createStub() {
        if (isNull(stub)) {
            stub = new JPanel(new GridLayout(1, 0));
            stub.setBackground(JBColor.WHITE);
            JLabel message = new JLabel(
                "Log in to GitHub. To add account go to Idea Settings -> Version Control -> GitHub");
            message.setHorizontalAlignment(SwingConstants.CENTER);
            message.setForeground(JBColor.GRAY);
            stub.add(message);
            add(stub, BorderLayout.CENTER);
        }
    }

    private void removeStub() {
        if (nonNull(stub)) {
            remove(stub);
            stub = null;
        }
    }

    public ChecksTable getTable() {
        return table;
    }

    public JPanel getStub() {
        return stub;
    }

    public ChecksToolbar getToolbar() {
        return toolbar;
    }
}
