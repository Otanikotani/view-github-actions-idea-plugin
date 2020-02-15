package org.github.otanikotani.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RefreshAction extends AnAction {

  private Runnable refreshChecksPanel;

  public RefreshAction(@NotNull Runnable refreshChecksPanel) {
    super("Refresh Checks", "Refreshes checks", AllIcons.Actions.Refresh);
    this.refreshChecksPanel = refreshChecksPanel;
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    refreshChecksPanel.run();
  }
}
