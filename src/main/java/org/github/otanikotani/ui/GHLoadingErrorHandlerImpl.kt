// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.github.otanikotani.ui

import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action

class GHLoadingErrorHandlerImpl(private val resetRunnable: () -> Unit)
    : GHLoadingErrorHandler {

    override fun getActionForError(error: Throwable): Action? {
        return RetryAction()
    }

    private inner class RetryAction : AbstractAction("Retry") {
        override fun actionPerformed(e: ActionEvent?) {
            resetRunnable()
        }
    }
}