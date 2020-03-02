// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.github.otanikotani.ui

import com.intellij.openapi.Disposable
import com.intellij.util.EventDispatcher
import org.jetbrains.plugins.github.pullrequest.ui.SimpleEventListener
import kotlin.properties.Delegates

internal class GithubWorkflowsListSelectionHolderImpl : GithubWorkflowsListSelectionHolder {

  override var selectionNumber: Long? by Delegates.observable<Long?>(null) { _, _, _ ->
    selectionChangeEventDispatcher.multicaster.eventOccurred()
  }

  private val selectionChangeEventDispatcher = EventDispatcher.create(SimpleEventListener::class.java)

  override fun addSelectionChangeListener(disposable: Disposable, listener: () -> Unit) =
    SimpleEventListener.addDisposableListener(selectionChangeEventDispatcher, disposable, listener)
}