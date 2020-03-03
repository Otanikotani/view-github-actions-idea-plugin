package org.github.otanikotani.workflow

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionListModel
import org.github.otanikotani.api.GithubWorkflow
import org.github.otanikotani.workflow.data.GitHubWorkflowListLoaderImpl
import org.github.otanikotani.workflow.data.GitHubWorkflowSearchQueryHolderImpl
import org.jetbrains.annotations.CalledInBackground
import org.jetbrains.plugins.github.api.GHRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.data.GHUser
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.authentication.accounts.GithubAccountInformationProvider
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import org.jetbrains.plugins.github.util.GithubUrlUtil
import java.io.IOException

@Service
internal class GitHubWorkflowDataContextRepository(private val project: Project) {
    @CalledInBackground
    @Throws(IOException::class)
    fun getContext(indicator: ProgressIndicator,
                   account: GithubAccount, requestExecutor: GithubApiRequestExecutor,
                   gitRemoteCoordinates: GitRemoteUrlCoordinates): GitHubWorkflowDataContext {
        val fullPath = GithubUrlUtil.getUserAndRepositoryFromRemoteUrl(gitRemoteCoordinates.url)
            ?: throw IllegalArgumentException(
                "Invalid GitHub Repository URL - ${gitRemoteCoordinates.url} is not a GitHub repository")

        indicator.text = "Loading account information"
        val accountDetails = GithubAccountInformationProvider.getInstance().getInformation(requestExecutor, indicator, account)
        indicator.checkCanceled()

        indicator.text = "Loading repository information"

        val currentUser = GHUser(accountDetails.nodeId, accountDetails.login, accountDetails.htmlUrl, accountDetails.avatarUrl!!,
            accountDetails.name)
        val repositoryCoordinates = GHRepositoryCoordinates(account.server, fullPath)

//        val messageBus = MessageBusFactory.getInstance().createMessageBus(object : MessageBusOwner {
//            override fun isDisposed() = project.isDisposed
//
//            override fun createListener(descriptor: ListenerDescriptor) = throw UnsupportedOperationException()
//        })

        val securityService = GitHubWorkflowSecurityServiceImpl(currentUser)

        val listModel = CollectionListModel<GithubWorkflow>()
        val searchHolder = GitHubWorkflowSearchQueryHolderImpl()
        val listLoader = GitHubWorkflowListLoaderImpl(ProgressManager.getInstance(), requestExecutor, fullPath, account.server, listModel, searchHolder)

//        val dataLoader = GHWorkflowDataLoaderImpl {
//            GHWorkflowDataProviderImpl(project, ProgressManager.getInstance(), Git.getInstance(), requestExecutor, gitRemoteCoordinates,
//                repositoryCoordinates, it)
//        }
//        requestExecutor.addListener(dataLoader) {
//            dataLoader.invalidateAllData()
//        }

        return GitHubWorkflowDataContext(gitRemoteCoordinates, repositoryCoordinates, account,
            securityService, requestExecutor, listLoader, listModel, searchHolder)
    }

    companion object {
        fun getInstance(project: Project) = project.service<GitHubWorkflowDataContextRepository>()
    }
}