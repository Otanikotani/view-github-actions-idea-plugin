package org.github.otanikotani.workflow.action

import com.intellij.openapi.actionSystem.DataKey
import org.github.otanikotani.api.GitHubWorkflow
import org.github.otanikotani.workflow.GitHubWorkflowActionDataContext

object GitHubWorkflowActionKeys {
    @JvmStatic
    val ACTION_DATA_CONTEXT = DataKey.create<GitHubWorkflowActionDataContext>("org.github.otanikotani.workflow.action.datacontext")

    @JvmStatic
    internal val SELECTED_WORKFLOW = DataKey.create<GitHubWorkflow>("org.github.otanikotani.workflow.action.list.selected")
}