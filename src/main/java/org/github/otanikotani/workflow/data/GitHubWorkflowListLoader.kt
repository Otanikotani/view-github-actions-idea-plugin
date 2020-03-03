package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.github.otanikotani.api.GithubWorkflow
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.pullrequest.data.GHListLoader
import java.util.concurrent.CompletableFuture

internal interface GitHubWorkflowListLoader : GHListLoader {
    @get:CalledInAwt
    val outdated: Boolean
    @get:CalledInAwt
    val filterNotEmpty: Boolean

    @CalledInAwt
    fun reloadData(request: CompletableFuture<out GithubWorkflow>)

    @CalledInAwt
    fun findData(number: Long): GithubWorkflow?

    @CalledInAwt
    fun resetFilter()

    @CalledInAwt
    fun addOutdatedStateChangeListener(disposable: Disposable, listener: () -> Unit)
}