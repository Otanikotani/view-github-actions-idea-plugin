package org.github.otanikotani;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.plugins.github.api.GithubApiRequest;

import java.io.IOException;

public interface GithubApiRequestExecutor {

    <T> T execute(ProgressIndicator indicator, GithubApiRequest<T> request) throws IOException;

    void queue(Task task);
}
