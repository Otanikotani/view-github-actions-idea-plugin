package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Disposer
import com.intellij.ui.CollectionListModel
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.GithubWorkflow
import org.github.otanikotani.api.GithubWorkflows
import org.github.otanikotani.api.Workflows
import org.jetbrains.plugins.github.api.GHRepositoryPath
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.GithubServerPath
import org.jetbrains.plugins.github.pullrequest.data.GHListLoaderBase
import org.jetbrains.plugins.github.pullrequest.ui.SimpleEventListener
import org.jetbrains.plugins.github.util.handleOnEdt
import java.util.concurrent.CompletableFuture
import kotlin.properties.Delegates

internal class GitHubWorkflowListLoaderImpl(progressManager: ProgressManager,
                                            private val requestExecutor: GithubApiRequestExecutor,
                                            private val repoPath: GHRepositoryPath,
                                            private val serverPath: GithubServerPath,
                                            private val listModel: CollectionListModel<GithubWorkflow>,
                                            private val searchQueryHolder: GitHubWorkflowSearchQueryHolder)
    : GHListLoaderBase<GithubWorkflow>(progressManager),
    GitHubWorkflowListLoader {

    override val hasLoadedItems: Boolean
        get() = !listModel.isEmpty

    private val outdatedStateEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

    override var outdated: Boolean by Delegates.observable(false) { _, _, newValue ->
        outdatedStateEventDispatcher.multicaster.eventOccurred()
    }

    private var resetDisposable: Disposable

    init {
        requestExecutor.addListener(this) { reset() }
        searchQueryHolder.addQueryChangeListener(this) { reset() }

        resetDisposable = Disposer.newDisposable()
        Disposer.register(this, resetDisposable)
    }

    override val filterNotEmpty: Boolean
        get() = !searchQueryHolder.query.isEmpty()

    override fun resetFilter() {
        searchQueryHolder.query = GitHubWorkflowSearchQuery.parseFromString("")
    }

    override fun handleResult(list: List<GithubWorkflow>) {
        listModel.add(list)
    }

    override fun reset() {
        listModel.removeAll()

        outdated = false

        Disposer.dispose(resetDisposable)
        resetDisposable = Disposer.newDisposable()
        Disposer.register(this, resetDisposable)

        loadMore()
    }

    override fun reloadData(request: CompletableFuture<out GithubWorkflow>) {
        request.handleOnEdt(resetDisposable) { result, error ->
            if (error == null && result != null) updateData(result)
        }
    }

    override fun findData(id: Long) = listModel.items.find { it.id == id }

    private fun updateData(workflow: GithubWorkflow) {
        val index = listModel.items.indexOfFirst { it.id == workflow.id }
        listModel.setElementAt(workflow, index)
    }

    override fun addOutdatedStateChangeListener(disposable: Disposable, listener: () -> Unit) =
        SimpleEventListener.addDisposableListener(outdatedStateEventDispatcher, disposable, listener)


    //This should not be needed, it is weird that originally it requires error != null to be able to load data
    override fun canLoadMore() = !loading

    override fun doLoadMore(indicator: ProgressIndicator): List<GithubWorkflow>? {
        val request = Workflows().getWorkflows(serverPath, repoPath)
        return requestExecutor.execute(request)?.workflows
    }
}