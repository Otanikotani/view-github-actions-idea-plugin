package org.github.otanikotani.ui.action;

import static java.util.Objects.isNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import java.util.List;
import one.util.streamex.StreamEx;
import org.github.otanikotani.dto.RepoDetails;
import org.github.otanikotani.repository.CheckRun;
import org.github.otanikotani.repository.CheckSuiteRepository;
import org.github.otanikotani.ui.toolwindow.ChecksToolWindowContent;
import org.jetbrains.annotations.NotNull;

public class OpenMainPanelAction extends AnAction {

  private final CheckSuiteRepository checkSuiteRepository;

  OpenMainPanelAction() {
    checkSuiteRepository = new CheckSuiteRepository(RepoDetails.token);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final Project project = e.getProject();
    if (isNull(project)) {
      return;
    }
    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
    ToolWindow checksToolWindow = toolWindowManager.getToolWindow("checksToolWindow");
    if (!checksToolWindow.isVisible()) {
      checksToolWindow.show(null);
    }
    ContentManager contentManager = checksToolWindow.getContentManager();
    if (contentManager.getContentCount() == 0) {
      return;
    }
    Content content = contentManager.getContent(0);
    if (isNull(content)) {
      return;
    }
    ChecksToolWindowContent checksContent = (ChecksToolWindowContent) content.getComponent();
    checksContent.removeAllRows();

    checkSuiteRepository.getCheckSuites(RepoDetails.owner, RepoDetails.repo, RepoDetails.branch)
      .thenAccept(checkSuites -> {
        List<CheckRun> checkRuns = StreamEx.of(checkSuites.getCheckSuites()).flatMap(it -> it.getCheckRuns().stream())
          .sorted((l, r) -> r.getStarted_at().compareTo(l.getStarted_at()))
          .toList();
        checksContent.addRows(checkRuns);
      });
  }
}
