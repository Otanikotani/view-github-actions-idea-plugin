package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.jetbrains.plugins.github.ui.util.SingleValueModel

internal class GitHubWorkflowRunSearchQueryHolder {
    private val delegate = SingleValueModel(GitHubWorkflowRunSearchQuery.parseFromString(""))

    var query: GitHubWorkflowRunSearchQuery
        get() = delegate.value
        set(value) {
            delegate.value = value
        }

    fun addQueryChangeListener(disposable: Disposable, listener: () -> Unit) =
        delegate.addValueChangedListener(disposable, listener)
}