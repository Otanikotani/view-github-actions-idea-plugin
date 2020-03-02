package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import com.intellij.util.EventDispatcher
import org.jetbrains.plugins.github.pullrequest.ui.SimpleEventListener
import kotlin.properties.Delegates

internal class GitHubWorkflowsListSelectionHolderImpl : GitHubWorkflowsListSelectionHolder {

    override var selectionNumber: Long? by Delegates.observable<Long?>(null) { _, _, _ ->
        selectionChangeEventDispatcher.multicaster.eventOccurred()
    }

    private val selectionChangeEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

    override fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit) =
        SimpleEventListener.addDisposableListener(selectionChangeEventDispatcher, disposable, listener)
}