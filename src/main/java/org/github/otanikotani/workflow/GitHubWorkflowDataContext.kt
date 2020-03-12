package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.data.GitHubWorkflowDataLoader
import org.github.otanikotani.workflow.data.GitHubWorkflowRunListLoader
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates
import javax.swing.ListModel

class GitHubWorkflowDataContext(val gitRepositoryCoordinates: GitRemoteUrlCoordinates,
                                val gitHubRepositoryCoordinates: GitHubRepositoryCoordinates,
                                val listModel: ListModel<GitHubWorkflowRun>,
                                val dataLoader: GitHubWorkflowDataLoader,
                                val listLoader: GitHubWorkflowRunListLoader,
                                val account: GithubAccount,
                                val requestExecutor: GithubApiRequestExecutor) : Disposable {
    override fun dispose() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
