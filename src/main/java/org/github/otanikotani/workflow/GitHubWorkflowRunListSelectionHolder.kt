package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.jetbrains.annotations.CalledInAwt

interface GitHubWorkflowRunListSelectionHolder {
    @get:CalledInAwt
    @set:CalledInAwt
    var selectionId: Long?

    @CalledInAwt
    fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit)
}