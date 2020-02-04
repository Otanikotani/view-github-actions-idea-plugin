package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task.Backgroundable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import one.util.streamex.StreamEx;
import org.github.otanikotani.api.CheckSuites;
import org.github.otanikotani.api.GithubCheckRun;
import org.github.otanikotani.api.GithubCheckRuns;
import org.github.otanikotani.api.GithubCheckSuites;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequest.Get.Json;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor.WithTokenAuth;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

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

    GithubAuthenticationManager authManager = GithubAuthenticationManager.getInstance();
    if (!authManager.ensureHasAccounts(repository.getProject())) {
      return;
    }

    GithubAccount account = authManager.getSingleOrDefaultAccount(repository.getProject());
    if (account == null) {
      return;
    }

    GithubApiRequestExecutorManager executorManager = GithubApiRequestExecutorManager.getInstance();
    WithTokenAuth executor = executorManager.getExecutor(account, repository.getProject());
    if (executor == null) {
      return;
    }

    new Backgroundable(project, "Getting Check Suites...") {

      private List<? extends GithubCheckRun> checkRuns;

      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        String remoteUrl = StreamEx.of(repository.getRemotes()).map(GitRemote::getFirstUrl)
          .findFirst()
          .orElseThrow(() -> new RuntimeException("Failed to find a remote url"));

        String[] parts = remoteUrl.split("/");
        String repo = parts[parts.length - 1];
        String owner = parts[parts.length - 2];

        GithubApiRequest<GithubCheckSuites> request = new CheckSuites()
          .get(account.getServer(), owner, repo, repository.getCurrentBranchName());
        GithubCheckSuites suites;
        try {
          suites = executor.execute(indicator, request);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }

        checkRuns = StreamEx.of(suites.getCheck_suites())
          .flatMap(it -> {
            GithubApiRequest<GithubCheckRuns> checkRunsRequest = new Json<>(
              it.getCheck_runs_url(),
              GithubCheckRuns.class,
              "application/vnd.github.antiope-preview+json")
              .withOperationName("Get Check Runs...");
            try {
              return executor.execute(indicator, checkRunsRequest).getCheck_runs().stream();
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          })
          .toList();
      }

      @Override
      public void onSuccess() {
        checksPanel.addRows(checkRuns);
      }
    }.queue();
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
