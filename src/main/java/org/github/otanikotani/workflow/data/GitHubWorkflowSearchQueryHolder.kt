package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.github.otanikotani.workflow.data.GitHubWorkflowSearchQuery
import org.jetbrains.annotations.CalledInAwt

internal interface GitHubWorkflowSearchQueryHolder {
    @get:CalledInAwt
    @set:CalledInAwt
    var query: GitHubWorkflowSearchQuery

    fun addQueryChangeListener(disposable: Disposable, listener: () -> Unit)
}