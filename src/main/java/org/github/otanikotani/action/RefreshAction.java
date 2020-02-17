package org.github.otanikotani.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefreshAction extends AnAction {

    private Runnable refreshChecksTable;

    public RefreshAction(@NotNull Runnable refreshChecksTable) {
        super("Refresh Checks", "Refreshes checks", AllIcons.Actions.Refresh);
        this.refreshChecksTable = refreshChecksTable;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        refreshChecksTable.run();
    }
}
