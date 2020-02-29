package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.BranchChangeListener;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.authentication.accounts.AccountTokenChangedListener;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EventListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorkflowsRefresher {

    public static final Topic<AccountTokenChangedListener> ACCOUNT_CHANGED_TOPIC = new Topic<>(
        "GITHUB_ACCOUNT_TOKEN_CHANGED",
        AccountTokenChangedListener.class);

    private static final int DEFAULT_REFRESH_DELAY = 1;

    @NotNull
    private final WorkflowsListener workflowsListener;

    private LocalDateTime lastRefreshTime = LocalDateTime.now();
    private WorkflowsLocation lastLocation;
    private Project project;
    private boolean subscribed;

    public WorkflowsRefresher(@NotNull WorkflowsListener workflowsListener, WorkflowsLocation location) {
        this.workflowsListener = workflowsListener;
        this.lastLocation = location;
        this.project = location.repository.getProject();
        subscribe(location);
    }

    public void everyMinutes(ScheduledExecutorService scheduledExecutorService, int minutes) {
        scheduledExecutorService.scheduleWithFixedDelay(
            () -> {
                Duration duration = Duration.between(LocalDateTime.now(), lastRefreshTime);
                if (Math.abs(duration.toMinutes()) >= DEFAULT_REFRESH_DELAY) {
                    workflowsListener.onLocationChange(lastLocation);
                    refresh();
                }
            },
            minutes,
            minutes,
            TimeUnit.MINUTES
        );
    }

    void refresh() {
        workflowsListener.onLocationChange(lastLocation);
        lastRefreshTime = LocalDateTime.now();
    }

    public void useLocation(WorkflowsLocation location) {
        this.lastLocation = location;
        subscribe(location);
    }

    private void subscribe(WorkflowsLocation location) {
        if (!subscribed) {
            MessageBusConnection bus = project.getMessageBus().connect();
            subscribeToMessages(bus);
            subscribed = true;
        } else if (!project.equals(location.repository.getProject())) {
            this.project = location.repository.getProject();
            MessageBusConnection bus = project.getMessageBus().connect();
            subscribeToMessages(bus);
        }
    }

    private void subscribeToMessages(MessageBusConnection bus) {
        bus.subscribe(ACCOUNT_CHANGED_TOPIC, githubAccount -> {
            lastLocation = new WorkflowsLocation(lastLocation.repository, githubAccount);
            refresh();
        });
        bus.subscribe(BranchChangeListener.VCS_BRANCH_CHANGED, new BranchChangeListener() {
            @Override
            public void branchWillChange(@NotNull String branchName) {

            }

            @Override
            public void branchHasChanged(@NotNull String branchName) {
                refresh();
            }
        });
        bus.subscribe(WorkflowsRefreshedListener.WORKFLOWS_REFRESHED, () -> lastRefreshTime = LocalDateTime.now());
    }

    interface WorkflowsRefreshedListener extends EventListener {

        Topic<WorkflowsRefreshedListener> WORKFLOWS_REFRESHED = Topic
            .create("GitHub workflows refreshed", WorkflowsRefreshedListener.class);

        void workflowsRefreshed();
    }
}
