package org.github.otanikotani.workflow

import org.jetbrains.plugins.github.api.GHRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.data.GHUser
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates

interface GitHubWorkflowActionDataContext {
    val account: GithubAccount

    val securityService: GitHubWorkflowSecurityService
    val requestExecutor: GithubApiRequestExecutor

    val gitRepositoryCoordinates: GitRemoteUrlCoordinates
    val repositoryCoordinates: GHRepositoryCoordinates

    //    val avatarIconsProviderFactory: CachingGithubAvatarIconsProvider.Factory
    val currentUser: GHUser

    fun resetAllData()
}