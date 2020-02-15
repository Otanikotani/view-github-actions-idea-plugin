package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import git4idea.repo.GitRepository;
import java.awt.BorderLayout;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import org.github.otanikotani.action.RefreshAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor.WithTokenAuth;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.AccountTokenChangedListener;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

class GHChecksToolWindowTabsContentManager {

  public static final Topic<AccountTokenChangedListener> ACCOUNT_CHANGED_TOPIC = new Topic<>(
    "GITHUB_ACCOUNT_TOKEN_CHANGED",
    AccountTokenChangedListener.class);
  private static final String CONTENT_TAB_NAME = "Checks";
  private static final String REFRESH_ACTION_ID = "GHChecks.Action.Refresh";
  private static final String GHCHECKS_ACTION_GROUP_ID = "GHChecks.ActionGroup";
  private static final int DEFAULT_REFRESH_DELAY = 1;
  private final ChangesViewContentI viewContentManager;
  Project project;
  GitRepository repository;
  GithubAccount account;
  LocalDateTime lastRefreshTime;
  private AnActionEvent emptyEvent;

  GHChecksToolWindowTabsContentManager(Project project, ChangesViewContentI viewContentManager) {
    this.project = project;
    this.viewContentManager = viewContentManager;
    this.lastRefreshTime = LocalDateTime.now();

    MessageBusConnection bus = project.getMessageBus().connect();
    bus.subscribe(ACCOUNT_CHANGED_TOPIC, githubAccount -> this.account = githubAccount);
    bus.subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, new BranchChangeListener() {
      @Override
      public void branchWillChange(@NotNull String branchName) {

      }

      @Override
      public void branchHasChanged(@NotNull String branchName) {
        update();
      }
    });
    bus.subscribe(ChecksRefreshedListener.CHECKS_REFRESHED, () -> lastRefreshTime = LocalDateTime.now());
  }

  private Content createContent(GitRepository repository) {
    JPanel mainPanel = new JPanel(new BorderLayout());

    ChecksPanel checksPanel = new ChecksPanel(new ChecksTableModel());
    mainPanel.add(checksPanel, BorderLayout.CENTER);

    JPanel toolbarPanel = createToolbar(() -> updateChecksPanel(checksPanel, repository));
    mainPanel.add(toolbarPanel, BorderLayout.WEST);

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(mainPanel, CONTENT_TAB_NAME, false);
    content.setCloseable(false);
    content.setDescription("GitHub Checks for your builds");
    content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY,
      ChangesViewContentManager.TabOrderWeight.OTHER.getWeight());

    updateChecksPanel(checksPanel, repository);

    AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
      () -> {
        Duration duration = Duration.between(LocalDateTime.now(), lastRefreshTime);
        if (Math.abs(duration.toMinutes()) >= DEFAULT_REFRESH_DELAY) {
          updateChecksPanel(checksPanel, repository);
        }
      },
      DEFAULT_REFRESH_DELAY,
      DEFAULT_REFRESH_DELAY,
      TimeUnit.MINUTES
    );

    return content;
  }

  private JPanel createToolbar(Runnable refreshChecksPanel) {
    JPanel toolbarPanel = new JPanel(new BorderLayout());
    ActionManager actionManager = ActionManager.getInstance();
    actionManager.registerAction(
      REFRESH_ACTION_ID,
      new RefreshAction(refreshChecksPanel)
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

    new GettingCheckSuites(project, checksPanel, repository, getAccount(), executor).queue();
  }

  void update() {
    Application app = ApplicationManager.getApplication();
    if (app.isDispatchThread()) {
      updateContent();
    } else {
      app.invokeLater(this::updateContent);
    }
  }

  void setRepository(GitRepository repository) {
    this.repository = repository;
  }

  private GithubAccount getAccount() {
    if (account == null) {
      GithubAuthenticationManager authManager = GithubAuthenticationManager.getInstance();
      if (!authManager.hasAccounts()) {
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
      ActionManager.getInstance().getAction(REFRESH_ACTION_ID).actionPerformed(getEmptyActionEvent());
    }
  }

  private AnActionEvent getEmptyActionEvent() {
    if (null == emptyEvent) {
      emptyEvent = new AnActionEvent(null, dataId -> null,
        ActionPlaces.UNKNOWN, new Presentation(), ActionManager.getInstance(), 0);
    }
    return emptyEvent;
  }
}
