package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import org.jetbrains.annotations.CalledInAwt

internal interface GitHubWorkflowSearchQueryHolder {
    @get:CalledInAwt
    @set:CalledInAwt
    var query: GitHubWorkflowSearchQuery

    fun addQueryChangeListener(disposable: Disposable, listener: () -> Unit)
}