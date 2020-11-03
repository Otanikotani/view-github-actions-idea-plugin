package org.github.otanikotani.workflow.action

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.util.containers.map2Array
import org.github.otanikotani.workflow.GitHubWorkflowRunManager
import org.jetbrains.plugins.github.util.GHGitRepositoryMapping
import org.jetbrains.plugins.github.util.GHProjectRepositoriesManager
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates

class GitHubWorkflowShowActionGroup : ActionGroup("Open Workflow Tab", true), DumbAware {

    override fun update(e: AnActionEvent) {
        val data = getGithubRemotes(e.dataContext)
        e.presentation.isEnabledAndVisible = data != null && data.isNotEmpty()
    }

    override fun actionPerformed(e: AnActionEvent) {
        val firstRemote = getGithubRemotes(e.dataContext)?.firstOrNull() ?: return
        GitHubWorkflowShowAction(firstRemote).actionPerformed(e)
    }

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        e ?: return emptyArray()
        val remotes = getGithubRemotes(e.dataContext)?.takeIf { it.size > 1 } ?: return emptyArray()
        return remotes.map2Array(::GitHubWorkflowShowAction)
    }

    override fun canBePerformed(context: DataContext): Boolean {
        return getGithubRemotes(context)?.size == 1
    }

    private fun getGithubRemotes(dataContext: DataContext): List<GitRemoteUrlCoordinates>? {
        val project = dataContext.getData(CommonDataKeys.PROJECT) ?: return null
        return project.service<GHProjectRepositoriesManager>().knownRepositories.map(GHGitRepositoryMapping::gitRemote)
    }
}

class GitHubWorkflowShowAction(val remote: GitRemoteUrlCoordinates) : DumbAwareAction(remote.remote.name + ": " + remote.remote.firstUrl) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.dataContext.getData(CommonDataKeys.PROJECT) ?: return
        project.service<GitHubWorkflowRunManager>().showTab(remote)
    }
}
