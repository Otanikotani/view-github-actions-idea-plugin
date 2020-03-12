package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.Workflows
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.util.GithubAsyncUtil
import org.jetbrains.plugins.github.util.LazyCancellableBackgroundProcessValue
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class GitHubWorkflowRunDataProvider(private val progressManager: ProgressManager,
                                    private val requestExecutor: GithubApiRequestExecutor,
                                    val url: String) {

    private val requestsChangesEventDispatcher = EventDispatcher.create(WorkflowRunChangedListener::class.java)

    private val logValue: LazyCancellableBackgroundProcessValue<String> = backingValue {
        requestExecutor.execute(it, Workflows.getWorkflowLog(url))
    }

    val logRequest by backgroundProcessValue(logValue)

    @CalledInAwt
    fun reloadLog() {
        requestsChangesEventDispatcher.multicaster.logChanged()
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


    fun addRequestsChangesListener(listener: WorkflowRunChangedListener) =
        requestsChangesEventDispatcher.addListener(listener)

    fun addRequestsChangesListener(disposable: Disposable, listener: WorkflowRunChangedListener) =
        requestsChangesEventDispatcher.addListener(listener, disposable)

    fun removeRequestsChangesListener(listener: WorkflowRunChangedListener) =
        requestsChangesEventDispatcher.removeListener(listener)

    interface WorkflowRunChangedListener : EventListener {
        fun logChanged() {}
    }
}