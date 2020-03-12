package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.GitHubWorkflowRun
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.pullrequest.ui.SimpleEventListener
import kotlin.properties.Delegates

internal class GitHubWorkflowRunListSelectionHolder {

    @get:CalledInAwt
    @set:CalledInAwt
    var selection: GitHubWorkflowRun? by Delegates.observable<GitHubWorkflowRun?>(null) { _, _, _ ->
        selectionChangeEventDispatcher.multicaster.eventOccurred()
    }

    private val selectionChangeEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

    @CalledInAwt
    fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit) =
        SimpleEventListener.addDisposableListener(selectionChangeEventDispatcher, disposable, listener)
}