package org.github.otanikotani;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.github.otanikotani.ui.toolwindow.WorkflowsLocation;
import org.github.otanikotani.ui.toolwindow.WorkflowsRefresher;
import org.github.otanikotani.ui.toolwindow.WorkflowsToolWindowTabsContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.util.concurrent.ScheduledExecutorService;

@Service
public final class WorkflowsContext {

    private final Project project;
    private WorkflowsToolWindowTabsContentManager contentManager;
    private ScheduledExecutorService appScheduledExecutorService;
    private WorkflowsRefresher refresher;

    public WorkflowsContext(Project project) {
        this.project = project;
    }

    private void onRepositoryChange(GitRepository repository) {
        WorkflowsLocation location = new WorkflowsLocation(repository, getAccount());
        WorkflowsToolWindowTabsContentManager contentManager = getContentManager();
        contentManager.onLocationChange(location);
        getOrSetupRefresher(contentManager, location);
    }

    private void getOrSetupRefresher(WorkflowsToolWindowTabsContentManager contentManager,
        WorkflowsLocation location) {
        if (null == refresher) {
            refresher = new WorkflowsRefresher(contentManager, location);
            refresher.everyMinutes(getAppScheduledExecutorService(), 1);
        } else {
            refresher.useLocation(location);
        }
    }

    private GithubAccount getAccount() {
        GithubAuthenticationManager manager = GithubAuthenticationManager.getInstance();
        if (manager.hasAccounts()) {
            return manager.getSingleOrDefaultAccount(project);
        }
        return null;
    }

    private WorkflowsToolWindowTabsContentManager getContentManager() {
        if (null == contentManager) {
            contentManager = new WorkflowsToolWindowTabsContentManager();
        }
        return contentManager;
    }

    @NotNull
    private ScheduledExecutorService getAppScheduledExecutorService() {
        if (null == appScheduledExecutorService) {
            appScheduledExecutorService = AppExecutorUtil.getAppScheduledExecutorService();
        }
        return appScheduledExecutorService;
    }

    //Kicks of the plugin. This is called whenever IDEA starts
    static class ChangeListener implements GitRepositoryChangeListener {

        private final Project project;

        ChangeListener(Project project) {
            this.project = project;
        }

        @Override
        public void repositoryChanged(@NotNull GitRepository repository) {
            WorkflowsContext service = project.getService(WorkflowsContext.class);
            service.onRepositoryChange(repository);
        }
    }
}
