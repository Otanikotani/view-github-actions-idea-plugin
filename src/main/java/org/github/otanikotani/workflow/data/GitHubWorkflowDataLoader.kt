package org.github.otanikotani.workflow.data

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.EventDispatcher
import org.jetbrains.annotations.CalledInAwt
import java.util.*

class GitHubWorkflowDataLoader(private val dataProviderFactory: (String) -> GitHubWorkflowRunDataProvider) : Disposable {

    private var isDisposed = false
    private val cache = CacheBuilder.newBuilder()
        .removalListener<String, GitHubWorkflowRunDataProvider> {
            runInEdt { invalidationEventDispatcher.multicaster.providerChanged(it.key) }
        }
        .maximumSize(200)
        .build<String, GitHubWorkflowRunDataProvider>()

    private val invalidationEventDispatcher = EventDispatcher.create(DataInvalidatedListener::class.java)

    fun getDataProvider(url: String): GitHubWorkflowRunDataProvider {
        if (isDisposed) throw IllegalStateException("Already disposed")

        return cache.get(url) {
            dataProviderFactory(url)
        }
    }

    @CalledInAwt
    fun invalidateAllData() {
        LOG.debug("All cache invalidated")
        cache.invalidateAll()
    }

    private interface DataInvalidatedListener : EventListener {
        fun providerChanged(url: String)
    }

    fun addInvalidationListener(disposable: Disposable, listener: (String) -> Unit) =
        invalidationEventDispatcher.addListener(object : DataInvalidatedListener {
            override fun providerChanged(url: String) {
                listener(url)
            }
        }, disposable)

    override fun dispose() {
        LOG.debug("Disposing...")
        invalidateAllData()
        isDisposed = true
    }

    companion object {
        private val LOG = Logger.getInstance("org.github.otanikotani")
    }
}