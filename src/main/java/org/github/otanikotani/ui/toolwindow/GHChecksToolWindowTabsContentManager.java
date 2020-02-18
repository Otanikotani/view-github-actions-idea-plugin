package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import git4idea.repo.GitRepository;
import javax.swing.JPanel;
import org.github.otanikotani.action.RefreshAction;
import org.github.otanikotani.ui.toolwindow.ContentRefresher.ChecksRefreshedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor.WithTokenAuth;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.AccountTokenChangedListener;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.awt.BorderLayout;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class GHChecksToolWindowTabsContentManager {

    public static final Topic<AccountTokenChangedListener> ACCOUNT_CHANGED_TOPIC = new Topic<>(
        "GITHUB_ACCOUNT_TOKEN_CHANGED",
        AccountTokenChangedListener.class);

    static final String REFRESH_ACTION_ID = "GHChecks.Action.Refresh";
    static final String GHCHECKS_ACTION_GROUP_ID = "GHChecks.ActionGroup";

    private static final String CONTENT_TAB_NAME = "Checks";

    private final ChangesViewContentI viewContentManager;
    private final ActionManager actionManager;
    private Project project;
    private GitRepository repository;
    private GithubAccount account;
    private ContentRefresher contentRefresher;


    private ChecksTabContentPanel checksTabContentPanel;

    GHChecksToolWindowTabsContentManager(Project project, ChangesViewContentI viewContentManager,
        ActionManager actionManager) {
        this.project = project;
        this.viewContentManager = viewContentManager;
        this.actionManager = actionManager;
        contentRefresher = new ContentRefresher(this::update);

        MessageBusConnection bus = project.getMessageBus().connect();
        bus.subscribe(ACCOUNT_CHANGED_TOPIC, githubAccount -> {
            this.account = githubAccount;
            update();
        });
        bus.subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, new BranchChangeListener() {
            @Override
            public void branchWillChange(@NotNull String branchName) {

            }

            @Override
            public void branchHasChanged(@NotNull String branchName) {
                update();
            }
        });
        bus.subscribe(ChecksRefreshedListener.CHECKS_REFRESHED, contentRefresher::refreshed);
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
            contentRefresher.everyMinutes(1);
        }
        boolean isAuthorized = isAuthorized();
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
        contentRefresher.refreshed();
    }

    private void createChecksTabContentPanel() {
        JPanel toolbar;
        if (null == actionManager) {
            toolbar = createEmptyToolbar();
        } else {
            toolbar = createToolbar(actionManager);
        }
        checksTabContentPanel = new ChecksTabContentPanel(toolbar, isAuthorized());

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(checksTabContentPanel, CONTENT_TAB_NAME, false);
        content.setCloseable(false);
        content.setDescription("GitHub Checks for your builds");
        content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY,
            ChangesViewContentManager.TabOrderWeight.OTHER.getWeight());
        viewContentManager.addContent(content);
    }

    private boolean isAuthorized() {
        return nonNull(getAccount());
    }

    @NotNull
    private JPanel createToolbar(@NotNull ActionManager actionManager) {
        AnAction actionGroup = actionManager.getAction(GHCHECKS_ACTION_GROUP_ID);
        if (actionManager.isGroup(GHCHECKS_ACTION_GROUP_ID) && actionGroup != null) {
            AnAction refreshAction = actionManager.getAction(REFRESH_ACTION_ID);
            if (refreshAction == null) {
                refreshAction = new RefreshAction(this::update);
                actionManager.registerAction(REFRESH_ACTION_ID, refreshAction);
            }

            DefaultActionGroup checksActionGroup = (DefaultActionGroup) actionGroup;
            checksActionGroup.add(refreshAction);
            return new ChecksToolbar(actionManager, checksActionGroup);
        } else {
            return createEmptyToolbar();
        }
    }

    @NotNull
    private JPanel createEmptyToolbar() {
        return new JPanel(new BorderLayout());
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
