package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GitHubWorkflowRun
import org.jetbrains.annotations.CalledInAwt

interface GitHubWorkflowRunListSelectionHolder {
    @get:CalledInAwt
    @set:CalledInAwt
    var selection: GitHubWorkflowRun?

    @CalledInAwt
    fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit)
}