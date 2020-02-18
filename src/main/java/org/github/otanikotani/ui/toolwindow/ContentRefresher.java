package org.github.otanikotani.ui.toolwindow;

import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EventListener;
import java.util.concurrent.TimeUnit;

public class ContentRefresher {

    private static final int DEFAULT_REFRESH_DELAY = 1;
    private LocalDateTime lastRefreshTime = LocalDateTime.now();

    private final Runnable handler;

    ContentRefresher(@NotNull Runnable handler) {
        this.handler = handler;
    }

    public void everyMinutes(int minutes) {
        AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(
            () -> {
                Duration duration = Duration.between(LocalDateTime.now(), lastRefreshTime);
                if (Math.abs(duration.toMinutes()) >= DEFAULT_REFRESH_DELAY) {
                    handler.run();
                    refreshed();
                }
            },
            minutes,
            minutes,
            TimeUnit.MINUTES
        );
    }

    void refreshed() {
        lastRefreshTime = LocalDateTime.now();
    }

    interface ChecksRefreshedListener extends EventListener {

        Topic<ChecksRefreshedListener> CHECKS_REFRESHED = Topic
            .create("GitHub checks refreshed", ChecksRefreshedListener.class);

        void checksRefreshed();
    }
}
