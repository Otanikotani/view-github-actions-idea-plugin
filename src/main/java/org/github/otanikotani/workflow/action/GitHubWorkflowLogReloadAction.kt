package org.github.otanikotani.workflow.action

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.RefreshAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GitHubWorkflowLogReloadAction : RefreshAction("Refresh Workflow Log", null, AllIcons.Actions.Refresh) {
    override fun update(e: AnActionEvent) {
        val selection = e.getData(GitHubWorkflowRunActionKeys.ACTION_DATA_CONTEXT)?.workflowRunDataProvider
        e.presentation.isEnabled = selection != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.getRequiredData(GitHubWorkflowRunActionKeys.ACTION_DATA_CONTEXT).workflowRunDataProvider?.reloadLog()
    }
}