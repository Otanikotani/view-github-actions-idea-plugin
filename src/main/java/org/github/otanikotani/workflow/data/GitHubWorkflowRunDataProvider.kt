package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GitHubWorkflowRun
import org.jetbrains.annotations.CalledInAwt
import java.util.*
import java.util.concurrent.CompletableFuture

interface GitHubWorkflowRunDataProvider {
    val id: Long

    val workflowRunRequest: CompletableFuture<GitHubWorkflowRun>
    val logRequest: CompletableFuture<String>

    fun addRequestsChangesListener(listener: WorkflowRunChangedListener)
    fun addRequestsChangesListener(disposable: Disposable, listener: WorkflowRunChangedListener)
    fun removeRequestsChangesListener(listener: WorkflowRunChangedListener)

    @CalledInAwt
    fun reloadWorkflowRun()

    @CalledInAwt
    fun reloadLog()

    interface WorkflowRunChangedListener : EventListener {
        fun logChanged() {}
        fun workflowRunChanged() {}
    }
}