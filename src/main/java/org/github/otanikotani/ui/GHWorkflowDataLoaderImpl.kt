// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.github.otanikotani.ui

import com.google.common.cache.CacheBuilder
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.util.EventDispatcher
import org.jetbrains.annotations.CalledInAwt
import java.util.*

internal class GHWorkflowDataLoaderImpl(private val dataProviderFactory: (Long) -> GHWorkflowDataProvider)
  : GHWorkflowDataLoader {

  private var isDisposed = false
  private val cache = CacheBuilder.newBuilder()
    .removalListener<Long, GHWorkflowDataProvider> {
      runInEdt { invalidationEventDispatcher.multicaster.providerChanged(it.key) }
    }
    .maximumSize(5)
    .build<Long, GHWorkflowDataProvider>()

  private val invalidationEventDispatcher = EventDispatcher.create(DataInvalidatedListener::class.java)

  @CalledInAwt
  override fun invalidateAllData() {
    cache.invalidateAll()
  }

  @CalledInAwt
  override fun getDataProvider(number: Long): GHWorkflowDataProvider {
    if (isDisposed) throw IllegalStateException("Already disposed")

    return cache.get(number) {
      dataProviderFactory(number)
    }
  }

  @CalledInAwt
  override fun findDataProvider(number: Long): GHWorkflowDataProvider? = cache.getIfPresent(number)

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