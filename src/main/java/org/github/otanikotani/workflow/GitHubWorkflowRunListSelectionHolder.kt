package org.github.otanikotani.workflow

import com.intellij.collaboration.ui.SimpleEventListener
import com.intellij.openapi.Disposable
import com.intellij.util.EventDispatcher
import com.intellij.util.concurrency.annotations.RequiresEdt
import org.github.otanikotani.api.GitHubWorkflowRun
import kotlin.properties.Delegates

internal class GitHubWorkflowRunListSelectionHolder {

    @get:RequiresEdt
    @set:RequiresEdt
    var selection: GitHubWorkflowRun? by Delegates.observable(null) { _, _, _ ->
        selectionChangeEventDispatcher.multicaster.eventOccurred()
    }

    private val selectionChangeEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

    @RequiresEdt
    fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit) =
        SimpleEventListener.addDisposableListener(selectionChangeEventDispatcher, disposable, listener)
}