package org.github.otanikotani.workflow

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionListModel
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.data.GitHubWorkflowDataLoader
import org.github.otanikotani.workflow.data.GitHubWorkflowRunDataProviderImpl
import org.github.otanikotani.workflow.data.GitHubWorkflowRunListLoaderImpl
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

        val githubWorkflowDataLoader = GitHubWorkflowDataLoader {
            GitHubWorkflowRunDataProviderImpl(ProgressManager.getInstance(), requestExecutor, repositoryCoordinates, it)
        }

        requestExecutor.addListener(githubWorkflowDataLoader) {
            githubWorkflowDataLoader.invalidateAllData()
        }

        val listModel = CollectionListModel<GitHubWorkflowRun>()
        val listLoader = GitHubWorkflowRunListLoaderImpl(ProgressManager.getInstance(), requestExecutor,
            repositoryCoordinates,
            listModel)

        return GitHubWorkflowDataContext(
            gitRemoteCoordinates,
            repositoryCoordinates,
            listModel,
            githubWorkflowDataLoader,
            listLoader,
            account,
            requestExecutor)
    }

    companion object {
        fun getInstance(project: Project) = project.service<GitHubWorkflowDataContextRepository>()
    }
}