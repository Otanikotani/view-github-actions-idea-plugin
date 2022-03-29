package org.github.otanikotani.workflow.action

import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.openapi.wm.impl.content.ToolWindowContentUi
import org.github.otanikotani.workflow.GitHubWorkflowToolWindowController
import org.github.otanikotani.workflow.data.GitHubWorkflowDataContextRepository
import org.github.otanikotani.workflow.ui.GitHubWorkflowToolWindowTabController
import org.github.otanikotani.workflow.ui.GitHubWorkflowToolWindowTabControllerImpl
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.util.GHProjectRepositoriesManager
import javax.swing.JPanel

class GitHubToolWindowFactory : ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) = with(toolWindow as ToolWindowEx) {
        component.putClientProperty(ToolWindowContentUi.HIDE_ID_LABEL, "true")
        with(contentManager) {
            addContent(factory.createContent(JPanel(null), null, false).apply {
                isCloseable = false
                setDisposer(Disposer.newDisposable("GitHubWorkflow tab disposable"))
            }.also {
                val authManager = GithubAuthenticationManager.getInstance()
                val repositoryManager = project.service<GHProjectRepositoriesManager>()
                val dataContextRepository = GitHubWorkflowDataContextRepository.getInstance(project)
                it.putUserData(
                    GitHubWorkflowToolWindowTabController.KEY,
                    GitHubWorkflowToolWindowTabControllerImpl(project, authManager, repositoryManager, dataContextRepository, it)
                )
            })
        }
    }

    override fun shouldBeAvailable(project: Project): Boolean =
        invokeAndWaitIfNeeded { project.service<GitHubWorkflowToolWindowController>().isAvailable() }

    companion object {
        const val ID = "Workflows"
    }
}