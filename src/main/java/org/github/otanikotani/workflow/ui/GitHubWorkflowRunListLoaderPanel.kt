package org.github.otanikotani.workflow.ui

import com.intellij.ide.actions.RefreshAction
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.progress.util.ProgressWindow
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.ui.StatusText
import com.intellij.vcs.log.ui.frame.ProgressStripe
import org.github.otanikotani.workflow.data.GitHubWorkflowRunListLoader
import org.jetbrains.plugins.github.ui.HtmlInfoPanel
import javax.swing.JComponent
import javax.swing.JPanel

internal class GitHubWorkflowRunListLoaderPanel(runListLoader: GitHubWorkflowRunListLoader,
                                                private val listReloadAction: RefreshAction,
                                                contentComponent: JComponent)
    : GitHubListLoaderPanel<GitHubWorkflowRunListLoader>(runListLoader, contentComponent), Disposable {

    private lateinit var progressStripe: ProgressStripe

    override fun createCenterPanel(content: JComponent): JPanel {
        val stripe = ProgressStripe(content, this,
            ProgressWindow.DEFAULT_PROGRESS_DIALOG_POSTPONE_TIME_MILLIS)
        progressStripe = stripe
        return stripe
    }

    override fun setLoading(isLoading: Boolean) {
        if (isLoading) progressStripe.startLoading() else progressStripe.stopLoading()
    }

    init {
        runListLoader.addOutdatedStateChangeListener(this) {
            updateInfoPanel()
        }
    }

    override fun displayEmptyStatus(emptyText: StatusText) {
        emptyText.text = "Nothing loaded. "
        emptyText.appendSecondaryText("Refresh", SimpleTextAttributes.LINK_PLAIN_ATTRIBUTES) {
            listLoader.reset()
        }
    }

    override fun updateInfoPanel() {
        super.updateInfoPanel()
        if (infoPanel.isEmpty && listLoader.outdated) {
            infoPanel.setInfo("<html><body>The list is outdated. <a href=''>Refresh</a></body></html>",
                HtmlInfoPanel.Severity.INFO) {
                ActionUtil.invokeAction(listReloadAction, this, ActionPlaces.UNKNOWN, it.inputEvent, null)
            }
        }
    }
}