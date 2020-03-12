package org.github.otanikotani.workflow

import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.data.GitHubWorkflowRunDataProvider

class GitHubWorkflowSelectionContext internal constructor(private val dataContext: GitHubWorkflowDataContext,
                                                          private val selectionHolder: GitHubWorkflowRunListSelectionHolder) {

    fun resetAllData() {
        dataContext.listLoader.reset()
        dataContext.dataLoader.invalidateAllData()
    }

    val workflowRun: GitHubWorkflowRun?
        get() = selectionHolder.selection

    val workflowRunDataProvider: GitHubWorkflowRunDataProvider?
        get() = workflowRun?.let { dataContext.dataLoader.getDataProvider(it.logs_url) }
}