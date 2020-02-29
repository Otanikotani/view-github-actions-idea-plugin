package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.util.Objects;

public class WorkflowsToolbar extends JPanel {

    public WorkflowsToolbar(@NotNull ActionManager actionManager, ActionGroup workflowsActionGroup) {
        super(new BorderLayout());
        ActionToolbar actionToolbar = Objects.requireNonNull(actionManager)
            .createActionToolbar(ActionPlaces.TOOLBAR, workflowsActionGroup, false);
        add(actionToolbar.getComponent(), BorderLayout.PAGE_START);
    }
}
