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
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import one.util.streamex.StreamEx;
import org.github.otanikotani.api.CheckRuns;
import org.github.otanikotani.api.CheckSuites;
import org.github.otanikotani.api.GithubCheckRun;
import org.github.otanikotani.api.GithubCheckRuns;
import org.github.otanikotani.api.GithubCheckSuites;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor.WithTokenAuth;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.AccountTokenChangedListener;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

public class GHChecksToolWindowTabsContentManager {

  private static final String CONTENT_TAB_NAME = "Checks";

  public static final Topic<AccountTokenChangedListener> ACCOUNT_CHANGED_TOPIC = new Topic<>(
    "GITHUB_ACCOUNT_TOKEN_CHANGED",
    AccountTokenChangedListener.class);

  Project project;
  GitRepository repository;
  GithubAccount account;

  private final ChangesViewContentI viewContentManager;

  public GHChecksToolWindowTabsContentManager(Project project) {
    this.project = project;
    this.viewContentManager = ChangesViewContentManager.getInstance(project);

    MessageBusConnection bus = project.getMessageBus().connect();
    bus.subscribe(ACCOUNT_CHANGED_TOPIC, githubAccount -> this.account = githubAccount);
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

    GithubAccount account = getAccount();
    if (account == null) {
      //TODO: Show login in UI
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

            GithubApiRequest<GithubCheckRuns> checkRunsRequest = new CheckRuns().get(it.getCheck_runs_url());
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

      @Override
      public void onThrowable(@NotNull Throwable error) {
        super.onThrowable(error);
        //TODO: Show something about the failure in UI
      }
    }.queue();
  }

  private GithubAccount getAccount() {
    if (account == null) {
      GithubAuthenticationManager authManager = GithubAuthenticationManager.getInstance();
      if (!authManager.ensureHasAccounts(repository.getProject())) {
        return null;
      }
      GithubAccount account = authManager.getSingleOrDefaultAccount(repository.getProject());
      if (account == null) {
        return null;
      }
      this.account = account;
    }
    return this.account;
  }

  private void updateContent() {
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

  public void update() {
    Application app = ApplicationManager.getApplication();
    if (app.isDispatchThread()) {
      updateContent();
    } else {
      app.invokeLater(this::updateContent);
    }
  }

  public void setRepository(GitRepository repository) {
    this.repository = repository;
  }
}
