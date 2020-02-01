package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import git4idea.repo.GitRepository;
import one.util.streamex.StreamEx;
import org.github.otanikotani.dto.RepoDetails;
import org.github.otanikotani.repository.CheckRun;
import org.github.otanikotani.repository.CheckSuiteRepository;

import java.util.List;

public class GHChecksToolWindowTabsContentManager {

  private static final String CONTENT_TAB_NAME = "Checks";

  private final Project project;
  private final ChangesViewContentI viewContentManager;

  GHChecksToolWindowTabsContentManager(Project project, ChangesViewContentI viewContentManager) {
    this.project = project;
    this.viewContentManager = viewContentManager;
  }

  private Content createContent(GitRepository repository) {
    ChecksPanel checksPanel = new ChecksPanel();
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(checksPanel, CONTENT_TAB_NAME, false);
    content.setDescription("GitHub Checks for your builds");
    content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY,
            ChangesViewContentManager.TabOrderWeight.OTHER.getWeight());

    updateChecksPanel(checksPanel, repository);

    return content;
  }

  private void updateChecksPanel(ChecksPanel checksPanel, GitRepository repository) {
    checksPanel.removeAllRows();

    CheckSuiteRepository checkSuiteRepository = new CheckSuiteRepository(RepoDetails.token);
    String branchName = repository.getCurrentBranch().getName();
    checksPanel.setBranch(branchName);
    checkSuiteRepository.getCheckSuites(RepoDetails.owner, RepoDetails.repo, branchName)
            .thenAccept(checkSuites -> {
              List<CheckRun> checkRuns = StreamEx.of(checkSuites.getCheckSuites())
                      .flatMap(it -> it.getCheckRuns().stream())
                      .sorted((l, r) -> r.getStarted_at().compareTo(l.getStarted_at()))
                      .toList();
              checksPanel.addRows(checkRuns);
            });
  }

  private void updateContent(GitRepository repository) {
    final List<Content> contents = viewContentManager.findContents(content ->
            CONTENT_TAB_NAME.equalsIgnoreCase(content.getTabName())
    );
    if (contents.isEmpty()) {
      viewContentManager.addContent(createContent(repository));
    } else {
      ChecksPanel checksPanel = (ChecksPanel) contents.get(0).getComponent();
      updateChecksPanel(checksPanel, repository);
    }
  }

  public void update(GitRepository repository) {
    Application app = ApplicationManager.getApplication();
    if (app.isDispatchThread()) {
      updateContent(repository);
    } else {
      app.invokeLater(() -> updateContent(repository));
    }
  }
}
