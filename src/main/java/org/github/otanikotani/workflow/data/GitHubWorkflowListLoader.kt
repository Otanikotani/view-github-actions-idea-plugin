package org.github.otanikotani.workflow.data

import com.intellij.openapi.Disposable
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.pullrequest.data.GHListLoader

internal interface GitHubWorkflowListLoader : GHListLoader {
    @get:CalledInAwt
    val outdated: Boolean

    @CalledInAwt
    fun addOutdatedStateChangeListener(disposable: Disposable, listener: () -> Unit)
}