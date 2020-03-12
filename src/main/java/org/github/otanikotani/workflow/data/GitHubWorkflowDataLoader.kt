package org.github.otanikotani.workflow.data

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.util.EventDispatcher
import org.jetbrains.annotations.CalledInAwt
import java.util.*

class GitHubWorkflowDataLoader(private val dataProviderFactory: (Long) -> GitHubWorkflowRunDataProvider) : Disposable {

    private var isDisposed = false
    private val cache = CacheBuilder.newBuilder()
        .removalListener<Long, GitHubWorkflowRunDataProvider> {
            runInEdt { invalidationEventDispatcher.multicaster.providerChanged(it.key) }
        }
        .maximumSize(200)
        .build<Long, GitHubWorkflowRunDataProvider>()

    private val invalidationEventDispatcher = EventDispatcher.create(DataInvalidatedListener::class.java)

    fun getDataProvider(id: Long): GitHubWorkflowRunDataProvider {

        if (isDisposed) throw IllegalStateException("Already disposed")

        return cache.get(id) {
            dataProviderFactory(id)
        }
    }

    fun findDataProvider(id: Long): GitHubWorkflowRunDataProvider? = cache.getIfPresent(id)


    @CalledInAwt
    fun invalidateAllData() {
        cache.invalidateAll()
    }

    private interface DataInvalidatedListener : EventListener {
        fun providerChanged(id: Long)
    }

    fun addInvalidationListener(disposable: Disposable, listener: (Long) -> Unit) =
        invalidationEventDispatcher.addListener(object : DataInvalidatedListener {
            override fun providerChanged(id: Long) {
                listener(id)
            }
        }, disposable)

    override fun dispose() {
        invalidateAllData()
        isDisposed = true
    }

}