package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Disposer
import com.intellij.ui.CollectionListModel
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.api.Workflows
import org.github.otanikotani.workflow.GitHubRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.pullrequest.data.GHListLoader
import org.jetbrains.plugins.github.pullrequest.data.GHListLoaderBase
import org.jetbrains.plugins.github.pullrequest.ui.SimpleEventListener
import org.jetbrains.plugins.github.util.handleOnEdt
import java.util.concurrent.CompletableFuture
import kotlin.properties.Delegates

class GitHubWorkflowRunListLoader(progressManager: ProgressManager,
                                           private val requestExecutor: GithubApiRequestExecutor,
                                           private val gitHubRepositoryCoordinates: GitHubRepositoryCoordinates,
                                           private val listModel: CollectionListModel<GitHubWorkflowRun>)
    : GHListLoaderBase<GitHubWorkflowRun>(progressManager) {

    override val hasLoadedItems: Boolean
        get() = !listModel.isEmpty

    var loaded: Boolean = false

    private val outdatedStateEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

    var outdated: Boolean by Delegates.observable(false) { _, _, newValue ->
        outdatedStateEventDispatcher.multicaster.eventOccurred()
    }

    private var resetDisposable: Disposable

    init {
        requestExecutor.addListener(this) { reset() }

        resetDisposable = Disposer.newDisposable()
        Disposer.register(this, resetDisposable)
    }

    override fun handleResult(list: List<GitHubWorkflowRun>) {
        listModel.add(list)
    }

    override fun reset() {
        listModel.removeAll()
        loaded = false

        outdated = false

        Disposer.dispose(resetDisposable)
        resetDisposable = Disposer.newDisposable()
        Disposer.register(this, resetDisposable)

        loadMore()
    }

    fun addOutdatedStateChangeListener(disposable: Disposable, listener: () -> Unit) =
        SimpleEventListener.addDisposableListener(outdatedStateEventDispatcher, disposable, listener)


    //This should not be needed, it is weird that originally it requires error != null to be able to load data
    override fun canLoadMore() = !loading && !loaded

    override fun doLoadMore(indicator: ProgressIndicator): List<GitHubWorkflowRun>? {
        val request = Workflows.getWorkflowRuns(gitHubRepositoryCoordinates)
        val result = requestExecutor.execute(indicator, request).workflow_runs

        //This is quite slow - N+1 requests, but there are no simpler way to get it, at least now.
        result.parallelStream().forEach {
            it.workflowName = requestExecutor.execute(Workflows.getWorkflowByUrl(it.workflow_url)).name
        }
        loaded = true
        return result
    }

    fun reloadData(request: CompletableFuture<out GitHubWorkflowRun>) {
        request.handleOnEdt(resetDisposable) { result, error ->
            if (error == null && result != null) updateData(result)
        }
    }

    fun findData(id: Long) = listModel.items.find { it.id == id }

    private fun updateData(workflowRun: GitHubWorkflowRun) {
        val index = listModel.items.indexOfFirst { it.id == workflowRun.id }
        listModel.setElementAt(workflowRun, index)
    }

}