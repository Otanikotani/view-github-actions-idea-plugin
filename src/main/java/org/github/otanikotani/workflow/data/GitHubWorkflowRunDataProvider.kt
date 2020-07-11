package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.Workflows
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.util.GithubAsyncUtil
import org.jetbrains.plugins.github.util.LazyCancellableBackgroundProcessValue
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class GitHubWorkflowRunDataProvider(private val progressManager: ProgressManager,
                                    private val requestExecutor: GithubApiRequestExecutor,
                                    val url: String) {

    private val runChangesEventDispatcher = EventDispatcher.create(WorkflowRunChangedListener::class.java)

    private val logValue: LazyCancellableBackgroundProcessValue<String> = backingValue {
        try {
            LOG.debug("Get workflow log for $url")
            requestExecutor.execute(it, Workflows.getWorkflowLog(url))
        } catch (ioe: IOException) {
            "Logs are unavailable - either the workflow run is not finished (currently GitHub API returns 404 for logs for unfinished runs)" +
                " or the url is incorrect. The log url: $url "
        }
    }

    val logRequest by backgroundProcessValue(logValue)

    @CalledInAwt
    fun reloadLog() {
        runChangesEventDispatcher.multicaster.logChanged()
    }

    private fun <T> backingValue(supplier: (ProgressIndicator) -> T) =
        object : LazyCancellableBackgroundProcessValue<T>(progressManager) {
            override fun compute(indicator: ProgressIndicator) = supplier(indicator)
        }

    private fun <T> backgroundProcessValue(backingValue: LazyCancellableBackgroundProcessValue<T>): ReadOnlyProperty<Any?, CompletableFuture<T>> =
        object : ReadOnlyProperty<Any?, CompletableFuture<T>> {
            override fun getValue(thisRef: Any?, property: KProperty<*>) =
                GithubAsyncUtil.futureOfMutable { invokeAndWaitIfNeeded { backingValue.value } }
        }


    fun addRunChangesListener(disposable: Disposable, listener: WorkflowRunChangedListener) =
        runChangesEventDispatcher.addListener(listener, disposable)

    interface WorkflowRunChangedListener : EventListener {
        fun logChanged() {}
    }

    companion object {
        private val LOG = logger("org.github.otanikotani")
    }
}