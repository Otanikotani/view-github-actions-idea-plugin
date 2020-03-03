package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.jetbrains.annotations.CalledInAwt

internal interface GitHubWorkflowDataLoader : Disposable {
    @CalledInAwt
    fun getDataProvider(number: Long): GitHubWorkflowDataProvider

    @CalledInAwt
    fun findDataProvider(number: Long): GitHubWorkflowDataProvider?

    @CalledInAwt
    fun invalidateAllData()

    @CalledInAwt
    fun addInvalidationListener(disposable: Disposable, listener: (Long) -> Unit)
}