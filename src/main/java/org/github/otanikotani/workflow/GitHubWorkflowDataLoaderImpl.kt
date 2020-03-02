package org.github.otanikotani.workflow

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.util.EventDispatcher
import org.jetbrains.annotations.CalledInAwt
import java.util.*

internal class GitHubWorkflowDataLoaderImpl(private val dataProviderFactory: (Long) -> GitHubWorkflowDataProvider)
    : GitHubWorkflowDataLoader {

    private var isDisposed = false
    private val cache = CacheBuilder.newBuilder()
        .removalListener<Long, GitHubWorkflowDataProvider> {
            runInEdt { invalidationEventDispatcher.multicaster.providerChanged(it.key) }
        }
        .maximumSize(5)
        .build<Long, GitHubWorkflowDataProvider>()

    private val invalidationEventDispatcher = EventDispatcher.create(DataInvalidatedListener::class.java)

    @CalledInAwt
    override fun invalidateAllData() {
        cache.invalidateAll()
    }

    @CalledInAwt
    override fun getDataProvider(number: Long): GitHubWorkflowDataProvider {
        if (isDisposed) throw IllegalStateException("Already disposed")

        return cache.get(number) {
            dataProviderFactory(number)
        }
    }

    @CalledInAwt
    override fun findDataProvider(number: Long): GitHubWorkflowDataProvider? = cache.getIfPresent(number)

    override fun addInvalidationListener(disposable: Disposable, listener: (Long) -> Unit) =
        invalidationEventDispatcher.addListener(object : DataInvalidatedListener {
            override fun providerChanged(pullRequestNumber: Long) {
                listener(pullRequestNumber)
            }
        }, disposable)

    override fun dispose() {
        invalidateAllData()
        isDisposed = true
    }

    private interface DataInvalidatedListener : EventListener {
        fun providerChanged(pullRequestNumber: Long)
    }
}