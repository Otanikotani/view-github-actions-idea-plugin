package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Disposer
import com.intellij.ui.CollectionListModel
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.GitHubWorkflow
import org.github.otanikotani.api.GitHubWorkflowRun
import org.github.otanikotani.api.Workflows
import org.github.otanikotani.workflow.GitHubRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.util.GithubApiPagesLoader
import org.jetbrains.plugins.github.pullrequest.data.GHListLoaderBase
import org.jetbrains.plugins.github.pullrequest.ui.SimpleEventListener
import kotlin.properties.Delegates

internal class GitHubWorkflowRunListLoaderImpl(progressManager: ProgressManager,
                                            private val requestExecutor: GithubApiRequestExecutor,
                                            private val gitHubRepositoryCoordinates: GitHubRepositoryCoordinates,
                                            private val listModel: CollectionListModel<GitHubWorkflowRun>)
    : GHListLoaderBase<GitHubWorkflowRun>(progressManager),
    GitHubWorkflowListLoader {

    override val hasLoadedItems: Boolean
        get() = !listModel.isEmpty

    var loaded: Boolean = false

    private val outdatedStateEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

    override var outdated: Boolean by Delegates.observable(false) { _, _, newValue ->
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

    override fun addOutdatedStateChangeListener(disposable: Disposable, listener: () -> Unit) =
        SimpleEventListener.addDisposableListener(outdatedStateEventDispatcher, disposable, listener)


    //This should not be needed, it is weird that originally it requires error != null to be able to load data
    override fun canLoadMore() = !loading && !loaded

    override fun doLoadMore(indicator: ProgressIndicator): List<GitHubWorkflowRun>? {
        val request = Workflows.getWorkflowRuns(gitHubRepositoryCoordinates)
        val result = requestExecutor.execute(indicator, request).workflow_runs
        loaded = true
        return result
    }
}