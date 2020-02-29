package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBColor;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.UI;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanelWithEmptyText;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.components.BorderLayoutPanel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class WorkflowsTabContentPanel extends JPanel {

    private JPanel stub;
    private JBPanelWithEmptyText contentContainer;

    public WorkflowsTabContentPanel(@NotNull JComponent workflowsToolbar, boolean isAuthorized) {
        super(new BorderLayout());

//        add(workflowsToolbar, BorderLayout.WEST);
    }

    private Component createContent() {
        BorderLayoutPanel left = new BorderLayoutPanel();
        BorderLayoutPanel center = new BorderLayoutPanel();
        BorderLayoutPanel right = new BorderLayoutPanel();

        OnePixelSplitter splitter = new OnePixelSplitter("Github.PullRequests.Component", 0.33f);
        splitter.setBackground(UIUtil.getListBackground());
        splitter.setOpaque(true);
        splitter.setFocusCycleRoot(true);
        splitter.setFirstComponent(left);

        OnePixelSplitter rightSplitter = new OnePixelSplitter("Github.PullRequests.Component", 0.5f);
        rightSplitter.setFirstComponent(center);
        rightSplitter.setSecondComponent(right);
        splitter.setSecondComponent(rightSplitter);

        return splitter;
    }

    public void redraw(boolean isAuthorized) {
        if (isAuthorized) {
            removeStub();
            createTable();
        } else {
        }
        revalidate();
        repaint();
    }

    private void createTable() {
        if (isNull(contentContainer)) {
            contentContainer = new JBPanelWithEmptyText(null);
            contentContainer.setBackground(UIUtil.getListBackground());
            contentContainer.setLayout(new BorderLayout());
            contentContainer.add(createContent(), BorderLayout.CENTER);
            contentContainer.validate();
            contentContainer.repaint();
            add(contentContainer, BorderLayout.CENTER);
        }
    }

    private void removeStub() {
        if (nonNull(stub)) {
            remove(stub);
            stub = null;
        }
    }
}
