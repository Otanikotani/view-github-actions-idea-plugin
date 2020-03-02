// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.github.otanikotani.ui

import com.intellij.openapi.Disposable
import org.jetbrains.annotations.CalledInAwt

internal interface GHWorkflowDataLoader : Disposable {
  @CalledInAwt
  fun getDataProvider(number: Long): GHWorkflowDataProvider

  @CalledInAwt
  fun findDataProvider(number: Long): GHWorkflowDataProvider?

  @CalledInAwt
  fun invalidateAllData()

  @CalledInAwt
  fun addInvalidationListener(disposable: Disposable, listener: (Long) -> Unit)
}