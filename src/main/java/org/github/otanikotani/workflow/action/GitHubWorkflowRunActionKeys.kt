package org.github.otanikotani.workflow.action

import com.intellij.openapi.actionSystem.DataKey
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.GitHubWorkflowRunSelectionContext

object GitHubWorkflowRunActionKeys {
    @JvmStatic
    val SELECTED_WORKFLOW_RUN = DataKey.create<GitHubWorkflowRun>("org.github.otanikotani.workflow.list.selected")

    @JvmStatic
    val ACTION_DATA_CONTEXT = DataKey.create<GitHubWorkflowRunSelectionContext>("org.github.otanikotani.workflowrun.action.datacontext")
}