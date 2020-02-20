package org.github.otanikotani;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentI;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentFactory.SERVICE;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.github.otanikotani.ui.toolwindow.ChecksRefresher;
import org.github.otanikotani.ui.toolwindow.ChecksToolWindowTabsContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager;
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager;
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

@Service
public final class LazyChecksContext implements ChecksContext {

    private ChecksToolWindowTabsContentManager contentManager;
    private final Project project;
    private ChangesViewContentI changesViewContentManager;
    private ActionManager actionManager;
    private ScheduledExecutorService appScheduledExecutorService;
    private ChecksRefresher refresher;
    private Application application;
    private GithubApiRequestExecutorManager githubApiRequestExecutorManager;
    private GithubAccountManager githubAuthenticationManager;
    private ContentFactory contentFactory;

    public LazyChecksContext(Project project) {
        this.project = project;
    }

    private void update(GitRepository repository) {
        ChecksToolWindowTabsContentManager contentManager = getContentManager(repository);
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
            contentManager = new ChecksToolWindowTabsContentManager(this, repository);
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

    @Override
    @Nullable
    public Project getProject() {
        return project;
    }

    @Override
    public Optional<ActionManager> getActionManager() {
        return Optional.ofNullable(getActionManagerInternal());
    }

    private ActionManager getActionManagerInternal() {
        if (actionManager == null) {
            actionManager = ActionManager.getInstance();
        }
        return actionManager;
    }

    @NotNull
    @Override
    public ChangesViewContentI getChangesViewContentManager() {
        if (null == changesViewContentManager) {
            changesViewContentManager = ChangesViewContentManager.getInstance(project);
        }
        return changesViewContentManager;
    }

    @NotNull
    @Override
    public Application getApplication() {
        if (null == application) {
            application = ApplicationManager.getApplication();
        }
        return application;
    }

    private GithubApiRequestExecutorManager getGithubApiRequestExecutorManager() {
        if (null == githubApiRequestExecutorManager) {
            githubApiRequestExecutorManager = GithubApiRequestExecutorManager.getInstance();
        }
        return githubApiRequestExecutorManager;
    }


    @Override
    public GithubAccountManager getGithubAccountManager() {
        if (null == githubAuthenticationManager) {
            githubAuthenticationManager = new JetbrainsGithubAccountManager(GithubAuthenticationManager.getInstance());
        }
        return githubAuthenticationManager;
    }

    @Override
    public ContentFactory getContentFactory() {
        if (null == contentFactory) {
            contentFactory = SERVICE.getInstance();
        }
        return contentFactory;
    }

    @Override
    public Optional<GithubApiRequestExecutor> getGithubApiExecutor(
        GithubAccount account,
        Project project) {
        return Optional.ofNullable(getGithubApiRequestExecutorManager())
            .map(it -> it.getExecutor(account, project))
            .map(GithubApiRequestExecutorWrapper::new);
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
