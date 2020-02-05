package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
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
import one.util.streamex.StreamEx;
import org.github.otanikotani.action.RefreshAction;
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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class GHChecksToolWindowTabsContentManager {

  private static final String CONTENT_TAB_NAME = "Checks";
  private static final String REFRESH_ACTION_ID = "GHChecks.Action.Refresh";
  private static final String GHCHECKS_ACTION_GROUP_ID = "GHChecks.ActionGroup";
  private static final AnActionEvent EMPTY_ACTION_EVENT = new AnActionEvent(null, dataId -> null,
          ActionPlaces.UNKNOWN, new Presentation(), ActionManager.getInstance(), 0);

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
    JPanel mainPanel = new JPanel(new BorderLayout());

    ChecksPanel checksPanel = new ChecksPanel();
    mainPanel.add(checksPanel, BorderLayout.CENTER);

    JPanel toolbarPanel = createToolbar(repository, checksPanel);
    mainPanel.add(toolbarPanel, BorderLayout.WEST);

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(mainPanel, CONTENT_TAB_NAME, false);
    content.setDescription("GitHub Checks for your builds");
    content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY,
            ChangesViewContentManager.TabOrderWeight.OTHER.getWeight());

    updateChecksPanel(checksPanel, repository);

    return content;
  }

  private JPanel createToolbar(GitRepository repository, ChecksPanel checksPanel) {
    JPanel toolbarPanel = new JPanel(new BorderLayout());
    Runnable refreshChecksPanelRunnable = () -> updateChecksPanel(checksPanel, repository);
    ActionManager actionManager = ActionManager.getInstance();
    actionManager.registerAction(
            REFRESH_ACTION_ID,
            new RefreshAction(refreshChecksPanelRunnable)
    );
    DefaultActionGroup checksActionGroup = (DefaultActionGroup) actionManager.getAction(GHCHECKS_ACTION_GROUP_ID);
    checksActionGroup.add(actionManager.getAction(REFRESH_ACTION_ID));

    toolbarPanel.add(
            actionManager.createActionToolbar(ActionPlaces.TOOLBAR, checksActionGroup, false).getComponent(),
            BorderLayout.PAGE_START
    );
    return toolbarPanel;
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
      ActionManager.getInstance().getAction(REFRESH_ACTION_ID).actionPerformed(EMPTY_ACTION_EVENT);
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
