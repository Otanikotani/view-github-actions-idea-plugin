package org.github.otanikotani.ui.toolwindow;

import lombok.Getter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ChecksToolWindowContent extends JPanel {
    private static final GridLayout MAIN_LAYOUT = new GridLayout(1, 3);

    public ChecksToolWindowContent() {
        super(MAIN_LAYOUT);
        MAIN_LAYOUT.setHgap(10);
        MAIN_LAYOUT.setVgap(5);

        JLabel nameLabel = new JLabel("Name");
        add(nameLabel);
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel);
        JLabel startedAtLabel = new JLabel("Started At");
        add(startedAtLabel);
    }

    public void addRow(String name, String status, String startedAt) {
        MAIN_LAYOUT.setRows(MAIN_LAYOUT.getRows() + 1);
        MAIN_LAYOUT.setColumns(3);

        add(new JLabel(name));
        JLabel statusLabel = new JLabel(status);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel);
        add(new JLabel(startedAt));

        revalidate();
        repaint();
    }

    public void removeAllRows() {
        List<Component> toDelete = new ArrayList<>();
        for (int i = 1; i < MAIN_LAYOUT.getRows(); i++) {
            toDelete.add(getComponent(i * MAIN_LAYOUT.getColumns()));
            toDelete.add(getComponent(i * MAIN_LAYOUT.getColumns() + 1));
            toDelete.add(getComponent(i * MAIN_LAYOUT.getColumns() + 2));
        }
        toDelete.forEach(this::remove);

        MAIN_LAYOUT.setRows(1);
        MAIN_LAYOUT.setColumns(3);
        revalidate();
        repaint();
    }
}
