package org.github.otanikotani.workflow

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.github.otanikotani.workflow.data.GitHubWorkflowDataLoaderImpl
import org.jetbrains.annotations.CalledInBackground
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import org.jetbrains.plugins.github.util.GithubUrlUtil
import java.io.IOException

@Service
internal class GitHubWorkflowDataContextRepository {
    @CalledInBackground
    @Throws(IOException::class)
    fun getContext(account: GithubAccount,
                   requestExecutor: GithubApiRequestExecutor, gitRemoteCoordinates: GitRemoteUrlCoordinates): GitHubWorkflowDataContext {
        val fullPath = GithubUrlUtil.getUserAndRepositoryFromRemoteUrl(gitRemoteCoordinates.url)
            ?: throw IllegalArgumentException(
                "Invalid GitHub Repository URL - ${gitRemoteCoordinates.url} is not a GitHub repository")

        val repositoryCoordinates = GitHubRepositoryCoordinates(account.server, fullPath)

        val githubWorkflowDataLoader = GitHubWorkflowDataLoaderImpl(requestExecutor)
        requestExecutor.addListener(githubWorkflowDataLoader) {
            githubWorkflowDataLoader.invalidateAllData()
        }

        return GitHubWorkflowDataContext(
            gitRemoteCoordinates,
            repositoryCoordinates,
            githubWorkflowDataLoader,
            account,
            requestExecutor)
    }

    companion object {
        fun getInstance(project: Project) = project.service<GitHubWorkflowDataContextRepository>()
    }
}