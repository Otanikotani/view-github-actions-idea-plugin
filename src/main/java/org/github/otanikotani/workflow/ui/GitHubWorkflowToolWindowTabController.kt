package org.github.otanikotani.workflow.ui

import com.intellij.openapi.util.Key

interface GitHubWorkflowToolWindowTabController {

    var initialView: GitHubWorkflowToolWindowViewType

    val componentController: GitHubWorkflowToolWindowTabComponentController?

    fun canResetRemoteOrAccount(): Boolean
    fun resetRemoteAndAccount()

    companion object {
        //TODO: REPLACE
        val KEY = Key.create<GitHubWorkflowToolWindowTabController>("Github.PullRequests.ToolWindow.Tab.Controller")
    }
}