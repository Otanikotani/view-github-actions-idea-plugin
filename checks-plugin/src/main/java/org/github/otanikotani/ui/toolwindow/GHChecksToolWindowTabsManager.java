package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

@Service
public final class GHChecksToolWindowTabsManager {

  private final Project project;
  private final GHChecksToolWindowTabsContentManager contentManager;

  public GHChecksToolWindowTabsManager(Project project) {
    this.project = project;
    contentManager = new GHChecksToolWindowTabsContentManager(project, ChangesViewContentManager.getInstance(project));
  }

  private void update(GitRepository repository) {
    contentManager.update(repository);
  }

  //Kicks of
  static class ChangeListener implements GitRepositoryChangeListener {

    private final Project project;

    ChangeListener(Project project) {
      this.project = project;
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
      project.getService(GHChecksToolWindowTabsManager.class)
        .update(repository);
    }
  }
}
