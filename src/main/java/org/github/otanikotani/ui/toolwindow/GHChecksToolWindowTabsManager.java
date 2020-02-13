package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;

@Service
public final class GHChecksToolWindowTabsManager {

  private final GHChecksToolWindowTabsContentManager contentManager;

  public GHChecksToolWindowTabsManager(Project project) {
    contentManager = new GHChecksToolWindowTabsContentManager(
      project);
  }

  //Kicks of
  static class ChangeListener implements GitRepositoryChangeListener {

    private final Project project;

    ChangeListener(Project project) {
      this.project = project;
    }

    @Override
    public void repositoryChanged(@NotNull GitRepository repository) {
      GHChecksToolWindowTabsManager service = project.getService(GHChecksToolWindowTabsManager.class);
      service.update(repository);
    }
  }

  private void update(GitRepository repository) {
    contentManager.setRepository(repository);
    contentManager.update();
  }
}
