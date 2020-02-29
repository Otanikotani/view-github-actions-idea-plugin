package org.github.otanikotani.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefreshAction extends AnAction {

    private Runnable refreshWorkflowsTable;

    public RefreshAction(@NotNull Runnable refreshWorkflowsTable) {
        super("Refresh Workflows", "Refreshes workflows", AllIcons.Actions.Refresh);
        this.refreshWorkflowsTable = refreshWorkflowsTable;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        refreshWorkflowsTable.run();
    }
}
