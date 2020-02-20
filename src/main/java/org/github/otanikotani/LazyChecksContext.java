package org.github.otanikotani;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.github.otanikotani.ui.toolwindow.ChecksRefresher;
import org.github.otanikotani.ui.toolwindow.ChecksToolWindowTabsContentManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;

@Service
public final class LazyChecksContext {

    private ChecksToolWindowTabsContentManager contentManager;
    private final Project project;
    private ScheduledExecutorService appScheduledExecutorService;
    private ChecksRefresher refresher;

    public LazyChecksContext(Project project) {
        this.project = project;
    }

    private void update(GitRepository repository) {
        ChecksToolWindowTabsContentManager contentManager = getContentManager(repository);
        contentManager.onRepositoryChanged(repository);
        getChecksRefresher(project, contentManager);
    }

    private void getChecksRefresher(Project project,
        ChecksToolWindowTabsContentManager contentManager) {
        if (null == refresher) {
            MessageBusConnection bus = project.getMessageBus().connect();
            refresher = new ChecksRefresher(bus, contentManager);
            refresher.everyMinutes(getAppScheduledExecutorService(), 1);
        }
    }

    private ChecksToolWindowTabsContentManager getContentManager(GitRepository repository) {
        if (null == contentManager) {
            contentManager = new ChecksToolWindowTabsContentManager(repository);
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
