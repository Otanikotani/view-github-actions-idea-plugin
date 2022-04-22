package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.EventDispatcher
import com.intellij.util.concurrency.annotations.RequiresEdt
import org.github.otanikotani.api.Workflows
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.util.LazyCancellableBackgroundProcessValue
import java.io.IOException
import java.util.EventListener

class GitHubWorkflowRunDataProvider(
    private val progressManager: ProgressManager,
    private val requestExecutor: GithubApiRequestExecutor,
    val url: String,
) {

    private val runChangesEventDispatcher = EventDispatcher.create(WorkflowRunChangedListener::class.java)

    private val logValue: LazyCancellableBackgroundProcessValue<String> = backingValue {
        try {
            LOG.debug("Get workflow log for $url")
            val log = requestExecutor.execute(it, Workflows.getDownloadUrlForWorkflowLog(url))
            LOG.debug("Downloaded log of size ${log.length}")
            log
        } catch (ioe: IOException) {
            LOG.error(ioe)
            "Logs are unavailable - either the workflow run is not finished (currently GitHub API returns 404 for logs for unfinished runs)" +
                " or the url is incorrect. The log url: $url "
        }
    }

    @RequiresEdt
    fun reloadLog() {
        logValue.drop()
        runChangesEventDispatcher.multicaster.logChanged()
    }

    private fun <T> backingValue(supplier: (ProgressIndicator) -> T) =
        LazyCancellableBackgroundProcessValue.create(progressManager) {
            supplier(it)
        }

    fun addRunChangesListener(disposable: Disposable, listener: WorkflowRunChangedListener) =
        runChangesEventDispatcher.addListener(listener, disposable)

    interface WorkflowRunChangedListener : EventListener {
        fun logChanged() {}
    }

    companion object {
        private val LOG = Logger.getInstance("org.github.otanikotani")
    }
}