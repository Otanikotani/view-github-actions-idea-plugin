package org.github.otanikotani.ui.listener;

import static java.util.Objects.isNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import java.awt.Window;
import org.github.otanikotani.dto.RepoDetails;
import org.github.otanikotani.ui.toolwindow.ChecksToolWindowContent;
import org.jetbrains.annotations.NotNull;

public class BranchChangeListener implements com.intellij.openapi.vcs.BranchChangeListener {

  @Override
  public void branchWillChange(@NotNull String branchName) {

  }

  @Override
  public void branchHasChanged(@NotNull String branchName) {
    RepoDetails.branch = branchName;
    Project project = getCurrentActiveProject();

    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
    ToolWindow checksToolWindow = toolWindowManager.getToolWindow("checksToolWindow");
    ContentManager contentManager = checksToolWindow.getContentManager();
    if (contentManager.getContentCount() == 0) {
      return;
    }
    Content content = contentManager.getContent(0);
    if (isNull(content)) {
      return;
    }
    ChecksToolWindowContent checksContent = (ChecksToolWindowContent) content.getComponent();
    checksContent.setBranch(RepoDetails.branch);
  }

  private Project getCurrentActiveProject() {
    Project[] projects = ProjectManager.getInstance().getOpenProjects();
    Project activeProject = null;
    for (Project project : projects) {
      Window window = WindowManager.getInstance().suggestParentWindow(project);
      if (window != null && window.isActive()) {
        activeProject = project;
      }
    }
    return activeProject;
  }
}
