// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.github.otanikotani.ui

import org.jetbrains.annotations.CalledInAwt

interface GHWorkflowDataProvider {
    val number: Long

    @CalledInAwt
    fun reloadDetails()

    @CalledInAwt
    fun reloadChanges()

    @CalledInAwt
    fun reloadReviewThreads()

}