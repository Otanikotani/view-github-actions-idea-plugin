package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.GitHubWorkflowRun
import org.jetbrains.plugins.github.pullrequest.ui.SimpleEventListener
import kotlin.properties.Delegates

internal class GitHubWorkflowRunListSelectionHolderImpl : GitHubWorkflowRunListSelectionHolder {

    override var selection: GitHubWorkflowRun? by Delegates.observable<GitHubWorkflowRun?>(null) { _, _, _ ->
        selectionChangeEventDispatcher.multicaster.eventOccurred()
    }

    private val selectionChangeEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

    override fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit) =
        SimpleEventListener.addDisposableListener(selectionChangeEventDispatcher, disposable, listener)
}