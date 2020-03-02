package org.github.otanikotani.ui

import org.jetbrains.plugins.github.api.GHRepositoryCoordinates
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.data.GHUser
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.pullrequest.avatars.CachingGithubAvatarIconsProvider
import org.jetbrains.plugins.github.util.GitRemoteUrlCoordinates

interface GHWorkflowActionDataContext {
    val account: GithubAccount

    val securityService: GHWorkflowSecurityService
    val requestExecutor: GithubApiRequestExecutor

    val gitRepositoryCoordinates: GitRemoteUrlCoordinates
    val repositoryCoordinates: GHRepositoryCoordinates

//    val avatarIconsProviderFactory: CachingGithubAvatarIconsProvider.Factory
    val currentUser: GHUser

    fun resetAllData()
}