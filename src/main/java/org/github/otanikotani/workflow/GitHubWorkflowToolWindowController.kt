package org.github.otanikotani.workflow

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.concurrency.annotations.RequiresEdt
import org.github.otanikotani.workflow.action.GitHubToolWindowFactory
import org.github.otanikotani.workflow.ui.GitHubWorkflowToolWindowTabController
import org.jetbrains.plugins.github.util.GHProjectRepositoriesManager

@Service
internal class GitHubWorkflowToolWindowController(private val project: Project) : Disposable {
    private val repositoryManager = project.service<GHProjectRepositoriesManager>()

    init {
        repositoryManager.addRepositoryListChangedListener(this) {
            ToolWindowManager.getInstance(project).getToolWindow(GitHubToolWindowFactory.ID)?.isAvailable = isAvailable()
        }
    }

    @RequiresEdt
    fun isAvailable(): Boolean = repositoryManager.knownRepositories.isNotEmpty()

    @RequiresEdt
    fun activate(onActivated: ((GitHubWorkflowToolWindowTabController) -> Unit)? = null) {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(GitHubToolWindowFactory.ID) ?: return
        toolWindow.activate {
            val controller = toolWindow.contentManager.selectedContent?.getUserData(GitHubWorkflowToolWindowTabController.KEY)
            if (controller != null && onActivated != null) {
                onActivated(controller)
            }
        }
    }

    fun getTabController(): GitHubWorkflowToolWindowTabController? = ToolWindowManager.getInstance(project)
        .getToolWindow(GitHubToolWindowFactory.ID)
        ?.let { it.contentManagerIfCreated?.selectedContent?.getUserData(GitHubWorkflowToolWindowTabController.KEY) }

    override fun dispose() {
    }
}