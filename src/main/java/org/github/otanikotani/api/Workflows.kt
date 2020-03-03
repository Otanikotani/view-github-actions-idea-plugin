package org.github.otanikotani.api

import org.github.otanikotani.workflow.GitHubRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiRequests
import org.jetbrains.plugins.github.api.GithubServerPath
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
    var id: Long,
    var node_id: String,
    var head_branch: String,
    var head_sha: String,
    var run_number: Int,
    var event: String,
    var status: String,
    var conclusion: String,
    var url: String,
    var html_url: String,
    var created_at: String?,
    var updated_at: String?,
    var jobs_url: String,
    var logs_url: String,
    var check_suite_url: String,
    var artifacts_url: String,
    var cancel_url: String,
    var rerun_url: String,
    var workflow_url: String
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
                        event: String?,
                        status: String?,
                        branch: String?,
                        actor: String?) =
        GithubApiPagesLoader.Request(get(coordinates, event, status, branch, actor), ::get)

    @JvmStatic
    fun get(coordinates: GitHubRepositoryCoordinates,
            event: String?,
            status: String?,
            branch: String?,
            actor: String?,
            pagination: GithubRequestPagination? = null) =
        get(GithubApiRequests.getUrl(coordinates.serverPath,
            urlSuffix,
            coordinates.repositoryPath.toString(),
            "actions",
            "runs",
            GithubApiUrlQueryBuilder.urlQuery {
                param("q", GithubApiSearchQueryBuilder.searchQuery {
                    qualifier("event", event)
                    qualifier("status", status)
                    qualifier("branch", branch)
                    qualifier("actor", actor)
                })
                param(pagination)
            }))

    @JvmStatic
    fun get(server: GithubServerPath, query: String, pagination: GithubRequestPagination? = null) =
        get(GithubApiRequests.getUrl(server, GithubApiRequests.Search.urlSuffix, GithubApiRequests.Search.Issues.urlSuffix,
            GithubApiUrlQueryBuilder.urlQuery {
                param("q", query)
                param(pagination)
            }))


    @JvmStatic
    fun get(url: String) = GithubApiRequest.Get.jsonSearchPage<GitHubWorkflowRun>(url)
        .withOperationName("search workflow runs")
}
