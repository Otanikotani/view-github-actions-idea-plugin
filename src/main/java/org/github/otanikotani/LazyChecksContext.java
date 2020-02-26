package org.github.otanikotani;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.github.otanikotani.ui.toolwindow.ChecksLocation;
import org.github.otanikotani.ui.toolwindow.ChecksRefresher;
import org.github.otanikotani.ui.toolwindow.ChecksToolWindowTabsContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.util.concurrent.ScheduledExecutorService;

@Service
public final class LazyChecksContext {

    private final Project project;
    private ChecksToolWindowTabsContentManager contentManager;
    private ScheduledExecutorService appScheduledExecutorService;
    private ChecksRefresher refresher;

    public LazyChecksContext(Project project) {
        this.project = project;
    }

    private void onRepositoryChange(GitRepository repository) {
        ChecksLocation location = new ChecksLocation(repository, getAccount());
        ChecksToolWindowTabsContentManager contentManager = getContentManager();
        contentManager.onRefresh(location);
        getOrSetupRefresher(contentManager, location);
    }

    private void getOrSetupRefresher(ChecksToolWindowTabsContentManager contentManager,
        ChecksLocation location) {
        if (null == refresher) {
            refresher = new ChecksRefresher(contentManager, location);
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

    private ChecksToolWindowTabsContentManager getContentManager() {
        if (null == contentManager) {
            contentManager = new ChecksToolWindowTabsContentManager();
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
            LazyChecksContext service = project.getService(LazyChecksContext.class);
            service.onRepositoryChange(repository);
        }
    }
}
