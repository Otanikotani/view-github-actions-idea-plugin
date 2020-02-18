package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor.WithTokenAuth;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.AccountTokenChangedListener;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class GHChecksToolWindowTabsContentManager {

    public static final Topic<AccountTokenChangedListener> ACCOUNT_CHANGED_TOPIC = new Topic<>(
        "GITHUB_ACCOUNT_TOKEN_CHANGED",
        AccountTokenChangedListener.class);
    private static final String CONTENT_TAB_NAME = "Checks";
    private static final int DEFAULT_REFRESH_DELAY = 1;
    private final ChangesViewContentI viewContentManager;
    private Project project;
    private GitRepository repository;
    private GithubAccount account;
    private LocalDateTime lastRefreshTime;
    private ChecksTabContentPanel checksTabContentPanel;

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

    private void startScheduler() {
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
            () -> {
                Duration duration = Duration.between(LocalDateTime.now(), lastRefreshTime);
                if (Math.abs(duration.toMinutes()) >= DEFAULT_REFRESH_DELAY) {
                    update();
                }
            },
            DEFAULT_REFRESH_DELAY,
            DEFAULT_REFRESH_DELAY,
            TimeUnit.MINUTES
        );
    }

    void update() {
        Application app = ApplicationManager.getApplication();
        if (app.isDispatchThread()) {
            updateChecksTabContentPanel();
        } else {
            app.invokeLater(this::updateChecksTabContentPanel);
        }
    }

    private void updateChecksTabContentPanel() {
        if (isNull(checksTabContentPanel)) {
            createChecksTabContentPanel();
            startScheduler();
        }
        boolean isAuthorized = nonNull(getAccount());
        checksTabContentPanel.redraw(isAuthorized);
        if (!isAuthorized) {
            return;
        }
        GithubApiRequestExecutorManager executorManager = GithubApiRequestExecutorManager.getInstance();
        WithTokenAuth executor = executorManager.getExecutor(account, repository.getProject());
        if (executor == null) {
            return;
        }

        new GettingCheckSuites(project, checksTabContentPanel.getTable(), repository, getAccount(), executor).queue();
    }

    private void createChecksTabContentPanel() {
        checksTabContentPanel = new ChecksTabContentPanel(ActionManager.getInstance(),
            this::update, nonNull(getAccount()));
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(checksTabContentPanel, CONTENT_TAB_NAME, false);
        content.setCloseable(false);
        content.setDescription("GitHub Checks for your builds");
        content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY,
            ChangesViewContentManager.TabOrderWeight.OTHER.getWeight());
        viewContentManager.addContent(content);
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

    void setRepository(GitRepository repository) {
        this.repository = repository;
    }
}
