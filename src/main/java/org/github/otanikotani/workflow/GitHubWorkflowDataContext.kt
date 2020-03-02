package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GithubWorkflow
import org.jetbrains.plugins.github.api.GHRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import javax.swing.ListModel

internal class GitHubWorkflowDataContext(val gitRepositoryCoordinates: GitRemoteUrlCoordinates,
                                         val repositoryCoordinates: GHRepositoryCoordinates,
                                         val account: GithubAccount,
                                         val securityService: GitHubWorkflowSecurityService,
                                         val requestExecutor: GithubApiRequestExecutor,
                                         val listLoader: GitHubWorkflowListLoader,
                                         val listModel: ListModel<GithubWorkflow>,
                                         val searchHolder: GitHubWorkflowSearchQueryHolder) : Disposable {
    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
