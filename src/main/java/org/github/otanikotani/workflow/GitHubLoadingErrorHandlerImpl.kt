package org.github.otanikotani.workflow

import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.Action

class GitHubLoadingErrorHandlerImpl(private val resetRunnable: () -> Unit)
    : GitHubLoadingErrorHandler {

    override fun getActionForError(error: Throwable): Action? {
        return RetryAction()
    }

    private inner class RetryAction : AbstractAction("Retry") {
        override fun actionPerformed(e: ActionEvent?) {
            resetRunnable()
        }
    }
}