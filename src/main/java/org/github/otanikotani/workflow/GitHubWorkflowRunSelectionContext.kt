package org.github.otanikotani.workflow

import com.intellij.openapi.diagnostic.Logger
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.data.GitHubWorkflowRunDataContext
import org.github.otanikotani.workflow.data.GitHubWorkflowRunDataProvider

class GitHubWorkflowRunSelectionContext internal constructor(private val dataContext: GitHubWorkflowRunDataContext,
                                                             private val selectionHolder: GitHubWorkflowRunListSelectionHolder) {

    fun resetAllData() {
        LOG.debug("resetAllData")
        dataContext.listLoader.reset()
        dataContext.dataLoader.invalidateAllData()
    }

    val workflowRun: GitHubWorkflowRun?
        get() = selectionHolder.selection

    val workflowRunDataProvider: GitHubWorkflowRunDataProvider?
        get() = workflowRun?.let { dataContext.dataLoader.getDataProvider(it.logs_url) }

    companion object {
        private val LOG = Logger.getInstance("org.github.otanikotani")
    }
}