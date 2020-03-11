package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.github.otanikotani.workflow.data.GitHubWorkflowDataLoader
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates

internal class GitHubWorkflowDataContext(val gitRepositoryCoordinates: GitRemoteUrlCoordinates,
                                         val gitHubRepositoryCoordinates: GitHubRepositoryCoordinates,
                                         val gitHubWorkflowDataLoader: GitHubWorkflowDataLoader,
                                         val account: GithubAccount,
                                         val requestExecutor: GithubApiRequestExecutor) : Disposable {
    override fun dispose() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
