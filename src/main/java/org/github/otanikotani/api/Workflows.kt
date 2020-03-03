package org.github.otanikotani.api

import org.github.otanikotani.workflow.GitHubRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiRequests
import org.jetbrains.plugins.github.api.GithubServerPath
import org.jetbrains.plugins.github.api.data.GithubResponsePage
import org.jetbrains.plugins.github.api.data.request.GithubRequestPagination
import org.jetbrains.plugins.github.api.util.GithubApiPagesLoader
import org.jetbrains.plugins.github.api.util.GithubApiSearchQueryBuilder
import org.jetbrains.plugins.github.api.util.GithubApiUrlQueryBuilder

data class GitHubWorkflows(
    val total_count: Int,
    val workflows: List<GitHubWorkflow> = emptyList()
)

data class GitHubWorkflow(
    val id: Long,
    val node_id: String,
    val name: String,
    val path: String,
    val state: String,
    val completed_at: String?,
    val updated_at: String?,
    val url: String,
    val html_url: String,
    val badge_url: String)

data class GitHubWorkflowRuns(
    val total_count: Int,
    val workflow_runs: List<GitHubWorkflowRun> = emptyList()
)

data class GitHubWorkflowRun(
    val id: Long,
    val node_id: String,
    val head_branch: String,
    val head_sha: String,
    val run_number: Int,
    val event: String,
    val status: String,
    val conclusion: String,
    val url: String,
    val html_url: String,
    val created_at: String?,
    val updated_at: String?,
    val jobs_url: String,
    val logs_url: String,
    val check_suite_url: String,
    val artifacts_url: String,
    val cancel_url: String,
    val rerun_url: String,
    val workflow_url: String
)


object Workflows : GithubApiRequests.Entity("/repos") {
    @JvmStatic
    fun getWorkflows(coordinates: GitHubRepositoryCoordinates): GithubApiRequest<GitHubWorkflows> {
        val serverUrl = coordinates.serverPath.toApiUrl()
        val repoPath = coordinates.repositoryPath
        val url = String.format("%s%s/%s/%s/actions/workflows",
            serverUrl, urlSuffix, repoPath.owner, repoPath.repository)
        return GithubApiRequest.Get.Json(url, GitHubWorkflows::class.java, null)
            .withOperationName("get workflows")
    }

    @JvmStatic
    fun getWorkflowRuns(coordinates: GitHubRepositoryCoordinates,
            event: String? = null,
            status: String? = null,
            branch: String? = null,
            actor: String? = null,
            pagination: GithubRequestPagination? = null): GithubApiRequest<GitHubWorkflowRuns> {
        val url = GithubApiRequests.getUrl(coordinates.serverPath,
            urlSuffix,
            "/" + coordinates.repositoryPath.toString(),
            "/actions",
            "/runs",
            GithubApiUrlQueryBuilder.urlQuery {
                param("q", GithubApiSearchQueryBuilder.searchQuery {
                    qualifier("event", event)
                    qualifier("status", status)
                    qualifier("branch", branch)
                    qualifier("actor", actor)
                })
                param(pagination)
            })
        return get(url)
    }

    @JvmStatic
    fun get(url: String) = GithubApiRequest.Get.json<GitHubWorkflowRuns>(url)
        .withOperationName("search workflow runs")
}
