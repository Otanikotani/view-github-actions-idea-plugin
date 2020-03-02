package org.github.otanikotani.workflow

import javax.swing.Action

interface GitHubLoadingErrorHandler {
    fun getActionForError(error: Throwable): Action?
}
