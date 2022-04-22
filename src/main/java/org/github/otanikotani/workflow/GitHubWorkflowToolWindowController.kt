package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.concurrency.annotations.RequiresEdt
import org.github.otanikotani.workflow.action.GHWorkflowToolWindowFactory
import org.github.otanikotani.workflow.ui.GitHubWorkflowToolWindowTabController

@Service
internal class GitHubWorkflowToolWindowController(private val project: Project) : Disposable {
    @RequiresEdt
    fun isAvailable(): Boolean {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(GHWorkflowToolWindowFactory.ID) ?: return false
        return toolWindow.isAvailable
    }

    @RequiresEdt
    fun activate(onActivated: ((GitHubWorkflowToolWindowTabController) -> Unit)? = null) {
        LOG.debug("GitHubWorkflowToolWindowController::Activate")
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(GHWorkflowToolWindowFactory.ID) ?: return
        toolWindow.activate {
            val tabController = getTabController(toolWindow)
            if (tabController != null && onActivated != null) {
                LOG.debug("GitHubWorkflowToolWindowController::Activate - Found controller to activate")
                onActivated(tabController)
            }
        }
    }

    fun getTabController(): GitHubWorkflowToolWindowTabController? = ToolWindowManager.getInstance(project)
        .getToolWindow(GHWorkflowToolWindowFactory.ID)
        ?.let { getTabController(it) }

    private fun getTabController(toolWindow: ToolWindow): GitHubWorkflowToolWindowTabController? =
        toolWindow.contentManagerIfCreated?.selectedContent?.getUserData(GitHubWorkflowToolWindowTabController.KEY)

    override fun dispose() {
    }

    companion object {
        private val LOG = Logger.getInstance("org.github.otanikotani.workflow")
    }
}