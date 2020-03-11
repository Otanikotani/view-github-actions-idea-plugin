package org.github.otanikotani.workflow.data

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.util.EventDispatcher
import org.github.otanikotani.api.GitHubWorkflow
import org.github.otanikotani.api.Workflows
import org.jetbrains.annotations.CalledInAwt
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import java.util.*

internal class GitHubWorkflowDataLoaderImpl(private val requestExecutor: GithubApiRequestExecutor)
    : GitHubWorkflowDataLoader {

    private var isDisposed = false
    private val cache = CacheBuilder.newBuilder()
        .removalListener<String, GitHubWorkflow> {
            runInEdt { invalidationEventDispatcher.multicaster.providerChanged(it.key) }
        }
        .maximumSize(200)
        .build<String, GitHubWorkflow>()

    private val invalidationEventDispatcher = EventDispatcher.create(DataInvalidatedListener::class.java)

    @CalledInAwt
    override fun invalidateAllData() {
        cache.invalidateAll()
    }

    override fun addInvalidationListener(disposable: Disposable, listener: (String) -> Unit) =
        invalidationEventDispatcher.addListener(object : DataInvalidatedListener {
            override fun providerChanged(workflowUrl: String) {
                listener(workflowUrl)
            }
        }, disposable)

    override fun getWorkflow(url: String): GitHubWorkflow {
        if (isDisposed) throw IllegalStateException("Already disposed")
        return cache.get(url) {
            val request = Workflows.getWorkflowByUrl(url)
            requestExecutor.execute(request)
        }
    }

    override fun dispose() {
        invalidateAllData()
        isDisposed = true
    }

    private interface DataInvalidatedListener : EventListener {
        fun providerChanged(workflowUrl: String)
    }
}