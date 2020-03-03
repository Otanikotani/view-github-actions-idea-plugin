package org.github.otanikotani.workflow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import org.jetbrains.plugins.github.AbstractGithubUrlGroupingAction
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates

class GithubViewWorkflowsAction :
  AbstractGithubUrlGroupingAction("View Pull Requests", null, AllIcons.Vcs.Vendors.Github) {
  override fun actionPerformed(e: AnActionEvent, project: Project, repository: GitRepository, remote: GitRemote, remoteUrl: String) {
    val remoteCoordinates = GitRemoteUrlCoordinates(remoteUrl, remote, repository)
    project.service<GitHubWorkflowManager>().showTab(remoteCoordinates)
  }
}