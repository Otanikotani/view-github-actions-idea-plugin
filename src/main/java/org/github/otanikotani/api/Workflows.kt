package org.github.otanikotani.api

import org.github.otanikotani.workflow.GitHubRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiRequest.Get
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager
import org.jetbrains.plugins.github.api.GithubApiRequests
import org.jetbrains.plugins.github.api.data.request.GithubRequestPagination
import org.jetbrains.plugins.github.api.util.GithubApiSearchQueryBuilder
import org.jetbrains.plugins.github.api.util.GithubApiUrlQueryBuilder
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import java.util.*
import java.util.concurrent.CompletableFuture

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
    val conclusion: String?,
    val url: String,
    val html_url: String,
    val created_at: Date?,
    val updated_at: Date?,
    val jobs_url: String,
    val logs_url: String,
    val check_suite_url: String,
    val artifacts_url: String,
    val cancel_url: String,
    val rerun_url: String,
    val workflow_url: String,
    var workflowName: String?,
    val head_commit: GitHubHeadCommit
)

data class GitHubHeadCommit(
    val id: String,
    val message: String,
    val author: GitHubAuthor
)

data class GitHubAuthor(
    val name: String,
    val email: String
)


object Workflows : GithubApiRequests.Entity("/repos") {
    @JvmStatic
    fun getWorkflows(coordinates: GitHubRepositoryCoordinates): GithubApiRequest<GitHubWorkflows> {
        val serverUrl = coordinates.serverPath.toApiUrl()
        val repoPath = coordinates.repositoryPath
        val url = String.format("%s%s/%s/%s/actions/workflows",              serverUrl, urlSuffix, repoPath.owner, repoPath.repository)
        return Get.Json(url, GitHubWorkflows::class.java, null)
            .withOperationName("get workflows")
    }

    @JvmStatic
    fun getWorkflowByUrl(url: String): GithubApiRequest<GitHubWorkflow> {
        return Get.Json(url, GitHubWorkflow::class.java, null)
            .withOperationName("get workflow")
    }

    @JvmStatic
    fun getWorkflowLog(url: String): GithubApiRequest<String> {
        return WorkflowRunLogGet(url)
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
            "/${coordinates.repositoryPath}",
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
    fun getWorkflowRun(coordinates: GitHubRepositoryCoordinates, id: Long): GithubApiRequest<GitHubWorkflowRun> {
        val url = GithubApiRequests.getUrl(coordinates.serverPath,
            urlSuffix,
            "/${coordinates.repositoryPath}",
            "/actions",
            "/runs/$id")

        return GithubApiRequest.Get.json<GitHubWorkflowRun>(url)
            .withOperationName("search workflow run")
    }

    @JvmStatic
    fun get(url: String) = GithubApiRequest.Get.json<GitHubWorkflowRuns>(url)
        .withOperationName("search workflow runs")

}
