package org.github.otanikotani.ui.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.LayoutManager;

public class ChecksDialogWrapper extends DialogWrapper {
    private static final String DIALOG_TITLE = "Checks";

    private JLabel name;
    private JLabel status;
    private JLabel startedAt;

    public ChecksDialogWrapper() {
        super(true);
        init();
        setTitle(DIALOG_TITLE);
        setResizable(false);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        LayoutManager mainLayout = new GridLayout(2, 3);
        JComponent mainPanel = new JPanel(mainLayout);

        JComponent nameLabel = new JLabel("Name");
        mainPanel.add(nameLabel);
        JComponent statusLabel = new JLabel("Status");
        mainPanel.add(statusLabel);
        JComponent startedAtLabel = new JLabel("Started At");
        mainPanel.add(startedAtLabel);

        name = new JLabel();
        mainPanel.add(name);
        status = new JLabel();
        mainPanel.add(status);
        startedAt = new JLabel();
        mainPanel.add(startedAt);
        return mainPanel;
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setStatus(String status) {
        this.status.setText(status);
    }

    public void setStartedAt(String startedAt) {
        this.startedAt.setText(startedAt);
    }
}
