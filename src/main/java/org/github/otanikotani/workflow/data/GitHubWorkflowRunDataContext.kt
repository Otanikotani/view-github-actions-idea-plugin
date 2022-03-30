//package org.github.otanikotani.workflow.data
//
//import com.intellij.openapi.Disposable
//import org.github.otanikotani.api.GitHubWorkflowRun
//import org.github.otanikotani.workflow.GitHubRepositoryCoordinates
//import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
//import javax.swing.ListModel
//
//class GitHubWorkflowRunDataContext(val gitHubRepositoryCoordinates: GitHubRepositoryCoordinates,
//                                   val listModel: ListModel<GitHubWorkflowRun>,
//                                   val dataLoader: GitHubWorkflowDataLoader,
//                                   val listLoader: GitHubWorkflowRunListLoader,
//                                   val account: GithubAccount) : Disposable {
//    override fun dispose() {
//    }
//
//}
