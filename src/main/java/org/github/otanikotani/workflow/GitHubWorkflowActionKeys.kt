package org.github.otanikotani.workflow

import com.intellij.openapi.actionSystem.DataKey
import org.github.otanikotani.api.GithubWorkflow

object GitHubWorkflowActionKeys {
    @JvmStatic
    val ACTION_DATA_CONTEXT = DataKey.create<GitHubWorkflowActionDataContext>("org.jetbrains.plugins.github.pullrequest.datacontext")

    @JvmStatic
    internal val SELECTED_PULL_REQUEST = DataKey.create<GithubWorkflow>("org.jetbrains.plugins.github.pullrequest.list.selected")
}