package org.github.otanikotani;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBusConnection;
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

    private void update(GitRepository repository) {
        ChecksLocation location = new ChecksLocation(repository, getAccount());
        ChecksToolWindowTabsContentManager contentManager = getContentManager();
        contentManager.onRefresh(location);
        getChecksRefresher(location, contentManager);
    }

    private void getChecksRefresher(ChecksLocation location,
        ChecksToolWindowTabsContentManager contentManager) {
        if (null == refresher) {
            MessageBusConnection bus = location.repository.getProject().getMessageBus().connect();
            refresher = new ChecksRefresher(bus, contentManager, location);
            refresher.everyMinutes(getAppScheduledExecutorService(), 1);
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
            service.update(repository);
        }
    }
}
