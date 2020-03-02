package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.jetbrains.annotations.CalledInAwt

internal interface GitHubWorkflowsListSelectionHolder {
    @get:CalledInAwt
    @set:CalledInAwt
    var selectionNumber: Long?

    @CalledInAwt
    fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit)
}