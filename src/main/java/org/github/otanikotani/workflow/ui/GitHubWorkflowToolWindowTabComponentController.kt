package org.github.otanikotani.workflow.ui

import com.intellij.openapi.util.Key
import org.jetbrains.plugins.github.pullrequest.data.GHPRIdentifier
import org.jetbrains.plugins.github.pullrequest.ui.toolwindow.GHPRViewComponentController

interface GitHubWorkflowToolWindowTabComponentController {

    val currentView: GitHubWorkflowToolWindowViewType

    fun createPullRequest(requestFocus: Boolean = true)

    fun resetNewPullRequestView()

    fun viewList(requestFocus: Boolean = true)

    fun refreshList()

    fun viewPullRequest(id: GHPRIdentifier, requestFocus: Boolean = true, onShown: ((GHPRViewComponentController?) -> Unit)? = null)

    fun openPullRequestTimeline(id: GHPRIdentifier, requestFocus: Boolean)

    fun openPullRequestDiff(id: GHPRIdentifier, requestFocus: Boolean)

    fun openNewPullRequestDiff(requestFocus: Boolean)

    companion object {
        val KEY = Key.create<GitHubWorkflowToolWindowTabComponentController>("Github.PullRequests.Toolwindow.Controller")
    }
}