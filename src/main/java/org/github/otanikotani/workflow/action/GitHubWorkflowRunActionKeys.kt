package org.github.otanikotani.workflow.action

import com.intellij.openapi.actionSystem.DataKey
import org.github.otanikotani.api.GitHubWorkflow
import org.github.otanikotani.workflow.GitHubWorkflowSelectionContext

object GitHubWorkflowRunActionKeys {
    @JvmStatic
    val ACTION_DATA_CONTEXT = DataKey.create<GitHubWorkflowSelectionContext>("org.github.otanikotani.workflow.action.datacontext")

    @JvmStatic
    val RUN_ACTION_DATA_CONTEXT = DataKey.create<GitHubWorkflowSelectionContext>("org.github.otanikotani.workflowrun.action.datacontext")

    @JvmStatic
    internal val SELECTED_WORKFLOW_RUN = DataKey.create<GitHubWorkflow>("org.github.otanikotani.workflow.action.list.selected")
}