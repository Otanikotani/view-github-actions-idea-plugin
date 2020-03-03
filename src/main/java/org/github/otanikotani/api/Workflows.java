package org.github.otanikotani.api;

import org.github.otanikotani.workflow.GitHubRepositoryCoordinates;
import org.jetbrains.plugins.github.api.GHRepositoryPath;
import org.jetbrains.plugins.github.api.GithubApiRequest;
import org.jetbrains.plugins.github.api.GithubApiRequest.Get;
import org.jetbrains.plugins.github.api.GithubApiRequests.Entity;
import org.jetbrains.plugins.github.api.GithubServerPath;
import org.jetbrains.plugins.github.api.util.GithubApiUrlQueryBuilder;

public class Workflows extends Entity {

    public Workflows() {
        super("/repos");
    }

    public GithubApiRequest<GithubWorkflows> getWorkflows(GitHubRepositoryCoordinates gitHubRepositoryCoordinates) {
        String serverUrl = gitHubRepositoryCoordinates.getServerPath().toApiUrl();
        GHRepositoryPath repoPath = gitHubRepositoryCoordinates.getRepositoryPath();
        String url = String.format("%s%s/%s/%s/actions/workflows",
            serverUrl, getUrlSuffix(), repoPath.getOwner(), repoPath.getRepository());
        return new Get.Json<>(url, GithubWorkflows.class, null)
            .withOperationName("get workflows");
    }

    public GithubApiRequest<GithubWorkflowRuns> getWorkflowRunsByBranch(GithubServerPath server,
        String owner, String repo, String branch) {
        String query = GithubApiUrlQueryBuilder.urlQuery(builder -> {
            builder.param("branch", branch);
            return null;
        });
        String url = String.format("%s%s/%s/%s/actions/runs%s",
            server.toApiUrl(), getUrlSuffix(), owner, repo, query);
        return new Get.Json<>(url, GithubWorkflowRuns.class,
            null)
            .withOperationName("get workflow runs for " + branch);
    }
}
