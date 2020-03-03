package org.github.otanikotani.workflow

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionListModel
import org.github.otanikotani.api.GithubWorkflow
import org.github.otanikotani.workflow.data.GitHubWorkflowListLoaderImpl
import org.jetbrains.annotations.CalledInBackground
import org.jetbrains.plugins.github.api.GHRepositoryCoordinates
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

//        val dataLoader = GHWorkflowDataLoaderImpl {
//            GHWorkflowDataProviderImpl(project, ProgressManager.getInstance(), Git.getInstance(), requestExecutor, gitRemoteCoordinates,
//                repositoryCoordinates, it)
//        }
//        requestExecutor.addListener(dataLoader) {
//            dataLoader.invalidateAllData()
//        }

        return GitHubWorkflowDataContext(
            gitRemoteCoordinates,
            repositoryCoordinates,
            account,
            requestExecutor)
    }

    companion object {
        fun getInstance(project: Project) = project.service<GitHubWorkflowDataContextRepository>()
    }
}