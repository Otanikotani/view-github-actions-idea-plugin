package org.github.otanikotani;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor.WithTokenAuth;

import java.io.IOException;

public class GithubApiRequestExecutorWrapper implements GithubApiRequestExecutor {

    private final WithTokenAuth executor;

    public GithubApiRequestExecutorWrapper(WithTokenAuth executor) {
        this.executor = executor;
    }

    @Override
    public <T> T execute(ProgressIndicator indicator,
        GithubApiRequest<T> request) throws IOException {
        return executor.execute(indicator, request);
    }

    @Override
    public void queue(@NotNull Task task) {
        task.queue();
    }
}
