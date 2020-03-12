package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.api.Workflows
import org.github.otanikotani.workflow.GitHubRepositoryCoordinates
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.util.GithubAsyncUtil
import org.jetbrains.plugins.github.util.LazyCancellableBackgroundProcessValue
import java.util.concurrent.CompletableFuture
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class GitHubWorkflowRunDataProviderImpl(private val progressManager: ProgressManager,
                                        private val requestExecutor: GithubApiRequestExecutor,
                                        private val coordinates: GitHubRepositoryCoordinates,
                                        override val id: Long)
    : GitHubWorkflowRunDataProvider {

    private val requestsChangesEventDispatcher = EventDispatcher.create(GitHubWorkflowRunDataProvider.WorkflowRunChangedListener::class.java)

    private val workflowRunValue: LazyCancellableBackgroundProcessValue<GitHubWorkflowRun> = backingValue {
        requestExecutor.execute(it, Workflows.getWorkflowRun(coordinates, id))
    }

    override val workflowRunRequest by backgroundProcessValue(workflowRunValue)

    private val logValue: LazyCancellableBackgroundProcessValue<String> = backingValue {
       requestExecutor.execute(it, Workflows.getWorkflowLog("https://api.github.com/repos/otanikotani/single-action-run/actions/runs/54299372/logs"))
    }

    override val logRequest by backgroundProcessValue(logValue)

    @CalledInAwt
    override fun reloadWorkflowRun() {
        workflowRunValue.drop()
        requestsChangesEventDispatcher.multicaster.workflowRunChanged()
    }

    @CalledInAwt
    override fun reloadLog() {
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


    override fun addRequestsChangesListener(listener: GitHubWorkflowRunDataProvider.WorkflowRunChangedListener) =
        requestsChangesEventDispatcher.addListener(listener)

    override fun addRequestsChangesListener(disposable: Disposable, listener: GitHubWorkflowRunDataProvider.WorkflowRunChangedListener) =
        requestsChangesEventDispatcher.addListener(listener, disposable)

    override fun removeRequestsChangesListener(listener: GitHubWorkflowRunDataProvider.WorkflowRunChangedListener) =
        requestsChangesEventDispatcher.removeListener(listener)
}