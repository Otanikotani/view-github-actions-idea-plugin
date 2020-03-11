package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GitHubWorkflow
import org.jetbrains.annotations.CalledInAwt

internal interface GitHubWorkflowDataLoader : Disposable {
    @CalledInAwt
    fun getWorkflow(url: String): GitHubWorkflow

    @CalledInAwt
    fun invalidateAllData()

    fun addInvalidationListener(disposable: Disposable, listener: (String) -> Unit)
}