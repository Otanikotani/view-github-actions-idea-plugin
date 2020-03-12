package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GitHubWorkflowRun
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.api.data.pullrequest.GHPullRequestShort
import org.jetbrains.plugins.github.pullrequest.data.GHListLoader
import java.util.concurrent.CompletableFuture

interface GitHubWorkflowRunListLoader : GHListLoader {
    @get:CalledInAwt
    val outdated: Boolean

    @CalledInAwt
    fun reloadData(request: CompletableFuture<out GitHubWorkflowRun>)

    @CalledInAwt
    fun findData(id: Long): GitHubWorkflowRun?

    @CalledInAwt
    fun addOutdatedStateChangeListener(disposable: Disposable, listener: () -> Unit)
}