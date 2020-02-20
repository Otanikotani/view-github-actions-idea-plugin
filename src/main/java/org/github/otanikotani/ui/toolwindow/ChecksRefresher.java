package org.github.otanikotani.ui.toolwindow;

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

public class ChecksRefresher {

    public static final Topic<AccountTokenChangedListener> ACCOUNT_CHANGED_TOPIC = new Topic<>(
        "GITHUB_ACCOUNT_TOKEN_CHANGED",
        AccountTokenChangedListener.class);

    private static final int DEFAULT_REFRESH_DELAY = 1;

    @NotNull
    private final ChecksListener checksListener;

    private LocalDateTime lastRefreshTime = LocalDateTime.now();
    private ChecksLocation lastLocation;

    public ChecksRefresher(@NotNull ChecksListener checksListener, ChecksLocation location) {
        this.checksListener = checksListener;
        this.lastLocation = location;
    }

    public void everyMinutes(ScheduledExecutorService scheduledExecutorService, int minutes) {
        scheduledExecutorService.scheduleWithFixedDelay(
            () -> {
                Duration duration = Duration.between(LocalDateTime.now(), lastRefreshTime);
                if (Math.abs(duration.toMinutes()) >= DEFAULT_REFRESH_DELAY) {
                    checksListener.onRefresh(lastLocation);
                    refresh();
                }
            },
            minutes,
            minutes,
            TimeUnit.MINUTES
        );
    }

    void refresh() {
        checksListener.onRefresh(lastLocation);
        lastRefreshTime = LocalDateTime.now();
    }

    public void useLocation(ChecksLocation location) {
        this.lastLocation = location;
        MessageBusConnection bus = location.repository.getProject().getMessageBus().connect();
        bus.subscribe(ACCOUNT_CHANGED_TOPIC, githubAccount -> {
            lastLocation = new ChecksLocation(lastLocation.repository, githubAccount);
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
        bus.subscribe(ChecksRefreshedListener.CHECKS_REFRESHED, () -> lastRefreshTime = LocalDateTime.now());
    }

    interface ChecksRefreshedListener extends EventListener {

        Topic<ChecksRefreshedListener> CHECKS_REFRESHED = Topic
            .create("GitHub checks refreshed", ChecksRefreshedListener.class);

        void checksRefreshed();
    }
}
