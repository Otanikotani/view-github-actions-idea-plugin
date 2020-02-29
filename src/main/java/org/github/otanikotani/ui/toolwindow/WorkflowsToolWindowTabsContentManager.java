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

public class WorkflowsToolWindowTabsContentManager implements WorkflowsListener {

    static final String REFRESH_ACTION_ID = "GHWorkflows.Action.Refresh";
    static final String GHWORKFLOWS_ACTION_GROUP_ID = "GHWorkflows.ActionGroup";

    private static final String CONTENT_TAB_NAME = "Workflows";
    WorkflowsLocation location;
    private WorkflowsTabContentPanel workflowsTabContentPanel;

    void updateWorkflows() {
        Application app = ApplicationManager.getApplication();
        if (app.isDispatchThread()) {
            updateWorkflowsContentPanel();
        } else {
            app.invokeLater(this::updateWorkflowsContentPanel);
        }
    }

    private void updateWorkflowsContentPanel() {
        GitRepository repo = location.repository;

        if (isNull(workflowsTabContentPanel)) {
            createWorkflowsTabContentPanel(repo.getProject());
        }
        boolean isAuthorized = isAuthorized();
        workflowsTabContentPanel.redraw(isAuthorized);
        if (!isAuthorized) {
            return;
        }

        GithubApiRequestExecutorManager requestExecutorManager = GithubApiRequestExecutorManager.getInstance();
        ofNullable(requestExecutorManager.getExecutor(location.account, repo.getProject()))
            .map(executor -> new GettingWorkflowRuns(location, executor))
            .ifPresent(Task::queue);
    }

    private void createWorkflowsTabContentPanel(Project project) {
        JPanel toolbar = ofNullable(ActionManager.getInstance())
            .map(this::createToolbar)
            .orElseGet(this::createEmptyToolbar);

        workflowsTabContentPanel = new WorkflowsTabContentPanel(toolbar, isAuthorized());

        ContentFactory contentFactory = SERVICE.getInstance();
        Content content = contentFactory.createContent(workflowsTabContentPanel, CONTENT_TAB_NAME, false);
        content.setCloseable(false);
        content.setDescription("GitHub Workflows for your builds");
        content.putUserData(ChangesViewContentManager.ORDER_WEIGHT_KEY,
            ChangesViewContentManager.TabOrderWeight.OTHER.getWeight());
        ChangesViewContentManager.getInstance(project).addContent(content);
    }

    private boolean isAuthorized() {
        return nonNull(location.account);
    }

    @NotNull
    private JPanel createToolbar(@NotNull ActionManager actionManager) {
        AnAction actionGroup = actionManager.getAction(GHWORKFLOWS_ACTION_GROUP_ID);
        if (actionManager.isGroup(GHWORKFLOWS_ACTION_GROUP_ID) && actionGroup != null) {
            AnAction refreshAction = actionManager.getAction(REFRESH_ACTION_ID);
            if (refreshAction == null) {
                refreshAction = new RefreshAction(this::updateWorkflows);
                actionManager.registerAction(REFRESH_ACTION_ID, refreshAction);
            }

            DefaultActionGroup workflowsActionGroup = (DefaultActionGroup) actionGroup;
            workflowsActionGroup.add(refreshAction);
            return new WorkflowsToolbar(actionManager, workflowsActionGroup);
        } else {
            return createEmptyToolbar();
        }
    }

    @NotNull
    private JPanel createEmptyToolbar() {
        return new JPanel(new BorderLayout());
    }

    @Override
    public void onLocationChange(WorkflowsLocation location) {
        this.location = location;
        updateWorkflows();
    }
}
