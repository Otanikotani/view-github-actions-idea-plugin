package org.github.otanikotani.workflow

import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.workflow.data.GitHubWorkflowRunDataProvider

class GitHubWorkflowSelectionContext internal constructor(val dataContext: GitHubWorkflowDataContext,
                                                          val selectionHolder: GitHubWorkflowRunListSelectionHolder) {

    val account = dataContext.account

    val gitRepositoryCoordinates = dataContext.gitRepositoryCoordinates
    val gitHubRepositoryCoordinates = dataContext.gitHubRepositoryCoordinates
    val requestExecutor = dataContext.requestExecutor

    fun resetAllData() {
        dataContext.listLoader.reset()
        dataContext.dataLoader.invalidateAllData()
    }

    val workflowRunId: Long?
        get() = selectionHolder.selectionId

    val workflowRun: GitHubWorkflowRun?
        get() = workflowRunId?.let { dataContext.listLoader.findData(it) }

    val workflowRunDataProvider: GitHubWorkflowRunDataProvider?
        get() = workflowRunId?.let { dataContext.dataLoader.getDataProvider(it) }
}