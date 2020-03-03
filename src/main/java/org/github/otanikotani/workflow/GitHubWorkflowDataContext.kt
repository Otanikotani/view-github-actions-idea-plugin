package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GithubWorkflow
import org.github.otanikotani.workflow.data.GitHubWorkflowListLoader
import org.jetbrains.plugins.github.api.GHRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.data.GHUser
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import javax.swing.ListModel

internal class GitHubWorkflowDataContext(val gitRepositoryCoordinates: GitRemoteUrlCoordinates,
                                         val gitHubRepositoryCoordinates: GitHubRepositoryCoordinates,
                                         val account: GithubAccount,
                                         val requestExecutor: GithubApiRequestExecutor) : Disposable {
    override fun dispose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
