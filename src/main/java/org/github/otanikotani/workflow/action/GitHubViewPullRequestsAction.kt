package org.github.otanikotani.workflow.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import org.github.otanikotani.workflow.GitHubWorkflowToolWindowController
import org.github.otanikotani.workflow.ui.GitHubWorkflowToolWindowViewType
import java.util.function.Supplier

class GitHubViewPullRequestsAction : DumbAwareAction(
    { "Open Workflows" },
    Supplier { null },
    AllIcons.Vcs.Vendors.Github
) {

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = isEnabledAndVisible(e)
    }

    private fun isEnabledAndVisible(e: AnActionEvent): Boolean {
        val project = e.project ?: return false
        return project.service<GitHubWorkflowToolWindowController>().isAvailable()
    }

    override fun actionPerformed(e: AnActionEvent) {
        e.project!!.service<GitHubWorkflowToolWindowController>().activate {
            it.initialView = GitHubWorkflowToolWindowViewType.LIST
            it.componentController?.viewList()
        }
    }
}