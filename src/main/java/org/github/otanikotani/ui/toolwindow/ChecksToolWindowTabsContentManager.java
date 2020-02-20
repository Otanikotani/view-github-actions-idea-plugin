package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactory.SERVICE;
import git4idea.repo.GitRepository;
import javax.swing.JPanel;
import org.github.otanikotani.action.RefreshAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;

import java.awt.BorderLayout;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class ChecksToolWindowTabsContentManager implements ChecksListener {

    static final String REFRESH_ACTION_ID = "GHChecks.Action.Refresh";
    static final String GHCHECKS_ACTION_GROUP_ID = "GHChecks.ActionGroup";

    private static final String CONTENT_TAB_NAME = "Checks";
    ChecksLocation location;
    private ChecksTabContentPanel checksTabContentPanel;

    void update() {
        Application app = ApplicationManager.getApplication();
        if (app.isDispatchThread()) {
            updateChecksTabContentPanel();
        } else {
            app.invokeLater(this::updateChecksTabContentPanel);
        }
    }

    private void updateChecksTabContentPanel() {
        GitRepository repo = location.repository;

        if (isNull(checksTabContentPanel)) {
            createChecksTabContentPanel(repo.getProject());
        }
        boolean isAuthorized = isAuthorized();
        checksTabContentPanel.redraw(isAuthorized);
        if (!isAuthorized) {
            return;
        }

        GithubApiRequestExecutorManager requestExecutorManager = GithubApiRequestExecutorManager.getInstance();
        ofNullable(requestExecutorManager.getExecutor(location.account, repo.getProject()))
            .map(executor -> new GettingCheckSuites(repo.getProject(), checksTabContentPanel.getTable(),
                repo,
                location.account,
                executor))
            .ifPresent(Task::queue);
    }

    private void createChecksTabContentPanel(Project project) {
        JPanel toolbar = ofNullable(ActionManager.getInstance())
            .map(this::createToolbar)
            .orElseGet(this::createEmptyToolbar);

        checksTabContentPanel = new ChecksTabContentPanel(toolbar, isAuthorized());

        ContentFactory contentFactory = SERVICE.getInstance();
        Content content = contentFactory.createContent(checksTabContentPanel, CONTENT_TAB_NAME, false);
        content.setCloseable(false);
        content.setDescription("GitHub Checks for your builds");
        content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY,
            ChangesViewContentManager.TabOrderWeight.OTHER.getWeight());
        ChangesViewContentManager.getInstance(project).addContent(content);
    }

    private boolean isAuthorized() {
        return nonNull(location.account);
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

    @Override
    public void onRefresh(ChecksLocation location) {
        this.location = location;
        update();
    }
}
