package org.github.otanikotani.ui.toolwindow;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task.Backgroundable;
import git4idea.repo.GitRemote;
import one.util.streamex.StreamEx;
import org.github.otanikotani.api.GithubWorkflowRun;
import org.github.otanikotani.api.GithubWorkflowRuns;
import org.github.otanikotani.api.Workflows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor;
import org.jetbrains.plugins.github.api.GithubServerPath;
import org.jetbrains.plugins.github.exceptions.GithubStatusCodeException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class GettingWorkflowRuns extends Backgroundable {

    private static final Pattern DOT_GIT_PATTERN = Pattern.compile("\\.git$");

    private final WorkflowsLocation location;
    private final GithubApiRequestExecutor.WithTokenAuth executor;
    private String owner;
    private String repo;
    private List<GithubWorkflowRun> workflowRuns;

    GettingWorkflowRuns(WorkflowsLocation location,
        GithubApiRequestExecutor.WithTokenAuth executor) {
        super(location.repository.getProject(), "Getting Workflow Runs...");
        this.location = location;
        this.executor = executor;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        String remoteUrl = StreamEx.of(location.repository.getRemotes())
            .map(GitRemote::getFirstUrl)
            .findFirst()
            .map(url -> DOT_GIT_PATTERN.matcher(url).replaceFirst(""))
            .orElseThrow(() -> new RuntimeException("Failed to find a remote url"));

        String[] parts = remoteUrl.split("/");
        repo = parts[parts.length - 1];
        owner = parts[parts.length - 2];

        GithubServerPath server = location.account.getServer();
        String branchName = location.repository.getCurrentBranchName();
        GithubApiRequest<GithubWorkflowRuns> apiRequest = new Workflows()
            .getWorkflowRunsByBranch(server, owner, repo, branchName);

        try {
            workflowRuns = executor.execute(indicator, apiRequest).getWorkflow_runs();
        } catch (GithubStatusCodeException e) {
            if (e.getStatusCode() == 404) {
                workflowRuns = Collections.emptyList();
            } else {
                throw new UncheckedIOException("Unexpected failure", e);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unexpected failure", e);
        }
    }

    @Override
    public void onSuccess() {
//        WorkflowsRefreshedListener publisher = myProject.getMessageBus()
//            .syncPublisher(WorkflowsRefreshedListener.WORKFLOWS_REFRESHED);
//        publisher.workflowsRefreshed();
    }
}
