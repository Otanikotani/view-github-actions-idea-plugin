package org.github.otanikotani.workflow.ui

import com.intellij.openapi.util.Key
import org.jetbrains.plugins.github.pullrequest.data.GHPRIdentifier
import org.jetbrains.plugins.github.pullrequest.ui.toolwindow.GHPRViewComponentController

interface GitHubWorkflowToolWindowTabComponentController {

    fun viewList(requestFocus: Boolean = true)

    fun refreshList()

    companion object {
        val KEY = Key.create<GitHubWorkflowToolWindowTabComponentController>("Github.PullRequests.Toolwindow.Controller")
    }
}