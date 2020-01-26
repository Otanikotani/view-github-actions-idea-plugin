package org.github.otanikotani.ui.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;

public class ChecksDialogWrapper extends DialogWrapper {
    private static final String DIALOG_TITLE = "Checks";

    private JComponent mainPanel;

    public ChecksDialogWrapper() {
        super(true);
        init();
        setTitle(DIALOG_TITLE);
        setResizable(false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        GridLayout mainLayout = new GridLayout(1, 3);
        mainLayout.setHgap(10);
        mainLayout.setVgap(5);
        mainPanel = new JPanel(mainLayout);

        JLabel nameLabel = new JLabel("Name");
        mainPanel.add(nameLabel);
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel);
        JLabel startedAtLabel = new JLabel("Started At");
        mainPanel.add(startedAtLabel);

        return mainPanel;
    }

    public void addRow(String name, String status, String startedAt) {
        GridLayout mainLayout = (GridLayout) mainPanel.getLayout();
        mainLayout.setRows(mainLayout.getRows() + 1);
        mainLayout.setColumns(3);

        mainPanel.add(new JLabel(name));
        JLabel statusLabel = new JLabel(status);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel);
        mainPanel.add(new JLabel(startedAt));
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
